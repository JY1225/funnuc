package eu.robojob.irscw.db.external.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import eu.robojob.irscw.db.ConnectionManager;
import eu.robojob.irscw.external.communication.socket.SocketConnection;

public final class ConnectionMapper {	
	
	private ConnectionMapper() { }

	public static SocketConnection getSocketConnectionById(final int id) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM SOCKETCONNECTION WHERE ID = ?");
		stmt.setInt(1, id);
		ResultSet results = stmt.executeQuery();
		SocketConnection socketConnection = null;
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
			socketConnection.setId(id);
		}
		return socketConnection;
	}
	
}
