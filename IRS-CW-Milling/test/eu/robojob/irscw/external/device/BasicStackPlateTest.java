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
		basicStackPlate = new BasicStackPlate("basic stack plate", 27, 7, 10, 15, 45, 89, 35, 0, 0.25f);
	}
	
	@Test
	public void testCalculateAmountHorizontal() {
		int amount = basicStackPlate.calculateMaxWorkPieceAmount(WorkPieceOrientation.HORIZONTAL, new WorkPieceDimensions(450, 75, 1));
		Assert.assertEquals(6, amount);
		amount = basicStackPlate.calculateMaxWorkPieceAmount(WorkPieceOrientation.HORIZONTAL, new WorkPieceDimensions(450, 70, 1));
		Assert.assertEquals(6, amount);
		amount = basicStackPlate.calculateMaxWorkPieceAmount(WorkPieceOrientation.HORIZONTAL, new WorkPieceDimensions(450, 141, 1));
		Assert.assertEquals(4, amount);
		amount = basicStackPlate.calculateMaxWorkPieceAmount(WorkPieceOrientation.HORIZONTAL, new WorkPieceDimensions(35, 141, 1));
		Assert.assertEquals(26, amount);
		amount = basicStackPlate.calculateMaxWorkPieceAmount(WorkPieceOrientation.TILTED, new WorkPieceDimensions(130, 45, 1));
		amount = basicStackPlate.calculateMaxWorkPieceAmount(WorkPieceOrientation.TILTED, new WorkPieceDimensions(80, 40, 1));
	}
}
