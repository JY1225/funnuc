package eu.robojob.irscw.external.device;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.robojob.irscw.positioning.Coordinates;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

// it is important this device can keep state, for other (processing) devices, no state is needed
// a workpiece is put, processed and picked, and the device-state is the same as before these actions
// for this device, state is needed to know the configuration (studs) and workpieces (finished, raw, ...) present

// for now, we asume only one layer can be placed, and the pick-locations will be re-used for putting finished workpieces
// also, this stack plate can only contain two types of workpieces, raw workpieces and finished workpieces
// also, the orientation of raw and finished workpieces is the same, and as mentioned earlier pick-locations equal put-locations
public class BasicStackPlate extends AbstractStackingDevice {

	enum WorkPieceOrientation {
		HORIZONTAL, TILTED
	}
	
	// general settings
	private int horizontalHoleAmount;
	private int verticalHoleAmount;
	private float holeDiameter;
	private float studDiameter;
	private float horizontalPadding;
	private float verticalPadding;
	private float horizontalHoleDistance;
	private float verticalHoleDistance;
	private float interferenceDistance;
	
	// specific configuration settings
	private WorkPieceOrientation workPieceOrientation;
	
	private WorkPieceDimensions rawWorkPieceDimensions;
	private int rawWorkPiecePresentAmount;
	
	private WorkPieceDimensions finishedWorkpieceDimensions;
	private int finishedWorkPiecePresentAmount;
	
	private static Logger logger = Logger.getLogger(BasicStackPlate.class);
	
	public BasicStackPlate(String id, List<Zone> zones, int horizontalHoleAmount, int verticalHoleAmount, float holeDiameter, float studDiameter,
			float horizontalPadding, float verticalPadding, float horizontalHoleDistance, float interferenceDistance) {
		super(id, zones);
		this.horizontalHoleAmount = horizontalHoleAmount;
		this.verticalHoleAmount = verticalHoleAmount;
		this.holeDiameter = holeDiameter;
		this.studDiameter = studDiameter;
		this.horizontalPadding = horizontalPadding;
		this.verticalPadding = verticalPadding;
		this.horizontalHoleDistance = horizontalHoleDistance;
		// the calculations in this class expect the vertical distance to be twice the horizontal distance
		this.verticalHoleDistance = 2*horizontalHoleDistance;
		this.interferenceDistance = interferenceDistance;
	}
	
	public BasicStackPlate(String id, int horizontalHoleAmount, int verticalHoleAmount, float holeDiameter, float studDiameter,
			float horizontalPadding, float verticalPadding, float horizontalHoleDistance, float interferenceDistance) {
		this(id, new ArrayList<Zone>(), horizontalHoleAmount, verticalHoleAmount, holeDiameter, studDiameter, horizontalPadding, verticalPadding, 
				horizontalHoleDistance, interferenceDistance);
	}

	public void configureRawWorkpieces(WorkPieceOrientation rawWorkPieceOrientation, WorkPieceDimensions rawWorkPieceDimensions, int rawWorkPiecePresentAmount) {
		//TODO check length is always larger than width
		if (calculateMaxWorkPieceAmount(rawWorkPieceOrientation, rawWorkPieceDimensions) > rawWorkPiecePresentAmount) {
			throw new IllegalArgumentException("Amount of workpieces exceeds maximum!");
		}
	}
	
	public int calculateMaxWorkPieceAmount(WorkPieceOrientation workPieceOrientation, WorkPieceDimensions workPieceDimensions) {
		switch (workPieceOrientation) {
			case HORIZONTAL:
				return calculateMaxWorkPieceAmountHorizontal(workPieceOrientation, workPieceDimensions);
			case TILTED:
				return calculateMaxWorkPieceAmountTilted(workPieceOrientation, workPieceDimensions);
			default:
				throw new IllegalArgumentException("Unknown orientation");
		}
	}
	
	private int calculateMaxWorkPieceAmountHorizontal(WorkPieceOrientation workPieceOrientation, WorkPieceDimensions workPieceDimensions) {
		
		//TODO: maybe this can be possible in the future, with special studs! calculations should take into account these studs by then
		if (workPieceDimensions.getWidth() <= (verticalHoleDistance - studDiameter/2)) {
			throw new IllegalArgumentException("Can't stack horizontally, workpiece-width is too small");
		}
		
		// calculate amount of holes one piece takes (horizontally):
		// add one because we align the workpiece-width against a stud
		int amountOfHorizontalHolesOnePiece = (int) Math.floor(workPieceDimensions.getLength()/horizontalHoleDistance) + 1;
		float horizontalRemainder = workPieceDimensions.getLength() % horizontalHoleDistance;
		if (horizontalRemainder < interferenceDistance) {
			// the next piece should be places an extra stud further
			// and because of that the horizontal remainder is increased with the horizontal distance
			amountOfHorizontalHolesOnePiece++;
			horizontalRemainder += horizontalHoleDistance;
		}
		// how many times will this fit
		int amountHorizontal = (int) Math.floor(horizontalHoleAmount / amountOfHorizontalHolesOnePiece);
		// special condition for the last piece
		if (horizontalHoleAmount % amountOfHorizontalHolesOnePiece == 0) {
			if ((horizontalHoleDistance - horizontalRemainder) > (horizontalPadding+studDiameter/2)) {
				amountHorizontal--;
			}
		}
		
		// calculate amount of holes one piece takes (vertically);
		int amountOfVerticalHolesOnePiece = (int) Math.floor(workPieceDimensions.getWidth()/verticalHoleDistance) + 1;
		float verticalRemainder = workPieceDimensions.getWidth() % verticalHoleDistance;
		if (verticalRemainder < interferenceDistance) {
			amountOfVerticalHolesOnePiece++;
			verticalRemainder += verticalHoleDistance;
		}
		int amountVertical = (int) Math.floor(verticalHoleAmount / amountOfVerticalHolesOnePiece);
		
		if (verticalHoleAmount % amountOfVerticalHolesOnePiece == 0) {
			if ((verticalHoleDistance - verticalRemainder) > (verticalPadding+studDiameter/2)) {
				amountVertical--;
			}
		}
				
		return amountHorizontal*amountVertical;
	}
	
