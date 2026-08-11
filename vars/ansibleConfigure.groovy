import org.devops.Ansible

def call() {
    Ansible ansible = new Ansible(this)

    withCredentials([
        file(credentialsId: 'ansible-key', variable: 'SSH_KEY')
    ]) {
        ansible.configure()
    }
}