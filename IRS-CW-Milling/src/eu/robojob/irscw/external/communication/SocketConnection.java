package eu.robojob.irscw.external.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SocketConnection {
	
	public enum Type {
		CLIENT, SERVER
	}
	
	private String id;
	private String ipAddress;
	private int portNumber;
	private Type type;
	
	private ServerSocket serverSocket;
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	
	private boolean connected;
		
	private static Logger logger = LogManager.getLogger(SocketConnection.class.getName());
		
	public SocketConnection(final Type type, final String id, final String ipAddress, final int portNumber) {
		this.type = type;
		this.id = id;
		this.ipAddress = ipAddress;
		this.portNumber = portNumber;
		this.connected = false;
	}
	
	public SocketConnection(final Type type, final String id, final int portNumber) {
		this(type, id, "127.0.0.1", portNumber);
	}
	
	public synchronized void connect() throws IOException {
		if (!connected) {
			if (type == Type.CLIENT) {
				try {
					socket = new Socket(ipAddress, portNumber);
					connectInOut();
					connected = true;
				} catch (IOException e) {
					if (connected) {
						disconnect();
					}
					throw e;
				}
			} else if (type == Type.SERVER) {
				try {
					serverSocket = new ServerSocket(portNumber);
					socket = serverSocket.accept();
					connectInOut();
					connected = true;
				} catch (IOException e) {
					if (connected) {
						disconnect();
					}
					throw e;
				}
			} else {
				throw new IllegalStateException("Unknown connection type.");
			}
		}
	}
	
	private void connectInOut() throws IOException {
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
	}
	
	public synchronized void disconnect() {
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException e) {
			logger.error(e);
			e.printStackTrace();
		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				logger.error(e);
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
						logger.error(e);
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
		return id;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(final String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(final int portNumber) {
		this.portNumber = portNumber;
	}
	
	public synchronized boolean isConnected() {
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
	
	public void send(final String message) throws DisconnectedException {
		if (isConnected()) {
			out.print(message);
			out.flush();
		} else {
			throw new DisconnectedException(this);
		}
	}
	
	public void send(final char character) throws DisconnectedException {
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
				// Disconnect by default when exception occurred, if external device is healthy it will be waiting for re-connection.
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
		    	  throw new IOException("Data truncated (end of stream reached).");
			   }
			   return (char) b;
			} catch (IOException e) {
				// Disconnect by default when exception occurred, if external device is healthy it will be waiting for re-connection.
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
			    	  throw new DisconnectedException(this);
				   } else {
						message = message + (char) b;
						while (in.ready()) {
							b = in.read();
							message = message + (char) b;
						}
						return message;
				   }
				} catch (IOException e) {
					// Disconnect by default when exception occurred, if external device is healthy it will be waiting for re-connection.
					disconnect();
					throw e;
				}
		} else {
			throw new DisconnectedException(this);
		}
	}
	
	@Override
	public String toString() {
		return this.id + "(type: " + type + ", " + ipAddress + ":" + portNumber + ")";
	}
}
