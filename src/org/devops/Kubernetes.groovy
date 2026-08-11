package org.devops

class Kubernetes implements Serializable {

    def steps

    Kubernetes(steps) {
        this.steps = steps
    }

    def fetchArtifact(String projectName, String targetDir = 'manifest-kustomize') {
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
        def deploy(String environment, String bastionIp, String masterIp) {
        steps.withCredentials([steps.file(credentialsId: 'ansible-key', variable: 'SSH_KEY')]) {

            steps.sh """
                chmod 400 "\$SSH_KEY"

                # Kill any stale SSH tunnels on local port 6443
                pkill -f '6443:' || true

                # Tunnel directly to localhost on the Master host via ProxyCommand
                ssh -f -N -L 6443:127.0.0.1:6443 \
                    -o StrictHostKeyChecking=no \
                    -o ProxyCommand="ssh -o StrictHostKeyChecking=no -i \"\$SSH_KEY\" -W %h:%p ubuntu@${bastionIp}" \
                    -i "\$SSH_KEY" ubuntu@${masterIp}

                ssh -o StrictHostKeyChecking=no -i "\$SSH_KEY" \
                    -o ProxyCommand="ssh -o StrictHostKeyChecking=no -i \"\$SSH_KEY\" -W %h:%p ubuntu@${bastionIp}" \
                    ubuntu@${masterIp} \
                    "cat ~/.kube/config 2>/dev/null || sudo cat /etc/kubernetes/admin.conf" > kubeconfig.tmp

                chmod 600 kubeconfig.tmp

                export KUBECONFIG=kubeconfig.tmp
                KUBECTL="kubectl --server=https://127.0.0.1:6443 --insecure-skip-tls-verify"

                # Clean up legacy ingress/namespace to prevent host conflicts
                \$KUBECTL delete ingress weather-app-ingress -n weather-app --ignore-not-found || true

                \$KUBECTL diff -k manifest-kustomize/overlays/${environment} || true
                \$KUBECTL apply -k manifest-kustomize/overlays/${environment}
                \$KUBECTL get pods -n ${environment}

                rm -f kubeconfig.tmp
                pkill -f '6443:' || true
            """

        }
    }


}