package eu.robojob.millassist.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.robojob.millassist.positioning.Config;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.positioning.UserFrame;
import eu.robojob.millassist.workpiece.RoundDimensions;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Material;
import eu.robojob.millassist.workpiece.WorkPiece.WorkPieceShape;
import eu.robojob.millassist.workpiece.RectangularDimensions;

public class GeneralMapper {

	private Map<Integer, UserFrame> userFrameBuffer;
	private Map<Integer, Map<Integer, Coordinates>> coordinatesBuffer;
	private Map<Integer, Map<Integer, WorkPiece>> workPieceBuffer;
	
	public GeneralMapper() {
		this.userFrameBuffer = new HashMap<Integer, UserFrame>();
		this.coordinatesBuffer = new HashMap<Integer, Map<Integer, Coordinates>>();
		this.workPieceBuffer = new HashMap<Integer, Map<Integer, WorkPiece>>();
	}
	
	public void clearBuffers(final int processFlowId) {
		coordinatesBuffer.remove(processFlowId);
		workPieceBuffer.remove(processFlowId);
	}
	
	public UserFrame getUserFrameById(final int userFrameId) throws SQLException {
		UserFrame userFrame = userFrameBuffer.get(userFrameId);
		if (userFrame != null) {
			return userFrame;
		} 
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM USERFRAME WHERE ID = ?");
		stmt.setInt(1, userFrameId);
		ResultSet results = stmt.executeQuery();
		while (results.next()) {
			int number = results.getInt("NUMBER");
			float zsafe = results.getFloat("ZSAFE");
			int locationId = results.getInt("LOCATION");
			String name = results.getString("NAME");
			Coordinates location = getCoordinatesById(0, locationId);
			userFrame = new UserFrame(number, name, zsafe, location);
			userFrame.setId(userFrameId);
		}
		stmt.close();
		userFrameBuffer.put(userFrameId, userFrame);
		return userFrame;
	}
	