	private int calculateMaxWorkPieceAmountTilted(WorkPieceOrientation workPieceOrientation, WorkPieceDimensions workPieceDimensions) {
		int amount = 0;
		
		// calculate necessary values:
		double d = horizontalHoleDistance/2;
		double b = d - Math.sqrt(2)*studDiameter/2;
		double a = workPieceDimensions.getWidth() * Math.sin(Math.PI/4) - b;
		double c = workPieceDimensions.getWidth() * Math.cos(Math.PI/4) - d;
		double e = c + d;
		double g = workPieceDimensions.getLength() * Math.sin(Math.PI/4) + a;
		double h = workPieceDimensions.getLength() * Math.cos(Math.PI/4) - c;
		double i = e;
		double f = Math.sqrt(0.5) * (horizontalHoleDistance - 2*c);
		double j = b + studDiameter/2*Math.sin(Math.PI/4);
		double k = workPieceDimensions.getLength() * Math.sin(Math.PI/4) - b;
		
		// TODO this should not be a problem (if not too small), but calculations change!
		if (workPieceDimensions.getWidth() < (studDiameter/2 + Math.sqrt(2)*b)) {
			if (workPieceDimensions.getWidth() > Math.sqrt(2)*b) {
				throw new IllegalArgumentException("width is too small! ... for now");
			} else {
				throw new IllegalArgumentException("width is too small!");
			}
		}
		
		// note: we want to store the centerpoints in the future
		
		// amount of studs a piece should leave to its left of the (left-)neighbouring piece
		int studsLeft = 1 + (2* (int) Math.floor(c/horizontalHoleDistance));
		if ((c % horizontalHoleDistance) < (horizontalHoleDistance/2 - interferenceDistance * Math.cos(Math.PI/4))) {
			studsLeft++;
		}
		
		// amount of extra studs to the left of first (most-left) workPiece:
		int studsMostLeft = 0;
		if (c > horizontalPadding) {
			 studsMostLeft += Math.ceil((c - horizontalPadding) / horizontalHoleDistance);
		}
		
		// location of top-right stud for one piece
		int topRightOnePiece = (int) Math.floor(k / verticalHoleDistance);
		
		// amount of studs needed
		int studsMostRight = topRightOnePiece*3;
		
		logger.debug("a: " + a);
		logger.debug("b: " + b);
		logger.debug("c: " + c);
		logger.debug("d: " + d);
		logger.debug("e: " + e);
		logger.debug("f: " + f);
		logger.debug("g: " + g);
		logger.debug("h: " + h);
		logger.debug("j: " + j);
		logger.debug("i: " + i);
		logger.debug("k: " + k);
		logger.debug("studs left: " + studsLeft);
		logger.debug("studs most left: " + studsMostLeft);
		logger.debug("top right: " + topRightOnePiece);
		logger.debug("studs most right: " + studsMostRight);
		
		return amount;
	}
	

	@Override
	public boolean canPickWorkpiece() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canPutWorkpiece() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Coordinates getPickLocation(WorkArea workArea) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Coordinates getPutLocation(WorkArea workArea) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void prepareForPick(AbstractDevicePickSettings pickSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void prepareForPut(AbstractDevicePutSettings putSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void prepareForIntervention(
			AbstractDeviceInterventionSettings interventionSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pickFinished(AbstractDevicePickSettings pickSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void putFinished(AbstractDevicePutSettings putSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void interventionFinished(
			AbstractDeviceInterventionSettings interventionSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void releasePiece(AbstractDevicePickSettings pickSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void grabPiece(AbstractDevicePutSettings putSettings)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getStatus() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public class StackingPosition {
		private Coordinates position;
		private boolean containsWorkPiece;
		private WorkPieceOrientation orientation;
		private WorkPieceDimensions dimensions;
		
		public StackingPosition(Coordinates position, boolean containsWorkPiece, WorkPieceOrientation orientation, WorkPieceDimensions dimensions) {
			this.position = position;
			this.containsWorkPiece = containsWorkPiece;
			this.orientation = orientation;
			this.dimensions = dimensions;
		}

		public Coordinates getPosition() {
			return position;
		}

		public void setPosition(Coordinates position) {
			this.position = position;
		}

		public boolean isContainsWorkPiece() {
			return containsWorkPiece;
		}

		public void setContainsWorkPiece(boolean containsWorkPiece) {
			this.containsWorkPiece = containsWorkPiece;
		}

		public WorkPieceOrientation getOrientation() {
			return orientation;
		}

		public void setOrientation(WorkPieceOrientation orientation) {
			this.orientation = orientation;
		}

		public WorkPieceDimensions getDimensions() {
			return dimensions;
		}

		public void setDimensions(WorkPieceDimensions dimensions) {
			this.dimensions = dimensions;
		}
	
	}

}
