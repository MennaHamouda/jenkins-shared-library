package org.devops

class Ansible implements Serializable {

    def steps

    Ansible(steps) {
        this.steps = steps
    }

    def configure() {
        steps.sh '''
        

            chmod 400 "$SSH_KEY"

            ansible-playbook \
              -i ansible/inventory/inventory.ini \
              --private-key "$SSH_KEY" \
              ansible/playbook.yml
        '''
    }
}