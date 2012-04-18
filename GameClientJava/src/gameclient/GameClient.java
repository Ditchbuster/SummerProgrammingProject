/**
 * Currently basic foundation for the client. Being used to test connections to the server
 */
package gameclient;
import java.io.*;
import javax.swing.JFrame;
/**
 * @author Chris
 *
 */
public class GameClient extends JFrame {
	/**
	 * 
	 */
	ConnectionHandler myCH = null;
    
	private final int OFFSET = 30;

    public GameClient() {
        myCH = new ConnectionHandler();
    	InitUI();
    }

    public void InitUI() {
        GameWorld world = new GameWorld();
        add(world);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(world.getViewWidth() + OFFSET,
                world.getViewHeight() + 2*OFFSET);
        setLocationRelativeTo(null);
        setTitle("Summer Project");
    }
    
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		 	
	        GameClient game = new GameClient();
	        game.setVisible(true);
	        

	}
	
}
