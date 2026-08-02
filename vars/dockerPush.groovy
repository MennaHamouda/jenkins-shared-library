import org.devops.Docker

def call(){

    Docker docker = new Docker(this)

    withCredentials([usernamePassword(
        credentialsId: 'dockerHub',
        usernameVariable: 'USER',
        passwordVariable: 'PASS'
    )]){

        sh "echo \$PASS | docker login -u \$USER --password-stdin"

        docker.pushImage("weather-app:latest")

    }

}