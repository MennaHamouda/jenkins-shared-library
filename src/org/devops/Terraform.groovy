package org.devops

class Terraform implements Serializable {

    def steps

    Terraform(steps) {
        this.steps = steps
    }

    def init(String directory) {

        steps.dir(directory) {
            steps.sh """
                terraform init \
                -migrate-state \
                -force-copy \
                -input=false
            """
        }
    }

    def validate(String directory) {

        steps.dir(directory) {
            steps.sh "terraform validate"
        }
    }

    def plan(String directory) {

        steps.dir(directory) {
            steps.sh "terraform plan -var-file=terraform.tfvars -out=tfplan"
        }
    }

    def apply(String directory) {

        steps.dir(directory) {
            steps.sh "terraform apply -var-file=terraform.tfvars tfplan"
        }
    }

    def output(String directory) {

        return steps.dir(directory) {
            return steps.sh(
                script: "terraform output -json",
                returnStdout: true
            ).trim()
        }
    }
}