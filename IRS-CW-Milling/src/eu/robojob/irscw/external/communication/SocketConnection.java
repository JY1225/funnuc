package eu.robojob.irscw.external.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

public class SocketConnection {
	
	private String name;
	private String ipAddress;
	private int portNumber;
	
	Socket socket;
	PrintWriter out;
	BufferedReader in;
	
	boolean connected;
	
	private static Logger logger = Logger.getLogger(SocketConnection.class.getName());
		
	public SocketConnection(String name, String ipAddress, int portNumber) {
		this.name = name;
		this.ipAddress = ipAddress;
		this.portNumber = portNumber;
		connected = false;
	}
	
	public SocketConnection(String name, Socket socket) throws IOException {
		this.socket = socket;
		connectInOut();
		connected = true;
	}
	
	public void connect() throws UnknownHostException, IOException {
		if (connected) {
			logger.info("Socket was already connected");
		} else {
			try {
				socket = new Socket(ipAddress, portNumber);
				connectInOut();
			} catch (UnknownHostException e) {
				logger.error(e);
				throw e;
			} catch (IOException e) {
				logger.error(e);
				disConnect();
				throw e;
			}
		}
	}
	
	private void connectInOut() throws IOException {
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			logger.info("PrintWriter connected to output of " + this.toString());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			logger.info("BufferedReader connected to input from " + this.toString());
			connected = true;
		} catch (IOException e) {
			logger.error(e);
			disConnect();
			throw e;
		} 
	}
	
	private void disConnect() {
		try {
			logger.info("Closing " + this.toString());
			connected = false;
			if (in!= null)
				in.close();
			if (out != null)
				out.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	public String getName() {
		return name;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}
	
	// checks for errors in output stream to detect disconnection
	// an alternative (better) approach would be to let the server
	// send heartbeat-messages...
	//TODO hearbeat-messages
	public boolean isConnected() {
		if ((socket == null) || (out == null)) {
			connected = false;
			return false;
		} else {
			return connected;
		}
	}
	
	public void sendString(String message) {
		if (isConnected()) {
			logger.info("Sending '" + message + "' to " + this.toString());
			out.println(message);
			logger.info("Message written");
		}
	}
	
	public String readString() throws IOException {
		if (isConnected()) {
			logger.info("Reading from " + this.toString());
			try {
				String msg = in.readLine();
				logger.info("message: " + msg);
				return msg;
			} catch (IOException e) {
				connected = false;
				disConnect();
				logger.error(e);
				throw e;
			}
		}
		return null;
	}
	
	public String synchronizedSendAndRead(String message) throws IOException {
		sendString(message);
		return readString();
	}
	
	@Override
	public String toString() {
		return "socket-connection " + this.name + " - " + socket;
	}
}
