package org.devops

class Ansible implements Serializable {

    def steps

    Ansible(steps) {
        this.steps = steps
    }

    def configure() {
        steps.sh '''
        

            chmod 400 terraform/weather-key.pem

            ansible-playbook \
                -i ansible/inventory/inventory.ini \
                --private-key terraform/weather-key.pem \
                ansible/playbook.yml
        '''
    }
}
