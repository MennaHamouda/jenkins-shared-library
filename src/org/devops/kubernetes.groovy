package org.devops

class Kubernetes implements Serializable {

    def steps

    Kubernetes(steps) {
        this.steps = steps
    }

    def fetchKubeConfig(Map config = [:]) {
        def jumpHost = config.jumpHost
        def jumpUser = config.jumpUser
        def masterHost = config.masterHost
        def masterUser = config.masterUser
        def remoteKubeConfigPath = config.remoteKubeConfigPath
        def localKubeConfigPath = config.localKubeConfigPath

        steps.sh """
            mkdir -p \"${steps.env.WORKSPACE}/.ssh\"
            mkdir -p \"${steps.env.WORKSPACE}/.kube\"
            cp \"\$SSH_KEY\" \"${steps.env.WORKSPACE}/.ssh/kube-ssh-key.pem\"
            chmod 600 \"${steps.env.WORKSPACE}/.ssh/kube-ssh-key.pem\"

            ssh -o StrictHostKeyChecking=no -i \"${steps.env.WORKSPACE}/.ssh/kube-ssh-key.pem\" \
                -J \"${jumpUser}@${jumpHost}\" \
                \"${masterUser}@${masterHost}\" \
                cat \"${remoteKubeConfigPath}\" > \"${localKubeConfigPath}\"

            chmod 600 \"${localKubeConfigPath}\"
            rm -f \"${steps.env.WORKSPACE}/.ssh/kube-ssh-key.pem\"
        """
    }

    def diff(String environment) {

        steps.sh """
            kubectl diff -k manifest-kustomize/overlays/${environment} || true
        """

    }

    def deploy(String environment) {

        steps.sh """
            kubectl apply -k manifest-kustomize/overlays/${environment}
        """

    }

    def getPods(String environment) {

        steps.sh """
            kubectl get pods -n ${environment}
        """

    }

}