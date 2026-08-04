package org.devops

class Ansible implements Serializable {

    def steps

    Ansible(steps) {
        this.steps = steps
    }

    def configure(String sshKey) {

        if (!steps.fileExists('terraform-output.json')) {
            steps.error('terraform-output.json was not found. Ensure the Terraform deploy stage completed successfully.')
        }

        def tf = steps.readJSON(file: 'terraform-output.json')
        def inventoryPath = 'ansible/inventory/inventory.ini'
        def inventoryContent = """[bastion]
${tf.bastion_public_ip.value}

[master]
${tf.master_private_ip.value}

[workers]
${tf.worker_private_ips.value.join('\n')}

[kubernetes:children]
master
workers

[all:vars]
ansible_user=ubuntu
ansible_ssh_common_args='-o ProxyJump=ubuntu@${tf.bastion_public_ip.value}'
"""

        steps.writeFile(file: inventoryPath, text: inventoryContent)

        steps.sh """
        ansible-playbook \
          -i ${inventoryPath} \
          --private-key ${sshKey} \
          ansible/playbook.yml
        """

    }

}