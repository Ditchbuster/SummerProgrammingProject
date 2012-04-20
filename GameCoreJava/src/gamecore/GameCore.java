/**
 * 
 */
package gamecore;


import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import gameProto.*;
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
		generateNewWorld();
		
        try {
            serverSocket =  new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 1234.");
            System.exit(1);
        }
        //GCOpen();
     }
	public void GCOpen(){ //listen for clients
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
	
	public void shutdown(){			//dont wait for threads. shut them down.
		
	    	   for(int i =0; i<myCh.size();i++){
	    		   myCh.get(i).stop(); //TODO change stop to inturrupt but requires CH to handle that properly
	    	   }
		
	        try {
				serverSocket.close();
			} catch (IOException e) {
				
			}
		
	}
	public void generateNewWorld() { //generate a new world.
		// TODO Auto-generated method stub
		world = new WorldNode[21][21];
		for(int i=0;i<21;i++){
			for(int j=0;j<21;j++){
				world[i][j]=new WorldNode((i+j)%2);
				System.out.print(String.valueOf(world[i][j].getType()));
			}
		}
		System.out.println("World Generated");
	}
	public WorldNode[][] getWorld() {
		return world;
	}
	
}
