package org.devops

class Docker implements Serializable{

    def steps

    Docker(steps){
        this.steps = steps
    }

    def buildImage(String image){

        steps.sh """
            docker build -t ${image} .
        """

    }

    def pushImage(String image){

        steps.sh """
            docker push ${image}
        """

    }

}