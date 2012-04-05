/**
 * Currently basic foundation for the client. Being used to test connections to the server
 */
package gameclient;
import java.io.*;
import java.net.*;
/**
 * @author Chris
 *
 */
public class GameClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		 	Socket socket = null;
	        PrintWriter out = null;
	        BufferedReader in = null;
		
		
	        System.out.println("Client Started!");
	        try {
	            socket = new Socket("127.0.0.1", 1234);
	            out = new PrintWriter(socket.getOutputStream(), true);
	            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        } catch (UnknownHostException e) {
	            System.err.println("Don't know about host");
	            System.exit(1);
	        } catch (IOException e) {
	            System.err.println("Couldn't get I/O for the connection");
	            System.exit(1);
	        }
	 
	        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
	        String num1,num2;
	 
			//System.out.println(in.readLine()); //Uncomment to debug
	 
			System.out.print("This int-->");
			num1=read.readLine();
			out.println(num1);
			System.out.print("Times this int-->");
			num2=read.readLine();	
			out.println(num2);
			System.out.println("Equals");
	 
			System.out.println(in.readLine());
	 
	        out.close();
	        in.close();
	        read.close();
	        socket.close();

	}

}
