import org.devops.Docker

def call(){

    Docker docker = new Docker(this)

    docker.buildImage("mennatallah2001/weather-app:latest")

}