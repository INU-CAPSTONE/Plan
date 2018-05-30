package inulock.io.websocket;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;


public class WebsocketEndpoint extends WebSocketClient {
	public WebsocketEndpoint(URI serverUri, Draft draft) {
		super(serverUri, draft);
	}

	public WebsocketEndpoint(URI serverURI) {
		super(serverURI);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		send("connection established");
		System.out.println("opened connection");
	}

	@Override
	public void onMessage(String message) {
		System.out.println("received: " + message);
	}

	@Override 
	public void onClose(int code, String reason, boolean remote) {
		System.out.println(
				"Connection closed by " + (remote ? "remote peer" : "local") + " Code: " + code + " Reason: " + reason);
	}

	@Override
	public void onError(Exception ex) {
		ex.printStackTrace();
	}
}
