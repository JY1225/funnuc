package eu.robojob.irscw.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
			Coordinates location = getCoordinatesById(locationId);
			userFrame = new UserFrame(number, zsafe, location);
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
		}
		stmt.close();
		workPieceBuffer.put(workPieceId, workPiece);
		return workPiece;
	}
}
