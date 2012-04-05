/**
 * 
 */
package gamecore;

/**
 * @author Chris
 *handles each client and related info
 */
public class ClientHandler extends Thread{
	ClientHandler() {
	}
	ClientHandler(String threadName) {
		super(threadName); // Initialize thread.
		System.out.println(this);
		start();
	}
	public void run() {
		//Display info about this particular thread
		System.out.println(Thread.currentThread().getName());
	}

}
