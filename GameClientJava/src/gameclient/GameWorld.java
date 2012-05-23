/**
 * 
 */
package gameclient;

import gameProto.WorldProtos.World;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * @author Chris
 *
 */
public class GameWorld{
	private ConnectionHandler myCH=null; //the interface to the server
	private char view=10; //how far north south east west of character to display thus width and height = (2* view)+1
	private WorldCube world=null; //for now only a single cube of blocks
	private boolean initWorld,initConn,initImages; //flags showing if it is initialized
	
	/*Graphics Globals*/
	
	
	public GameWorld(){
		generateNewWorld();
		
	}
	/*public GameWorld() {
		super();
		initWorld=initConn=initImages=false;
		//generateNewWorld();
		try {
			myCH=new ConnectionHandler(new Socket("127.0.0.1", 1234));
			initConn=true;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loadImages();
		loadWorld();
	}*/

	public void generateNewWorld() { //generate a new world. this will eventually be done by the server GameCore and this will use loadWorld()
		// TODO Auto-generated method stub
		world = new WorldCube(0);
		for(int i=0;i<(WorldCube.size);i++){
			for(int j=0;j<(WorldCube.size);j++){
				for(int k=0;k<(WorldCube.size);k++){
					world.setType(i, j, k, (i+j+k)%2);
				}
			}
		}
		
		initWorld=true;
	}
	public void loadWorld(){
		/*if(initConn){
		world = new WorldCube[(2*view)+1][(2*view)+1];	
		String in=null;
		myCH.out.println('0');
		System.out.println("Sent command");
		World worldData = null;
		
		try {
			worldData = World.parseDelimitedFrom(myCH.socket.getInputStream());
			//in=myCH.in.readLine();
			System.out.println("World received:"+in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0;i<21;i++){
			for(int j=0;j<21;j++){
				world[i][j]=new WorldCube(worldData.getChunks(0).getNodes(i+j).getType());
			}
		}
		System.out.println("World loaded");
		initWorld=true;
		}*/
	}
	

	public boolean isInitilized() {
		return (initWorld&&initConn&&initImages);
	}
	public int getCubeType(int x, int y, int z){
		return(world.getBlockType(x,y,z));
		
	}
	
	
}
