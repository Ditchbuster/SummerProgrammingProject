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
	static GameCore GCserver=null; //the parent thread. Allows clients to request actions
	Socket clientSocket = null;	//this clients socket
	boolean alive = false;
	PrintWriter out = null;
	BufferedReader in = null;
	
	
	ClientHandler() {
	}
	ClientHandler(String threadName) {
		super(threadName); // Initialize thread.
		System.out.println(this);
		start();
	}
	ClientHandler(Socket accept,GameCore parent) {
		// TODO Auto-generated constructor stub
		clientSocket = accept;
		GCserver=parent;
		start();
	}
	
	public void run() {
		//Display info about this particular thread
		System.out.println(Thread.currentThread().getName());
		alive=true;
		 try {
	           clientinterface();
	        } catch (IOException e) {
	            System.err.println("ClientHandler failed.");
	            //System.exit(1);
	        }
	}
	
	void clientinterface() throws IOException{
		out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String int1,int2;
        int num1=0,num2=0;
 
	//out.println("server: Connected");//uncomment for debug
 
        while(alive){
        	switch(in.readLine().charAt(0)){
        	case '0':{//send world
        		out.println(sendChunk(0));
        	}
        	
        	}
        	
        	
        }
 
	/*int1 = in.readLine();
	System.out.println(int1);
	int2 = in.readLine();
	System.out.println("*"+int2);
	if(int1=="shutdown"||int2=="shutdown")
		GCserver.shutdown();
 
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
 */
 
 
        out.close();
        in.close();
        clientSocket.close();
		
	}
	private String sendChunk(int chunkId) {
		// TODO Auto-generated method stub
		String send = new String();
		for(int i=0;i<21;i++){
			for(int j=0;j<21;j++){
				send+=String.valueOf(GCserver.getWorld()[i][j].getType());
				System.out.print(String.valueOf(GCserver.getWorld()[i][j].getType()));
			}
		}
		System.out.println("World:"+send);
		return(send);
	}
	
}
