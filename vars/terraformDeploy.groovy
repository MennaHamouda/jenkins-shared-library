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
        
        def outputs = terraform.output("terraform")

        steps.writeFile(
        file: "terraform-output.json",
        text: outputs
        )

        terraform.apply("terraform")

    }
}