/**
 * 
 */
package gamecore;
import java.net.*;
import java.io.*;
/**
 * @author Chris
 *
 */
public class GameCore {

	/**
	 * @param args
	 */
	static Thread myC1;
	Thread myC2;
	
	public static void main(String[] args) throws IOException{
		System.out.println("Hello World!");
		ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(1234);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 1234.");
            System.exit(1);
        }
 
        
        try {
            myC1 = new ClientHandler(serverSocket.accept());
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }
 
       try {
		myC1.join();
	} catch (InterruptedException e) {
		 System.err.println("Join effed up");
         System.exit(1);
	}
        serverSocket.close();

	}

}
