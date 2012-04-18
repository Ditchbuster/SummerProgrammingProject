/**
 * 
 */
package gameclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author Chris
 *
 */
public class ConnectionHandler {
	Socket socket = null;
    PrintWriter out = null;
    BufferedReader in = null;
    
    
    
    public ConnectionHandler() {
	}



	public ConnectionHandler(Socket socket, PrintWriter out, BufferedReader in) {
		super();
		this.socket = socket;
		this.out = out;
		this.in = in;
	}



	public void init(){
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
    try{
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
    } catch (IOException e){
    	System.err.println("I/O Fail");
        System.exit(1);
    
    }
    
    }
}
