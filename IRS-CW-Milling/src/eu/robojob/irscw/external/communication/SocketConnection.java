package eu.robojob.irscw.external.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

public class SocketConnection {
	
	public enum Type {
		CLIENT, SERVER
	}
	
	private String name;
	private String ipAddress;
	private int portNumber;
	private Type type;
	
	ServerSocket serverSocket;
	Socket socket;
	PrintWriter out;
	BufferedReader in;
	
	private boolean connected;
		
	private static Logger logger = Logger.getLogger(SocketConnection.class.getName());
		
	public SocketConnection(Type type, String name, String ipAddress, int portNumber) {
		this.type = type;
		this.name = name;
		this.ipAddress = ipAddress;
		this.portNumber = portNumber;
		this.connected = false;
	}
	
	public SocketConnection(Type type, String name, int portNumber) {
		this(type, name, "127.0.0.1", portNumber);
	}
	
	public void connect() throws IOException{
		if (connected) {
			logger.info("Socket was already connected");
		} else {
			if (type == Type.CLIENT) {
				try {
					connectAsClient();
					connectInOut();
					connected = true;
					logger.info("Client connection succeeded!");
				} catch (IOException e) {
					throw e;
				} finally {
					if (!connected) {
						disconnect();
					}
				}
			} else if (type == Type.SERVER) {
				try {
					connectAsServer();
					connectInOut();
					connected = true;
					logger.info("Server connection succeeded");
				} catch (IOException e) {
					throw e;
				} finally {
					if (!connected) {
						disconnect();
					}
				}
			} else {
				throw new IllegalStateException("Unknown type");
			}
		}
	}
	
	private void connectAsClient() throws IOException {
		socket = new Socket(ipAddress, portNumber);
	}
	
	private void connectAsServer() throws IOException {
		serverSocket = new ServerSocket(portNumber);
		socket = serverSocket.accept();
	}
	
	private void connectInOut() throws IOException {
		out = new PrintWriter(socket.getOutputStream(), true);
		logger.info("PrintWriter connected to output of " + this.toString());
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		logger.info("BufferedReader connected to input from " + this.toString());
	}
	
	
	// TODO refactor!
	public void disconnect() throws IOException {
		logger.info("Disconnecting connection: " + this.toString());
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				throw e;
			} finally {
				serverSocket = null;
				socket = null;
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				out = null;
				in = null;
				connected = false;
			}
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
		if ((socket == null) || (out == null) || (in == null) || (out.checkError())) {
			connected = false;
			return false;
		} else {
			return connected;
		}
	}
	
	public boolean sendString(String message) {
		logger.debug("Sending message: " + message);
		if (isConnected()) {
			logger.info("Sending '" + message + "' to " + this.toString());
			out.println(message);
			logger.info("Message written");
			return true;
		} else {
			logger.info("Could not send message, socket was not connected");
			return false;
		}
	}
	
	public String readString() throws IOException {
		if (isConnected()) {
			logger.info("Reading from " + this.toString());
			try {
				String msg = in.readLine();
				if (msg == null) {
					disconnect();
				}
				logger.info("message: " + msg);
				return msg;
			} catch (IOException e) {
				logger.error("error while reading from: " + this.toString());
				connected = false;
				disconnect();
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
