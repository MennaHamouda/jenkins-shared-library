package org.devops

class Node implements Serializable {

    def steps

    Node(steps){
        this.steps = steps
    }

    def installDependencies(){

        steps.sh "npm install"

    }

    def runTests(){

        steps.sh "npm test"

    }

}