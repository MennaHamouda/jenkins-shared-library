import org.devops.Terraform

def call() {

    Terraform terraform = new Terraform(this)

    withCredentials([
        usernamePassword(
            credentialsId: 'aws-creds',
            usernameVariable: 'AWS_ACCESS_KEY_ID',
            passwordVariable: 'AWS_SECRET_ACCESS_KEY'
        )
    ]) {

        terraform.init("terraform")
        terraform.validate("terraform")
        terraform.plan("terraform")
        terraform.apply("terraform")
        terraform.archive("ansible/inventory")
    }
}

