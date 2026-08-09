package org.devops

class Ansible implements Serializable {

    def steps

    Ansible(steps) {
        this.steps = steps
    }

    def configure(String sshKey) {

        steps.sh """
        ansible-playbook \
          -i ansible/inventory.ini \
          --private-key ${sshKey} \
          ansible/playbook.yml
        """

    }

}