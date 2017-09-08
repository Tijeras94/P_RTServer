import org.java_websocket.WebSocket;


public class RealTimeUser {

	public String userID = "not_define";
	public WebSocket conn = null;
	public String Latitude= "not_define";
	public String Longitude= "not_define";
	
	public RealTimeUser(String id, WebSocket conn)
	{
		this.userID = id;
		this.conn = conn;
	}
	
	public void init( String id ) {  
		  this.userID =  id;
	}
	
	public void cordinates( String raw ) {  
		String[] arr = raw.split(",");
		 this.Latitude = arr[0];
		 this.Longitude = arr [1]; 
	}
	
	public void send( String data ) {  
		this.conn.send(data);
	}
}
