package eu.robojob.irscw;

import org.apache.log4j.Logger;

public class RoboSoft {

	static Logger logger = Logger.getLogger(RoboSoft.class.getName());
	
	/*public static void main(String[] args) {
		SocketConnection socketConnection = new SocketConnection("IRS-CW RoboSoft connection to robot", "127.0.0.1", 1234);
		try {
			socketConnection.connect();
			for (int i = 1; i < 10; i++) {
				socketConnection.sendString("Hello there: " + i);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.error(e);
				}
			}
		} catch (IOException e1) {
			logger.error(e1);
		}	
	}*/
	
	public static void main(String[] args) {
		
	}
}
