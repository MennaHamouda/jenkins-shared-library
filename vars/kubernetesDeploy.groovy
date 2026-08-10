import org.devops.Kubernetes

def call() {

    Kubernetes kubernetes = new Kubernetes(this)

    withCredentials([
        file(
            credentialsId: 'kubeconfig',
            variable: 'KUBECONFIG'
        )
    ]) {

        def environment

        if (env.BRANCH_NAME == 'dev') {

            environment = 'dev'

        } else if (env.BRANCH_NAME == 'main') {

            environment = 'prod'

        } else {

            error "Unsupported branch: ${env.BRANCH_NAME}"

        }

        kubernetes.diff(environment)

        kubernetes.deploy(environment)

        kubernetes.getPods(environment)
    }
}