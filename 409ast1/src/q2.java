
public class q2 {
	public static Node root;
	
	public static void main(String[] args) {
		
	}

}

class Node {
    float value;
    Node parent;
    Node left;
    Node right;
 
    Node(float value) {
        this.value = value;
        parent = null;
        right = null;
        left = null;
    }
}
