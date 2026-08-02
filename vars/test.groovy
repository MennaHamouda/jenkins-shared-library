import org.devops.Node

def call(){

    Node node = new Node(this)

    stage("Run Tests"){

        node.runTests()

    }

}