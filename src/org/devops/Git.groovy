package org.devops

class Git implements Serializable{

    def steps

    Git(steps){
        this.steps = steps
    }

    def checkoutCode(){

        steps.checkout scm

    }

}