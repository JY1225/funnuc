package eu.robojob.irscw.external.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

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
		} else {
			if (type == Type.CLIENT) {
				try {
					connectAsClient();
					connectInOut();
					connected = true;
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
				} catch (IOException e) {
					throw e;
				} finally {
					if (!connected) {
						disconnect();
					}
				}
			} else {
				throw new IllegalStateException("Unknown connection type");
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
		in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
	}
	
	
	// TODO refactor!
	public void disconnect() {
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				serverSocket = null;
				socket = null;
				if (out != null) {
					out.close();
				}
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
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
	
	public boolean isConnected() {
		if (connected) {
			if ((socket == null) || (out == null) || (in == null)) {
				throw new IllegalStateException("Status indicates connection, but one or more objects are null");
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
	
	public void sendString(String message) throws DisconnectedException {
		if (isConnected()) {
			out.print(message);
			out.flush();
		} else {
			throw new DisconnectedException(this);
		}
	}
	
	public void sendCharacter(char character) throws DisconnectedException {
		if (isConnected()) {
			out.print(character);
			out.flush();
		} else {
			throw new DisconnectedException(this);
		}
	}
	
	public String readString() throws IOException, DisconnectedException {
		if (isConnected()) {
			try {
				String msg = in.readLine();
				if (msg == null) {
					disconnect();
					throw new DisconnectedException(this);
				}
				return msg;
			} catch (IOException e) {
				connected = false;
				disconnect();
				throw e;
			}
		} else {
			throw new DisconnectedException(this);
		}
	}
	
	public char read() throws IOException, DisconnectedException {
		if (isConnected()) {
			try {
		      int b = in.read();
		      if (b < 0) {
		    	  disconnect();
		    	  throw new IOException("Data truncated (end of stream reached)");
			   } else {
					logger.info("read character: " + b);
			   }
			   return (char) b;
			} catch (IOException e) {
				connected = false;
				disconnect();
				throw e;
			}
		} else {
			throw new DisconnectedException(this);
		}
	}
	
	public String readMessage() throws IOException, DisconnectedException {
		if (isConnected()) {
			String message = "";
			try {
			      int b = in.read();
			      if (b < 0) {
			    	  disconnect();
			    	  throw new IOException("Data truncated");
				   } else {
						message = message + (char) b;
						while (in.ready()) {
							b = in.read();
							message = message + (char) b;
						}
						//logger.info("read message: " + message);
						return message;
				   }
				} catch (IOException e) {
					connected = false;
					disconnect();
					throw e;
				}
		} else {
			throw new DisconnectedException(this);
		}
	}
	
	@Override
	public String toString() {
		return this.name + "(type: " + type + ", " + ipAddress + ":" + portNumber + ")";
	}
}
