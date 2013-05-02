package eu.robojob.millassist.db.external.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import eu.robojob.millassist.db.ConnectionManager;
import eu.robojob.millassist.external.communication.socket.SocketConnection;

public class ConnectionMapper {	
	
	private Map<Integer, SocketConnection> socketConnectionBuffer;
	
	public ConnectionMapper() {
		this.socketConnectionBuffer = new HashMap<Integer, SocketConnection>();
	}

	public SocketConnection getSocketConnectionById(final int socketConnectionId) throws SQLException {
		SocketConnection socketConnection = socketConnectionBuffer.get(socketConnectionId);
		if (socketConnection != null) {
			return socketConnection;
		}
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM SOCKETCONNECTION WHERE ID = ?");
		stmt.setInt(1, socketConnectionId);
		ResultSet results = stmt.executeQuery();
		if (results.next()) {
			String ipAddress = results.getString("IPADDRESS");
			int portNumber = results.getInt("PORTNR");
			boolean client = results.getBoolean("CLIENT");
			String name = results.getString("NAME");
			if (client) {
				socketConnection = new SocketConnection(SocketConnection.Type.CLIENT, name, ipAddress, portNumber);
			} else {
				socketConnection = new SocketConnection(SocketConnection.Type.SERVER, name, ipAddress, portNumber);
			}
			socketConnection.setId(socketConnectionId);
		}
		stmt.close();
		socketConnectionBuffer.put(socketConnectionId, socketConnection);
		return socketConnection;
	}
	
}
