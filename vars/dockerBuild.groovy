import org.devops.Docker

def call(){

    Docker docker = new Docker(this)

    docker.buildImage("weather-app:latest")

}