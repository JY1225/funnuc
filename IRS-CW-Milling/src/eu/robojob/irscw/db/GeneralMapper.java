package eu.robojob.irscw.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.positioning.UserFrame;
import eu.robojob.irscw.workpiece.WorkPiece;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class GeneralMapper {

	private static final int WORKPIECE_SHAPE_CUBOID = 1;
	private static final int WORKPIECE_TYPE_RAW = 1;
	private static final int WORKPIECE_TYPE_FINISHED = 2;
	
	private Map<Integer, UserFrame> userFrameBuffer;
	private Map<Integer, Coordinates> coordinatesBuffer;
	private Map<Integer, WorkPiece> workPieceBuffer;
	
	public GeneralMapper() {
		this.userFrameBuffer = new HashMap<Integer, UserFrame>();
		this.coordinatesBuffer = new HashMap<Integer, Coordinates>();
		this.workPieceBuffer = new HashMap<Integer, WorkPiece>();
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
			Coordinates location = getCoordinatesById(locationId);
			userFrame = new UserFrame(number, name, zsafe, location);
			userFrame.setId(userFrameId);
		}
		stmt.close();
		userFrameBuffer.put(userFrameId, userFrame);
		return userFrame;
	}
	
	public Coordinates getCoordinatesById(final int coordinatesId) throws SQLException {
		Coordinates coordinates = coordinatesBuffer.get(coordinatesId);
		if (coordinates != null) {
			return coordinates;
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
		coordinatesBuffer.put(coordinatesId, coordinates);
		return coordinates;
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
			PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE COORDINATES SET X = ?, Y = ?, Z = ?, W = ?, P = ?, R = ? WHERE ID = ?", Statement.RETURN_GENERATED_KEYS);
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
	
	public WorkPiece getWorkPieceById(final int workPieceId) throws SQLException {
		WorkPiece workPiece = workPieceBuffer.get(workPieceId);
		if (workPiece != null) {
			return workPiece;
		}
		PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("SELECT * FROM WORKPIECE WHERE ID = ?");
		stmt.setInt(1, workPieceId);
		ResultSet results = stmt.executeQuery();
		if (results.next()) {
			int typeId = results.getInt("TYPE");
			int shapeId = results.getInt("SHAPE");
			float length = results.getFloat("LENGTH");
			float width = results.getFloat("WIDTH");
			float height = results.getFloat("HEIGHT");
			if (shapeId == WORKPIECE_SHAPE_CUBOID) {
				if (typeId == WORKPIECE_TYPE_RAW) {
					workPiece = new WorkPiece(WorkPiece.Type.RAW, new WorkPieceDimensions(length, width, height));
				} else if (typeId == WORKPIECE_TYPE_FINISHED) {
					workPiece = new WorkPiece(WorkPiece.Type.FINISHED, new WorkPieceDimensions(length, width, height));
				} else {
					throw new IllegalStateException("Unknown workpiece type: [" + typeId + "].");
				}
			} else {
				throw new IllegalStateException("Unknown workpiece shape: [" + shapeId + "].");
			}
			workPiece.setId(workPieceId);
		}
		stmt.close();
		workPieceBuffer.put(workPieceId, workPiece);
		return workPiece;
	}
	
	public void saveWorkPiece(final WorkPiece workPiece) throws SQLException {
		int type = 0;
		if (workPiece.getType().equals(WorkPiece.Type.RAW)) {
			type = WORKPIECE_TYPE_RAW;
		} else if (workPiece.getType().equals(WorkPiece.Type.FINISHED)) {
			type = WORKPIECE_TYPE_FINISHED;
		} else {
			throw new IllegalStateException("Unkown workpiece type: [" + workPiece.getType() + "].");
		}
		//TODO: for now shape is always cuboid!
		int shape = WORKPIECE_SHAPE_CUBOID;
		if (workPiece.getId() > 0) {
			PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("UPDATE WORKPIECE SET TYPE = ?, SHAPE = ?, LENGTH = ?, WIDTH = ?, HEIGHT = ? WHERE ID = ?");
			stmt.setInt(1, type);
			stmt.setInt(2, shape);
			stmt.setFloat(3, workPiece.getDimensions().getLength());
			stmt.setFloat(4, workPiece.getDimensions().getWidth());
			stmt.setFloat(5, workPiece.getDimensions().getHeight());
			stmt.setInt(6, workPiece.getId());
			stmt.executeUpdate();
		} else {
			PreparedStatement stmt = ConnectionManager.getConnection().prepareStatement("INSERT INTO WORKPIECE (TYPE, SHAPE, LENGTH, WIDTH, HEIGHT) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, type);
			stmt.setInt(2, shape);
			stmt.setFloat(3, workPiece.getDimensions().getLength());
			stmt.setFloat(4, workPiece.getDimensions().getWidth());
			stmt.setFloat(5, workPiece.getDimensions().getHeight());
			stmt.executeUpdate();
			ResultSet keys = stmt.getGeneratedKeys();
			if ((keys != null) && (keys.next())) {
				workPiece.setId(keys.getInt(1));
			}
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
