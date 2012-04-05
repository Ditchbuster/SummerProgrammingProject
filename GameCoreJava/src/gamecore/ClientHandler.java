/**
 * 
 */
package gamecore;

import java.net.*;
import java.io.*;
/**
 * @author Chris
 *handles each client and related info
 */
public class ClientHandler extends Thread{
	
	Socket clientSocket = null;
	
	ClientHandler() {
	}
	ClientHandler(String threadName) {
		super(threadName); // Initialize thread.
		System.out.println(this);
		start();
	}
	ClientHandler(Socket accept) {
		// TODO Auto-generated constructor stub
		clientSocket = accept;
		start();
	}
	public void run() {
		//Display info about this particular thread
		System.out.println(Thread.currentThread().getName());
		 try {
	           clientinterface();
	        } catch (IOException e) {
	            System.err.println("Accept failed.");
	            System.exit(1);
	        }
	}
	void clientinterface() throws IOException{
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String int1,int2;
        int num1=0,num2=0;
 
	//out.println("server: Connected");//uncomment for debug
 
 
 
	int1 = in.readLine();
	System.out.println(int1);
	int2 = in.readLine();
	System.out.println("*"+int2);
 
 
	try
	{  
        num1=Integer.parseInt(int1);
        num2=Integer.parseInt(int2);
        }
        catch(NumberFormatException nfe)
        {
        	System.out.println("Numbers not intergers");
        	out.println("Numbers not intergers");
        }
        System.out.println("="+num1*num2);
        out.println(String.valueOf(num1*num2));
 
 
 
        out.close();
        in.close();
        clientSocket.close();
		
	}

}
