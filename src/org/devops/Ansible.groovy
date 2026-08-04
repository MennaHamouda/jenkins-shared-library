package org.devops

import groovy.text.SimpleTemplateEngine

class Ansible implements Serializable {

    def steps

    Ansible(steps) {
        this.steps = steps
    }

    def configure(String sshKey) {
        def tf = steps.readJSON(file: 'terraform-output.json')
        def template = steps.readFile(file: 'ansible/inventory/inventory.ini.tpl')

        def binding = [
            bastion_public_ip: tf.bastion_public_ip.value,
            master_private_ip: tf.master_private_ip.value,
            worker_private_ips: tf.worker_private_ips.value.join('\n'),
            ansible_user: 'ubuntu',
            bastion_ssh_proxy: "ubuntu@${tf.bastion_public_ip.value}"
        ]

        def renderedInventory = new SimpleTemplateEngine()
            .createTemplate(template)
            .make(binding)
            .toString()

        steps.writeFile(file: 'ansible/inventory/inventory.ini', text: renderedInventory)

        steps.sh """
        ansible-playbook \
          -i ansible/inventory/inventory.ini \
          --private-key ${sshKey} \
          ansible/playbook.yml
        """
    }

}