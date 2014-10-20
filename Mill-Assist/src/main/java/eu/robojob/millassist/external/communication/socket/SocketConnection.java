package eu.robojob.millassist.external.communication.socket;

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
	
	private int id;
	private String name;
	private String ipAddress;
	private int portNumber;
	private Type type;
	
	private ServerSocket serverSocket;
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	
	private boolean connected;
		
	private static Logger logger = LogManager.getLogger(SocketConnection.class.getName());
		
	public SocketConnection(final Type type, final String name, final String ipAddress, final int portNumber) {
		this.type = type;
		this.name = name;
		this.ipAddress = ipAddress;
		this.portNumber = portNumber;
		this.connected = false;
	}
	
	public SocketConnection(final Type type, final String name, final int portNumber) {
		this(type, name, "127.0.0.1", portNumber);
	}
	
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
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
		} else {
			logger.info(toString() + " was already connected.");
		}
	}
	
	private synchronized void connectInOut() throws IOException {
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
		return name;
	}
	
	public void setName(final String name) {
		this.name = name;
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
	
	public boolean isConnected() {
		if (connected) {
			if ((socket == null) || (out == null) || (in == null)) {
				disconnect();
				//throw new IllegalStateException("Status indicates connection, but one or more objects are null");
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
	
	public void send(final String message) throws SocketDisconnectedException {
		if (isConnected()) {
			out.print(message);
			out.flush();
		} else {
			throw new SocketDisconnectedException(this);
		}
	}
	
	public void send(final char character) throws SocketDisconnectedException {
		if (isConnected()) {
			out.print(character);
			out.flush();
		} else {
			throw new SocketDisconnectedException(this);
		}
	}
	
	public String readString() throws IOException, SocketDisconnectedException {
		if (isConnected()) {
			try {
				String msg = in.readLine();
				if (msg == null) {
					disconnect();
					throw new SocketDisconnectedException(this);
				}
				return msg;
			} catch (IOException e) {
				// Disconnect by default when exception occurred, if external device is healthy it will be waiting for re-connection.
				disconnect();
				throw e;
			}
		} else {
			throw new SocketDisconnectedException(this);
		}
	}
	
	public char read() throws IOException, SocketDisconnectedException {
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
			throw new SocketDisconnectedException(this);
		}
	}
	
	public String readMessage() throws IOException, SocketDisconnectedException {
		if (isConnected()) {
			String message = "";
			try {
			      int b = in.read();
			      if (b < 0) {
			    	  logger.info("Read negative char, so disconnected...");
			    	  disconnect();
			    	  throw new SocketDisconnectedException(this);
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
			throw new SocketDisconnectedException(this);
		}
	}
	
	@Override
	public String toString() {
		return this.name + " [type=" + type + " - ip=" + ipAddress + " - port=" + portNumber + "]";
	}
}
