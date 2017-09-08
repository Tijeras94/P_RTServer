import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader; 

import org.java_websocket.WebSocketImpl;

 
public class Main {

	/*
	 * {"action":"deviceTracking" , "message" : "false"}


//e03ec52743657b52

{"action":"getUserCord" , "message" : "4006bc416687f916"}


{"action":"getAllUserCord"}



	 */
	public static void main(String args[]) throws IOException, InterruptedException{ 
		WebSocketImpl.DEBUG = false;
		int port = 8000; // 843 flash policy port
		try {
			port = Integer.parseInt( args[ 0 ] );
		} catch ( Exception ex ) {
		}
		Server s = new Server( port );
		s.start();
		System.out.println( "Server started on port: " + s.getPort() );

		BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
		while ( true ) {
			String in = sysin.readLine();
			s.sendToAll( in );
			if( in.equals( "exit" ) ) {
				s.stop();
				break;
			}
		}
	}
}
