package org.devops

class Ansible implements Serializable {

    def steps

    Ansible(steps) {
        this.steps = steps
    }

    def fetchArtifact(String projectName, String targetDir = 'ansible/inventory') {
        steps.sh "echo 'Fetching archived inventory from upstream job: ${projectName}...'"
        steps.copyArtifacts(
            projectName: projectName,
            selector: steps.lastSuccessful(),
            filter: 'inventory.ini',
            target: targetDir,
            flatten: true
        )
    }

    def configure() {
        steps.sh '''
            mkdir -p "$HOME/.ssh"
            cp "$SSH_KEY" "$HOME/.ssh/ansible-key.pem"
            chmod 600 "$HOME/.ssh/ansible-key.pem"

            ansible-playbook \
                -i ansible/inventory/inventory.ini \
                ansible/playbook.yml

            rm -f "$HOME/.ssh/ansible-key.pem"
        '''
    }
}
