/**
 * 
 */
package gameclient;

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
public class GameWorld extends JPanel{
	private ConnectionHandler myCH=null; //the interface to the server
	private char view=10; //how far north south east west of character to display thus width and height = (2* view)+1
	private WorldCube[][] world=null;
	private boolean initWorld,initConn,initImages; //flags showing if it is initialized
	
	/*Graphics Globals*/
	private int offset=7;
	private int tileSize=30; //size of a worldcube in pixels
	private Image[] images=null; //holds all the images for different things that can be drawn - eventually make into own class probably
	
	
	public GameWorld() {
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
	}

	public void generateNewWorld() { //generate a new world. this will eventually be done by the server GameCore and this will use loadWorld()
		// TODO Auto-generated method stub
		world = new WorldCube[(2*view)+1][(2*view)+1];
		for(int i=0;i<(2*view)+1;i++){
			for(int j=0;j<(2*view)+1;j++){
				world[i][j]=new WorldCube((i+j)%2);
			}
		}
		
		initWorld=true;
	}
	public void loadWorld(){
		if(initConn){
		world = new WorldCube[(2*view)+1][(2*view)+1];	
		String in=null;
		myCH.out.println('0');
		System.out.println("Sent command");
		try {
			in=myCH.in.readLine();
			System.out.println("World received:"+in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0;i<21;i++){
			for(int j=0;j<21;j++){
				world[i][j]=new WorldCube(in.charAt((i*21)+j)%2);
			}
		}
		System.out.println("World loaded");
		initWorld=true;
		}
	}
	
	private void loadImages(){
		 	images= new Image[3];
			URL loc = this.getClass().getResource("type0.png");
	        ImageIcon iia = new ImageIcon(loc);
	        images[0] = iia.getImage();
	        loc = this.getClass().getResource("type1.png");
	        iia = new ImageIcon(loc);
	        images[1] = iia.getImage();
	        loc = this.getClass().getResource("type2.png");
	        iia = new ImageIcon(loc);
	        images[2] = iia.getImage();
	        
		initImages=true;
	}
	private void paintWorld(Graphics g) {
		// TODO Auto-generated method stub
		int x,y;
		x=y=offset;
		g.setColor(new Color(250, 240, 170));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        
        for(int i=0;i<(2*view)+1;i++){
			for(int j=0;j<(2*view)+1;j++){
				g.drawImage(images[world[i][j].getType()], i*tileSize+offset, j*tileSize+offset, this); //should probably check the type for out of bounds or something
				
				if(j==view&&i==view){
					g.drawImage(images[2], i*tileSize+offset, j*tileSize+offset, this); //should probably check the type for out of bounds or something
				}
				
			}
		}
	}
	
	
	
	

	public int getViewWidth() {
		// TODO Auto-generated method stub
		return ((2*view)+1)*tileSize;
	}

	public int getViewHeight() {
		// TODO Auto-generated method stub
		return ((2*view)+1)*tileSize;
	}

	public boolean isInitilized() {
		return (initWorld&&initConn&&initImages);
	}
	 @Override
	    public void paint(Graphics g) {
	        super.paint(g);
	        paintWorld(g);
	    }

	
}
