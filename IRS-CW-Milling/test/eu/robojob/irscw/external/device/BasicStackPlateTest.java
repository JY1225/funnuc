package eu.robojob.irscw.external.device;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import eu.robojob.irscw.external.device.BasicStackPlate.WorkPieceOrientation;
import eu.robojob.irscw.workpiece.WorkPieceDimensions;

public class BasicStackPlateTest {

	private BasicStackPlate basicStackPlate;
	
	@Before
	public void setup() {
		basicStackPlate = new BasicStackPlate("basic stack plate", 27, 7, 10, 15, 45, 40, 35, 0, 0.25f);
	}
	
	@Test
	public void testCalculateAmountHorizontal() {
		basicStackPlate.configureRawWorkpieces(WorkPieceOrientation.HORIZONTAL, new WorkPieceDimensions(300, 150, 1), 5);
		System.out.println("ok");
	}
}
