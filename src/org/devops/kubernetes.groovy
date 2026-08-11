package org.devops

class Kubernetes implements Serializable {

    def steps

    Kubernetes(steps) {
        this.steps = steps
    }

    def fetchArtifact(String projectName, String targetDir = 'ansible/inventory') {
        steps.sh "echo 'Fetching archived inventory from upstream job: ${projectName}...'"
        steps.copyArtifacts(
            projectName: projectName,
            selector: steps.lastSuccessful(),
            filter: 'inventory.ini',
            target: targetDir,
            flatten: true
        )

        def bastionIp = steps.sh(
            script: "awk '/\\[bastion\\]/{found=1;next} found && /ansible_host=/{print; exit}' ${targetDir}/inventory.ini | grep -oP 'ansible_host=\\K[^ ]+'",
            returnStdout: true
        ).trim()

        def masterIp = steps.sh(
            script: "awk '/\\[master\\]/{found=1;next} found && /ansible_host=/{print; exit}' ${targetDir}/inventory.ini | grep -oP 'ansible_host=\\K[^ ]+'",
            returnStdout: true
        ).trim()

        steps.sh "echo 'Bastion IP: ${bastionIp}'"
        steps.sh "echo 'Master IP: ${masterIp}'"

        return [bastionIp: bastionIp, masterIp: masterIp]
    }

    def diff(String environment) {

        steps.sh """
            kubectl diff -k manifest-kustomize/overlays/${environment} || true
        """

    }

    def deploy(String environment, String bastionIp, String masterIp) {
        steps.withCredentials([steps.sshUserPrivateKey(credentialsId: 'ansible-key', keyFileVariable: 'SSH_KEY')]) {

            steps.sh """
                chmod 400 "\$SSH_KEY"

                ssh -f -N -L 6443:${masterIp}:6443 -o StrictHostKeyChecking=no -i "\$SSH_KEY" ubuntu@${bastionIp}

                ssh -o StrictHostKeyChecking=no -i "\$SSH_KEY" \
                    -o ProxyCommand="ssh -o StrictHostKeyChecking=no -i \"\$SSH_KEY\" -W %h:%p ubuntu@${bastionIp}" \
                    ubuntu@${masterIp} \
                    "cat ~/.kube/config 2>/dev/null || sudo cat /etc/kubernetes/admin.conf" > kubeconfig.tmp

                chmod 600 kubeconfig.tmp

                KUBECONFIG=kubeconfig.tmp kubectl apply -k manifest-kustomize/overlays/${environment} --server=https://127.0.0.1:6443 --insecure-skip-tls-verify

                KUBECONFIG=kubeconfig.tmp kubectl get pods -n ${environment}

                rm -f kubeconfig.tmp
                pkill -f 'ssh -f -N -L 6443:${masterIp}' || true

            """

        }
    }

}