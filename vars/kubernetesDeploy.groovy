import org.devops.Kubernetes

def call() {

    Kubernetes kubernetes = new Kubernetes(this)

    def envVars = kubernetes.fetchArtifact('weather-app/infrastructure', 'manifest-kustomize')
       
    def environment

    if (env.BRANCH_NAME == 'dev') {
        environment = 'dev'
    } else if (env.BRANCH_NAME == 'main') {
        environment = 'prod'
    } else {
        error "Unsupported branch: ${env.BRANCH_NAME}"
    }

    kubernetes.deploy(environment, envVars.bastionIp, envVars.masterIp)
}
