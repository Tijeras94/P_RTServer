import java.io.Console;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Server extends WebSocketServer {

	ArrayList<WebSocket> clients = new ArrayList<WebSocket>(); 
	Map<String, RealTimeUser> cl= new HashMap<String, RealTimeUser>();
	
	public Server(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
	}

	public Server(InetSocketAddress address) {
		super(address);
	}
  

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) { 
		 
		cl.put(conn.getUID(), new RealTimeUser(conn.getUID(), conn));
		clients.add(conn);

		JSONObject obj = new JSONObject();
		obj.put("action", "InitConection");
		obj.put("message", "1");
		conn.send(obj.toJSONString());

		obj = new JSONObject();
		obj.put("action", "deviceTracking");
		obj.put("message", "true");
		conn.send(obj.toJSONString());

	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) { 
		String rid = this.cl.get(conn.getUID()).userID;
		System.out.println(rid + " has left the room!");
		this.cl.remove(conn);
		this.clients.remove(conn);

		JSONObject obj = new JSONObject();
		obj.put("action", "device_removed");
		obj.put("message", rid);
		sendToAll(obj.toJSONString()); 
		
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		System.out.println("Connected Clients: " + this.clients.size());
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(message);
			JSONObject array = (JSONObject) obj;

			String act = (String) array.get("action");
			String mess = (String) array.get("message");

			if (act.equals("ConnectInfo")) {
				cl.get(conn.getUID()).init(mess); 
				 System.out.println(mess);
			}

			if (act.equals("cordinates")) {
				System.out.println(mess);
				
				cl.get(conn.getUID()).cordinates(mess);
				conn.send("Cods Received!!");
			} 

			if (act.equals("getAllUserCord")) {
				ArrayList<String> cords = new ArrayList<String>();

				for (WebSocket c : clients) {
					if (c.equals(conn))
						continue;
					RealTimeUser a = cl.get(c.getUID());
					if (a.userID == "null"
							|| (a.Latitude == "null" && a.Longitude == "null"))
						continue;

					//System.out.println(a.userID + ":" + a.Latitude + ":"
					//		+ a.Longitude);

					cords.add(a.userID + ":" + a.Latitude + ":" + a.Longitude); 
				}

				JSONObject tSn = new JSONObject();
				tSn.put("action", "usersLocation");
				tSn.put("message", cords);
				conn.send(tSn.toJSONString());
				System.out.println("data: " + tSn.toJSONString());
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onFragment(WebSocket conn, Framedata fragment) {
		System.out.println("received fragment: " + fragment);
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
		if (conn != null) {
			// some errors like port binding failed may not be assignable to a
			// specific websocket
		}
	}

	@Override
	public void onStart() {
		System.out.println("Server started!");
	}

	/**
	 * Sends <var>text</var> to all currently connected WebSocket clients.
	 * 
	 * @param text
	 *            The String to send across the network.
	 * @throws InterruptedException
	 *             When socket related I/O errors occur.
	 */
	public void sendToAll(String text) {
		Collection<WebSocket> con = connections();
		synchronized (con) {
			for (WebSocket c : con) {
				c.send(text);
			}
		}
	}

}
