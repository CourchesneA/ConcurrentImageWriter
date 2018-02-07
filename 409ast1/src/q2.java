import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

/**
 * We can change the tree depth from the static final variable below.
 * The algorithm as a tendency to reduce the size of the tree even if
 * there is more chance of creating a node than deleting at each iteration.
 * This is because when we delete a note, we delete a whole subtree.
 * Moreover, the check for deletion is made before
 * @author Anthony
 *
 */
public class q2 {
	public static final float maxNodeValue = 100f;
	public static final float minNodeValue = 0f;
	public static final long threadsRunTime = 5000;
	public static final int treeDepth = 6; //+1, root is depth 0
	public static Node root;
	public static Random rnd = new Random();
	
	public static void main(String[] args) {
		
		//First construct the initial tree
		Thread constructorThread = new Thread(new TreeConstructor());
		constructorThread.start();
		try {
			constructorThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Start 2 readers thread
		TreeReader2 reader1 = new TreeReader2();
		TreeReader2 reader2 = new TreeReader2();
		Thread readerThread1 = new Thread(reader1);
		Thread readerThread2 = new Thread(reader2);
		Thread writerThread = new Thread(new TreeWriter());
		
		readerThread1.start();
		readerThread2.start();
		writerThread.start();
		
		//Start writer thread -> should join with the two reading thread and output their string

		
		try {
			readerThread1.join();
			readerThread2.join();
			writerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Thead A: ");
		System.out.println(reader1.getString());
		System.out.println("Thead B: ");
		System.out.println(reader2.getString());
		
	}
	
	static class TreeConstructor implements Runnable{
		@Override
		public void run() {
			root = new Node((maxNodeValue-minNodeValue)/2+minNodeValue);	//50
			addChild(root,minNodeValue, maxNodeValue, 0);
		}
		
		void addChild(Node node, float min, float max, int depth) {
			if(depth >= treeDepth) return;
			Node left = new Node(getRandomBetween(min, node.value));  Node right = new Node(getRandomBetween(node.value, max));
			left.parent.set(node);  right.parent.set(node);
			node.left.set(left);  node.right.set(right);
			
			addChild(left,min, node.value, depth+1); addChild(right, node.value, max,  depth+1);
		}
		
	}
	/*
	static class TreeReader implements Runnable{
		private String record = "";
		
		@Override
		public void run() {
			// Record in-order sequence a space separated string
			
			
			long startTime = System.currentTimeMillis();
			
			while(System.currentTimeMillis()-startTime < threadsRunTime) {	//This may exceed 5 sec but it should not matter too much, we will terminate after the current tree traversal
				Node currentNode = root;
				Node lastNode = null;
				//This algorithm to read a binary tree was taken from https://stackoverflow.com/questions/10371848/how-to-do-in-order-traversal-of-a-bst-without-recursion-or-stack-but-using-paren
				while (currentNode != null)
			    {
			        if (lastNode == currentNode.parent.get())
			        {
			            if (currentNode.left.get() != null)
			            {
			                lastNode = currentNode;
			                currentNode = currentNode.left.get();
			                trySleep(rnd.nextInt(15)+5);
			                continue;
			            }
			            else
			                lastNode = null;
			        }
			        if (lastNode == currentNode.left.get())	//We can't be sure that we will still have the same ref to childs so use value instaad for comparison
			        {
			        	record+=" "+currentNode.value;
			        	trySleep(rnd.nextInt(15)+5);

			            if (currentNode.right.get() != null)
			            {
			                lastNode = currentNode;
			                currentNode = currentNode.right.get();
			                trySleep(rnd.nextInt(15)+5);
			                continue;
			            }
			            else
			                lastNode = null;
			        }
			        if (lastNode == currentNode.right.get())
			        {
			            lastNode = currentNode;
			            currentNode = currentNode.parent.get();
			        }
			    }
				record+="\r\n";
			}
			
		}
		
		public String getString() {
			return record;
		}
		
	}
	*/
	
	static class TreeReader2 implements Runnable{
		private String record = "";
		
		@Override
		public void run() {
			// Record in-order sequence a space separated string
			
			
			long startTime = System.currentTimeMillis();
			
			while(System.currentTimeMillis()-startTime < threadsRunTime) {	//This may exceed 5 sec but it should not matter too much, we will terminate after the current tree traversal
				Node currentNode = root;
				Node lastNode = null;
                Node temp = null;

				//This algorithm to read a binary tree was taken from https://stackoverflow.com/questions/10371848/how-to-do-in-order-traversal-of-a-bst-without-recursion-or-stack-but-using-paren
				while (currentNode != null)
			    {
					temp = null;
			        if (lastNode == currentNode.parent.get())
			        {
		                lastNode = currentNode;
			            if ((temp = currentNode.left.get()) != null)
			            {
			                currentNode = temp;
			            }else if((temp = currentNode.right.get()) != null){
			            	record+=" "+currentNode.value;
			            	currentNode = temp;
			            }else {		//We reached a leaf, go up
			            	record+=" "+currentNode.value;
			            	currentNode = currentNode.parent.get();
			            }
			        }else if(lastNode.value < currentNode.value) {	//We are coming back from our left child
		                lastNode = currentNode;
		            	record+=" "+currentNode.value;
			        	if((temp = currentNode.right.get()) != null) { //Go to right child
			        		currentNode = currentNode.right.get();
			        	}else { //Go up
			            	currentNode = currentNode.parent.get();
			        	}
			        }else if(lastNode.value >= currentNode.value) { //we are coming back from our right child
			        	//Go up
		        		lastNode = currentNode;
		            	currentNode = currentNode.parent.get();
			        }
	        		trySleep(rnd.nextInt(15)+5);
			    }
				record+="\r\n";
			}
			
		}
		
		public String getString() {
			return record;
		}
		
		
	}
	
	static class TreeWriter implements Runnable{
		//AtomicReference<Node> currentNode;
		@Override
		public void run() {
			
			long startTime = System.currentTimeMillis();
			
			while(System.currentTimeMillis()-startTime < threadsRunTime) {	//This may exceed 5 sec but it should not matter too much, we will terminate after the current action
				randomAction(root, minNodeValue, maxNodeValue);
			}
			
		}
		
		void randomAction(Node currentNode, float min, float max) {
			//Randomly delete left node
			if(currentNode.left.get() != null) {	//The get and set are not atomic but it does not matter for the reader threads
				if(rnd.nextFloat() < 0.1f) {
					//System.out.println("Deleted Node");
					currentNode.left.set(null);
					trySleep(rnd.nextInt(5)+1);
					return;
				}
			}else {
				//Add a node to the left
				if(rnd.nextFloat() < 0.4f) {
					//System.out.println("Added node");
					Node newNode = new Node(rnd.nextFloat()*(currentNode.value-min)+min);
					newNode.parent.set(currentNode);
					currentNode.left.set(newNode);
					trySleep(rnd.nextInt(5)+1);
					return;
				}
			}
			//Randomly delete right node
			if(currentNode.right.get() != null) {
				if(rnd.nextFloat() < 0.1f) {
					//System.out.println("Deleted Node");
					currentNode.right.set(null);
					trySleep(rnd.nextInt(5)+1);
					return;
				}
			}else {
				//Add a node to the right
				if(rnd.nextFloat() < 0.4f) {
					//System.out.println("Added node");
					Node newNode = new Node(rnd.nextFloat()*(max-currentNode.value)+currentNode.value);
					newNode.parent.set(currentNode);
					currentNode.right.set(newNode);
					trySleep(rnd.nextInt(5)+1);
					return;
				}
			}
			if(currentNode.left.get() != null && rnd.nextFloat() < 0.5f) {

				randomAction(currentNode.left.get(),min,currentNode.value);
			}else if(currentNode.right.get() != null) {
				randomAction(currentNode.right.get(),currentNode.value,max);
			}
			//If we reach here we are on a leaf and were not able to create or delete anything. Do nothing, just restart from the outer loop
		}
		
	}
	
	//HELPERS
	static float getRandomBetween(float min, float max) {
		return rnd.nextFloat()*(max-min)+min;
	}
	
	/**
	 * We dont need to do anything on interruption, this method handle the try catch for cleaner code
	 * @param millis
	 */
	static void trySleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {

		}
	}

}


/**
 * The references are atomic such that we dont get corrupted references from multithreading
 * @author Anthony
 *
 */
class Node {
    float value;
    AtomicReference<Node> parent;
    AtomicReference<Node> left;
    AtomicReference<Node> right;
 
    Node(float value) {
        this.value = value;
        parent = new AtomicReference<Node>(null);
        right = new AtomicReference<Node>(null);
        left = new AtomicReference<Node>(null);
    }
}