	public UserFrame getUserFrameByName(final String userFrameName) throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM USERFRAME WHERE NAME = ?");
		stmt.setString(1, userFrameName);
		ResultSet results = stmt.executeQuery();
		UserFrame uf = null;
		if (results.next()) {
			int id = results.getInt("ID");
			int number = results.getInt("NUMBER");
			float zsafe = results.getFloat("ZSAFE");
			int locationId = results.getInt("LOCATION");
			String name = results.getString("NAME");
			Coordinates location = getCoordinatesById(0, locationId);
			uf = new UserFrame(number, name, zsafe, location);
			uf.setId(id);
		}
		stmt.close();
		return uf;
	}
	
	public Coordinates getCoordinatesById(final int processFlowId, final int coordinatesId) throws SQLException {
		Coordinates coordinates = null;
		if (processFlowId != 0) {
			Map<Integer, Coordinates> buffer = coordinatesBuffer.get(processFlowId);
			if (buffer != null) {
				coordinates = buffer.get(coordinatesId);
				if (coordinates != null) {
					return coordinates;
				}
			} else {
				Map<Integer, Coordinates> newBuffer = new HashMap<Integer, Coordinates>();
				coordinatesBuffer.put(processFlowId, newBuffer);
			}
		}
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM COORDINATES WHERE ID = ?");
		stmt.setInt(1, coordinatesId);
		ResultSet results = stmt.executeQuery();
		if (results.next()) {
			float x = results.getFloat("X");
			float y = results.getFloat("Y");
			float z = results.getFloat("Z");
			float w = results.getFloat("W");
			float p = results.getFloat("P");
			float r = results.getFloat("R");
			coordinates = new Coordinates(x, y, z, w, p, r);
			coordinates.setId(coordinatesId);
		}
		stmt.close();
		if (processFlowId != 0) {
			coordinatesBuffer.get(processFlowId).put(coordinatesId, coordinates);
		}
		return coordinates;
	}
	
	public void deleteCoordinates(final Coordinates coordinates) throws SQLException {
		if (coordinates.getId() > 0) {
			PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("DELETE FROM COORDINATES WHERE ID = ?");
			stmt.setInt(1, coordinates.getId());
			stmt.executeUpdate();	
			for (Map<Integer, Coordinates> buffer : coordinatesBuffer.values()) {
				buffer.remove(coordinates.getId());
			}
			coordinates.setId(0);
		}
	}
	
	public void saveCoordinates(final Coordinates coordinates) throws SQLException {
		if (coordinates.getId() <= 0) {
			PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO COORDINATES (X, Y, Z, W, P, R) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			stmt.setFloat(1, coordinates.getX());
			stmt.setFloat(2, coordinates.getY());
			stmt.setFloat(3, coordinates.getZ());
			stmt.setFloat(4, coordinates.getW());
			stmt.setFloat(5, coordinates.getP());
			stmt.setFloat(6, coordinates.getR());
			stmt.executeUpdate();
			ResultSet keys = stmt.getGeneratedKeys();
			if ((keys != null) && (keys.next())) {
				coordinates.setId(keys.getInt(1));
			}
		} else {
			PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE COORDINATES SET X = ?, Y = ?, Z = ?, W = ?, P = ?, R = ? WHERE ID = ?");
			stmt.setFloat(1, coordinates.getX());
			stmt.setFloat(2, coordinates.getY());
			stmt.setFloat(3, coordinates.getZ());
			stmt.setFloat(4, coordinates.getW());
			stmt.setFloat(5, coordinates.getP());
			stmt.setFloat(6, coordinates.getR());
			stmt.setInt(7, coordinates.getId());
			stmt.executeUpdate();
		}
	}

	public void saveConfig(final Config config) throws SQLException {
	    if (config.getId() <= 0) {
	        PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO CONFIG (FLIP, UP, FRONT, TURN1, TURN2, TURN3) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
	        stmt.setInt(1, config.getCfgFlip());
	        stmt.setInt(2, config.getCfgUp());
	        stmt.setInt(3, config.getCfgFront());
	        stmt.setInt(4, config.getCfgTurn1());
	        stmt.setInt(5, config.getCfgTurn2());
	        stmt.setInt(6, config.getCfgTurn3());
	        stmt.executeUpdate();
	        ResultSet keys = stmt.getGeneratedKeys();
	        if ((keys != null) && (keys.next())) {
	            config.setId(keys.getInt(1));
	        }
	    } else {
	        PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE CONFIG SET FLIP = ?, UP = ?, FRONT = ?, TURN1 = ?, TURN2 = ?, TURN3 = ? WHERE ID = ?");
	        stmt.setInt(1, config.getCfgFlip());
	        stmt.setInt(2, config.getCfgUp());
	        stmt.setInt(3, config.getCfgFront());
	        stmt.setInt(4, config.getCfgTurn1());
	        stmt.setInt(5, config.getCfgTurn2());
	        stmt.setInt(6, config.getCfgTurn3());
	        stmt.setInt(7, config.getId());
	        stmt.executeUpdate();
	    }
	}
	
	public Config getConfigById(final int id) throws SQLException {
	    PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM CONFIG WHERE ID = ?");
	    stmt.setInt(1, id);
	    ResultSet results = stmt.executeQuery();
	    Config config = null;
	    if (results.next()) {
	        int cfg1 = results.getInt("FLIP");
	        int cfg2 = results.getInt("UP");
	        int cfg3 = results.getInt("FRONT");
	        int cfg4 = results.getInt("TURN1");
	        int cfg5 = results.getInt("TURN2");
	        int cfg6 = results.getInt("TURN3");
	        config = new Config(cfg1, cfg2, cfg3, cfg4, cfg5, cfg6);
	        config.setId(id);
	    }
	    return config;
	}

	public WorkPiece getWorkPieceById(final int processFlowId, final int workPieceId) throws SQLException {
	    WorkPiece workPiece = null;
	    if (processFlowId != 0) {
	        Map<Integer, WorkPiece> buffer = workPieceBuffer.get(processFlowId);
	        if (buffer != null) {
	            workPiece = buffer.get(workPieceId);
	            if (workPiece != null) {
	                return workPiece;
				}
			} else {
				Map<Integer, WorkPiece> newBuffer = new HashMap<Integer, WorkPiece>();
				workPieceBuffer.put(processFlowId, newBuffer);
			}
		}
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM WORKPIECE WHERE ID = ?");
		stmt.setInt(1, workPieceId);
		ResultSet results = stmt.executeQuery();
		if (results.next()) {
			int typeId = results.getInt("TYPE");
			int shapeId = results.getInt("SHAPE");
			float weight = results.getFloat("WEIGHT");
			int materialId = results.getInt("MATERIAL");
			Material material = Material.getMaterialById(materialId);
			workPiece = new WorkPiece(WorkPiece.Type.getTypeById(typeId), WorkPieceShape.getShapeById(shapeId), material, weight);
			workPiece.setId(workPieceId);
			getDimensionsByWP(workPiece);
		}
		stmt.close();
		if (processFlowId != 0) {
			workPieceBuffer.get(processFlowId).put(workPieceId, workPiece);
		}
		return workPiece;
	}
	
	public void getDimensionsByWP(final WorkPiece workPiece) throws SQLException {
		if (workPiece.getShape().equals(WorkPieceShape.CUBIC)) {
			PreparedStatement rectStmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM WP_RECT_DIM WHERE WORKPIECE_ID = ?");
			rectStmt.setInt(1, workPiece.getId());
			ResultSet results = rectStmt.executeQuery();
			if (results.next()) {
				float length = results.getFloat("LENGTH");
				float width = results.getFloat("WIDTH");
				float height = results.getFloat("HEIGHT");
				workPiece.setDimensions(new RectangularDimensions(length, width, height));
			}
			rectStmt.close();
		} else if (workPiece.getShape().equals(WorkPieceShape.CYLINDRICAL)) {
			PreparedStatement cylStmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM WP_CYL_DIM WHERE WORKPIECE_ID = ?");
			cylStmt.setInt(1, workPiece.getId());
			ResultSet results = cylStmt.executeQuery();
			if (results.next()) {
				float diameter = results.getFloat("DIAMETER");
				float height = results.getFloat("HEIGHT");
				workPiece.setDimensions(new RoundDimensions(diameter, height));
			}
			cylStmt.close();
		}
	}
	 
	public void saveWorkPiece(final WorkPiece workPiece) throws SQLException {
		int type = workPiece.getType().getTypeId();
		int shape = workPiece.getShape().getShapeId();
		int material = workPiece.getMaterial().getId();
		if (workPiece.getId() > 0) {
			PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE WORKPIECE SET TYPE = ?, SHAPE = ?, WEIGHT = ?, MATERIAL = ? WHERE ID = ?");
			stmt.setInt(1, type);
			stmt.setInt(2, shape);
			stmt.setFloat(3, workPiece.getWeight());
			stmt.setInt(4, material);
			stmt.setInt(5, workPiece.getId());
			try {
				updateDimensions(workPiece);
			} catch (SQLException e) {
				e.printStackTrace();
				ConnectionManager.getConnection().rollback();
			}
			stmt.executeUpdate();
		} else {
			PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO WORKPIECE (TYPE, SHAPE, WEIGHT, MATERIAL) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, type);
			stmt.setInt(2, shape);
			stmt.setFloat(3, workPiece.getWeight());
			stmt.setInt(4, material);
			stmt.executeUpdate();
			ResultSet keys = stmt.getGeneratedKeys();
			if ((keys != null) && (keys.next())) {
				workPiece.setId(keys.getInt(1));
			}
			try {
				saveDimensions(workPiece);
			} catch (SQLException e) {
				e.printStackTrace();
				ConnectionManager.getConnection().rollback();
			}
		}
	}
	
	private void saveDimensions(final WorkPiece workPiece) throws SQLException {
		if (workPiece.getShape().equals(WorkPieceShape.CUBIC)) {
			PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO WP_RECT_DIM (WORKPIECE_ID, LENGTH, WIDTH, HEIGHT) VALUES (?,?,?,?)");
			RectangularDimensions dim = (RectangularDimensions) workPiece.getDimensions();
			stmt.setInt(1, workPiece.getId());
			stmt.setFloat(2, dim.getLength());
			stmt.setFloat(3, dim.getWidth());
			stmt.setFloat(4, dim.getHeight());
			stmt.executeUpdate();
		} else if (workPiece.getShape().equals(WorkPieceShape.CYLINDRICAL)) {
			PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO WP_CYL_DIM (WORKPIECE_ID, DIAMETER, HEIGHT) VALUES (?,?,?)");
			RoundDimensions dim = (RoundDimensions) workPiece.getDimensions();
			stmt.setInt(1, workPiece.getId());
			stmt.setFloat(2, dim.getDiameter());
			stmt.setFloat(3, dim.getHeight());
			stmt.executeUpdate();
		}
	}
	
	private void updateDimensions(final WorkPiece workPiece) throws SQLException {
		if (workPiece.getShape().equals(WorkPieceShape.CUBIC)) {
			PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE WP_RECT_DIM SET LENGTH = ?, WIDTH = ?, HEIGHT = ? WHERE WORKPIECE_ID = ?");
			RectangularDimensions dim = (RectangularDimensions) workPiece.getDimensions();
			stmt.setFloat(1, dim.getLength());
			stmt.setFloat(2, dim.getWidth());
			stmt.setFloat(3, dim.getHeight());
			stmt.setInt(4, workPiece.getId());
			stmt.executeUpdate();
		} else if (workPiece.getShape().equals(WorkPieceShape.CYLINDRICAL)) {
			PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE WP_CYL_DIM SET DIAMETER = ?, HEIGHT = ? WHERE WORKPIECE_ID = ?");
			RoundDimensions dim = (RoundDimensions) workPiece.getDimensions();
			stmt.setFloat(1, dim.getDiameter());
			stmt.setFloat(2, dim.getHeight());
			stmt.setInt(3, workPiece.getId());
			stmt.executeUpdate();
		}
	}
	
	public Set<UserFrame> getAllUserFrames() throws SQLException {
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT ID FROM USERFRAME");
		ResultSet results = stmt.executeQuery();
		Set<UserFrame> userFrames = new HashSet<UserFrame>();
		while (results.next()) {
			int id = results.getInt("ID");
			userFrames.add(getUserFrameById(id));
		}
		return userFrames;
	}
	
}
