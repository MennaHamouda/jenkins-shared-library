import org.devops.Node

def call(){

    Node node = new Node(this)

    stage("Install Dependencies"){

        node.installDependencies()

    }

}