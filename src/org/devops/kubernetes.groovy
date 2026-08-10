package org.devops

class Kubernetes implements Serializable {

    def steps

    Kubernetes(steps) {
        this.steps = steps
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