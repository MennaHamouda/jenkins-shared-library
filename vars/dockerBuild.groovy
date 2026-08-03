import org.devops.Config
import org.devops.Docker

def call(){

    Docker docker = new Docker(this)

    docker.buildImage("${Config.USERNAME}/weather-app:latest")

}