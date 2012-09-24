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
	private float overFlowPercentage;
	
	// specific configuration settings
	private WorkPieceOrientation workPieceOrientation;
	
	private WorkPieceDimensions rawWorkPieceDimensions;
	private int rawWorkPiecePresentAmount;
	
	private WorkPieceDimensions finishedWorkpieceDimensions;
	private int finishedWorkPiecePresentAmount;
	
	private static Logger logger = Logger.getLogger(BasicStackPlate.class);
	
	public BasicStackPlate(String id, List<Zone> zones, int horizontalHoleAmount, int verticalHoleAmount, float holeDiameter, float studDiameter,
			float horizontalPadding, float verticalPadding, float horizontalHoleDistance, float interferenceDistance, float overflowPercentage) {
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
		this.overFlowPercentage = overflowPercentage;
		if (overflowPercentage < 0 || overflowPercentage > 1) {
			throw new IllegalArgumentException("Wrong percentage value");
		}
	}
	
	public BasicStackPlate(String id, int horizontalHoleAmount, int verticalHoleAmount, float holeDiameter, float studDiameter,
			float horizontalPadding, float verticalPadding, float horizontalHoleDistance, float interferenceDistance, float overflowPercentage) {
		this(id, new ArrayList<Zone>(), horizontalHoleAmount, verticalHoleAmount, holeDiameter, studDiameter, horizontalPadding, verticalPadding, 
				horizontalHoleDistance, interferenceDistance, overflowPercentage);
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
		
		if (workPieceDimensions.getLength() < (horizontalHoleDistance - studDiameter/2)) {
			throw new IllegalStateException("Workpiece-length is too small!");
		}
		
		// calculate amount of holes one piece takes (horizontally):
		float remainingLength = workPieceDimensions.getLength();
		// initially two studs are used
		int amountOfHorizontalStudsOnePiece = 2;
		// the piece is moved studDiameter/2 from the center of its left-most stud
		// the distance between the left-most side and the center of the second stud is removed from the remaining length
		remainingLength -= (horizontalHoleDistance + studDiameter/2);
		// for each time the horizontal hole distance fits in the remaining length the amount of horizontal studs is incremented
		while (remainingLength > horizontalHoleDistance) {
			remainingLength -= horizontalHoleDistance;
			amountOfHorizontalStudsOnePiece++;
		}
		// the remaining distance is the space between the next stud and the end of the piece
		float remainingDistance = horizontalHoleDistance - remainingLength;
		if (remainingDistance - studDiameter/2 < interferenceDistance) {
			amountOfHorizontalStudsOnePiece++;
		}
		
		// how many times will this fit
		int amountHorizontal = (int) Math.floor(horizontalHoleAmount / amountOfHorizontalStudsOnePiece);
		// special condition for the last piece
		if (horizontalHoleAmount % amountOfHorizontalStudsOnePiece == 0) {
			if ((remainingDistance < horizontalHoleDistance) && ((remainingLength - horizontalPadding)/workPieceDimensions.getLength() > overFlowPercentage)) {
				amountHorizontal--;
			}
		}
		
		int amountOfVerticalStudsOnePiece = 1;
		float remainingWidth = workPieceDimensions.getWidth();
		while (remainingWidth > verticalHoleDistance) {
			remainingWidth -= verticalHoleDistance;
			amountOfVerticalStudsOnePiece ++;
		}
		remainingDistance = verticalHoleDistance - remainingWidth;
		if (remainingDistance - studDiameter < interferenceDistance) {
			amountOfVerticalStudsOnePiece++;
		}
		
		// how many times will this fit
		int amountVertical = (int) Math.floor(verticalHoleAmount / amountOfVerticalStudsOnePiece);
		// special condition for the last piece
		if (verticalHoleAmount % amountOfVerticalStudsOnePiece == 0) {
			if ((remainingDistance < verticalHoleDistance) && ((remainingWidth - verticalPadding)/workPieceDimensions.getWidth() > overFlowPercentage)) {
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
		
		// TODO this should not be a problem, but calculations change!
		// condition 1
		// note: otherwhise, the connection between the workpiece and the left-bottom-stud is not really a 
		// 'tangent line'
		if (workPieceDimensions.getWidth() < (studDiameter/2 + Math.sqrt(2)*b)) {
			if (workPieceDimensions.getWidth() > Math.sqrt(2)*b) {
				throw new IllegalArgumentException("width is too small! ... for now");
			} else {
				throw new IllegalArgumentException("width is too small!");
			}
		}
		
		// note: we want to store the centerpoints in the future
		
		// amount of studs to the left of a workpiece
		int studsLeft = 1 + ((int) Math.floor((2*c+d)/horizontalHoleDistance));
		double q = horizontalHoleDistance - ((2*c + d) % horizontalHoleDistance);
		if (Math.sqrt(2) * ((q/2 + d)/2) < interferenceDistance) {
			studsLeft++;
		}
		
		// amount of extra studs to the left of first (most-left) workPiece:
		int studsMostLeft = 1;
		if (c > horizontalPadding) {
			 studsMostLeft += Math.ceil((c - horizontalPadding) / horizontalHoleDistance);
		}
		
		// vertical location of top-right stud for one piece
		int topRightOnePiece = (int) Math.floor(k / verticalHoleDistance);
		
		// amount of studs needed horizontally
		int studsMostRight = topRightOnePiece*3;
		
		// check how many vertical studs a row of workpieces needs
		int verticalStuds = 1 + (int) Math.floor(g/verticalHoleDistance);
		if (verticalHoleDistance - (g % verticalHoleDistance) <= b) {
			verticalStuds++;
		}
		
		int verticalAmount = (int) Math.floor(verticalHoleAmount / verticalStuds);
		if (verticalHoleAmount % verticalStuds > 2) {
			double remaining = (workPieceDimensions.getWidth() + workPieceDimensions.getLength())*Math.sin(Math.PI/4) - (verticalHoleAmount % verticalAmount - 1) * verticalHoleDistance - verticalPadding;
			double percentage = remaining / (workPieceDimensions.getWidth() + workPieceDimensions.getLength())*Math.sin(Math.PI/4);
			if (percentage > overFlowPercentage) {
				verticalAmount++;
			}
		}
		
		// for the top row, minimum two studs should remain, and the distance above the padding-zone should be maximum toppercentage of the piece
		
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
		logger.debug("vertical studs: " + verticalStuds);
		logger.debug("top amount: " + verticalAmount);
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
