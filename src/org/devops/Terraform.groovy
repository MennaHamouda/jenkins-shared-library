package org.devops

class Terraform implements Serializable {

    def steps

    Terraform(steps){
        this.steps = steps
    }

    def init(String directory){

        steps.dir(directory){
            steps.sh "terraform init"
        }

    }

    def validate(String directory){

        steps.dir(directory){
            steps.sh "terraform validate"
        }

    }

    def plan(String directory){

        steps.dir(directory){
            steps.sh "terraform plan -out=tfplan"
        }

    }

    def apply(String directory){

        steps.dir(directory){
            steps.sh "terraform apply -auto-approve tfplan"
        }

    }

   

}