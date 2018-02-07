/**
 * I am using the integer object to be able to lock it in the synchronized blocks.
 * I am also using it on other tasks in case there is a discrepancy between int++ and Integer++
 * @author Anthony
 *
 */
public class q3 {
	public static final Integer x = 5;
	public static Integer varA = 0;
	public static volatile Integer varB = 0;
	public static Integer varC = 0;
	public static volatile Integer varD = 0;
	public static Integer varE = 0;
	
	public static void main(String[] args) {
		Thread tA = new Thread(new TaskA());
//		Thread tB = new Thread(new TaskB());
//		Thread tC = new Thread(new TaskC());
//		Thread tD1 = new Thread(new TaskD());
//		Thread tD2 = new Thread(new TaskD());
//		Thread tE1 = new Thread(new TaskE());
//		Thread tE2 = new Thread(new TaskE());

		
		long startTime = System.currentTimeMillis();
		
		tA.start();
//		tB.start();
//		tC.start();
//		tD1.start();
//		tD2.start();
//		tE1.start();
//		tE2.start();

		try {
			tA.join();
//			tB.join();
//			tC.join();
//			tD1.join();
//			tD2.join();
//			tE1.join();
//			tE2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println(System.currentTimeMillis() - startTime);


		
	}
	
	
	static class TaskA implements Runnable{

		@Override
		public void run() {
			while(varA < Integer.MAX_VALUE/x)varA++;
		}
		
	}
	
	static class TaskB implements Runnable{

		@Override
		public void run() {
			while(varB < Integer.MAX_VALUE/x)varB++;
			
		}
		
	}
	
	static class TaskC implements Runnable{

		@Override
		public void run() {
			synchronized(varC) {
				while(varC < Integer.MAX_VALUE/x)varC++;
			}
		}
	}
	
	static class TaskD implements Runnable{

		@Override
		public void run() {
			synchronized(varD) {
				while(varD < Integer.MAX_VALUE/x)varD++;
			}
		}
	}
	
	static class TaskE implements Runnable{

		@Override
		public void run() {
			while(varE < Integer.MAX_VALUE/x)varE++;
		}
		
	}
}
