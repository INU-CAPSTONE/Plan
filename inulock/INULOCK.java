package inulock;

import java.net.URI;
import java.net.URISyntaxException;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

import com.hopding.jrpicam.exceptions.FailedToRunRaspistillException;

import inulock.io.gpio.GpioControl;
import inulock.qr.QRScanner;

public class INULOCK {
	public static void main(String[] args) throws InterruptedException, FailedToRunRaspistillException, URISyntaxException {
		final GpioControl gpio = new GpioControl();
		final String ipAddr;
		final boolean preview;
		
		// parse arguments
		if (args.length == 1) {
			ipAddr = args[0];
			preview = false;
		} else if(args.length == 2) {
			ipAddr = args[0];
			if (args[1].equals("true"))preview = true;
			else preview = false;
		} else {
			ipAddr = "192.168.1.1";
			preview = true;
		}
		
		QRScanner scan = new QRScanner();
		WebSocketClient wsclient = new WebSocketClient(new URI("ws://"+ipAddr+":3000")) {
			@Override
			public void onOpen(ServerHandshake handshakedata) {
				send("connection established");
				System.out.println("opened connection");
			}

			@Override
			public void onMessage(String message) {
				System.out.println("received: " + message);
				if(message.equals("success") == true) {
					try {
						gpio.setLED('Y', true);
						gpio.setLED('G', true);
						gpio.openDoor();
						
						gpio.setLED('Y', false);
						//Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}else if(message.equals("fail") == true) {
					gpio.setLED('Y', true);
					gpio.setLED('R', true);
					//try {
						//Thread.sleep(1000);
					//} catch (InterruptedException e) {
						// TODO Auto-generated catch block
					//	e.printStackTrace();
					//}
				}
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
		};
		
		String decodeText;
		wsclient.connectBlocking();
		wsclient.closeBlocking();
		
		while(true) {
			gpio.setLED('R', true);
		
			while(true) {
				decodeText = scan.takeInstantSnap(preview);
				if(decodeText != null)
					break;
			}

			System.out.println("Decode QR: " + decodeText);
			gpio.setLED('R', false);
			gpio.setLED('Y', true);
		
			wsclient.reconnectBlocking();
			wsclient.send(decodeText);
			Thread.sleep(3000);
			
			gpio.setLED('G', false);
			gpio.setLED('Y', false);
		}
	}
}
