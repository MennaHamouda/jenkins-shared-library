import org.devops.Ansible

def call() {

    Ansible ansible = new Ansible(this)

withCredentials([
    file(credentialsId: 'weather-key', variable: 'SSH_KEY')
]) {

    ansible.configure(env.SSH_KEY)

}
}