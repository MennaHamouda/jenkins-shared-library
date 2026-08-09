package org.devops

class Ansible implements Serializable {

    def steps

    Ansible(steps) {
        this.steps = steps
    }

    def configure() {

        steps.sh """
        ansible-playbook \
          -i ansible/inventory/inventory.ini \
          --private-key ${sshKey} \
          ansible/playbook.yml
        """

    }

}