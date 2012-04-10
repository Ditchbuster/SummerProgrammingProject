/**
 * 
 */
package gamecore;
import java.net.*;
import java.util.ArrayList;
import java.io.*;
/**
 * @author Chris
 *
 */
public class GameCore {

	/**
	 * @param args
	 */
	ArrayList<Thread> myCh = new ArrayList<Thread>();
	private ServerSocket serverSocket = null;
	private WorldNode[][] world;
	
	GameCore(int port){
		System.out.println("Hello World!");
		world = new WorldNode[10][10];
        try {
            serverSocket =  new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 1234.");
            System.exit(1);
        }
        //GCOpen();
     }
	public void GCOpen(){
        try {
        	for(int i=0;i<5;i++)
        		myCh.add(new ClientHandler(serverSocket.accept(),this));
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }
        shutdown();
	
	}
	
	
	public static void main(String[] args) throws IOException{
		GameCore myGC = new GameCore(1234);
		myGC.GCOpen();
	}
	
	public void shutdown(){
		 try {
	    	   for(int i =0; i<myCh.size();i++){
	    		   myCh.get(i).join();
	    	   }
		} catch (InterruptedException e) {
			 System.err.println("Join effed up");
	         System.exit(1);
		}
	        try {
				serverSocket.close();
			} catch (IOException e) {
				
			}
		
	}
}
