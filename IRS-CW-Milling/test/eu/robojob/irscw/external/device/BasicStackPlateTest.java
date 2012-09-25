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
		int amount = basicStackPlate.calculateMaxWorkPieceAmount(WorkPieceOrientation.HORIZONTAL, new WorkPieceDimensions(300, 150, 1));
		Assert.assertEquals(6, amount);
		amount = basicStackPlate.calculateMaxWorkPieceAmount(WorkPieceOrientation.HORIZONTAL, new WorkPieceDimensions(750, 300, 1));
		Assert.assertEquals(1, amount);
		amount = basicStackPlate.calculateMaxWorkPieceAmount(WorkPieceOrientation.HORIZONTAL, new WorkPieceDimensions(400, 300, 1));
		Assert.assertEquals(2, amount);
		amount = basicStackPlate.calculateMaxWorkPieceAmount(WorkPieceOrientation.HORIZONTAL, new WorkPieceDimensions(300, 300, 1));
		Assert.assertEquals(3, amount);
		amount = basicStackPlate.calculateMaxWorkPieceAmount(WorkPieceOrientation.HORIZONTAL, new WorkPieceDimensions(310, 300, 1));
		Assert.assertEquals(2, amount);
		amount = basicStackPlate.calculateMaxWorkPieceAmount(WorkPieceOrientation.TILTED, new WorkPieceDimensions(310, 200, 1));
		Assert.assertEquals(2, amount);
		amount = basicStackPlate.calculateMaxWorkPieceAmount(WorkPieceOrientation.TILTED, new WorkPieceDimensions(250, 150, 1));
		Assert.assertEquals(3, amount);
	}
}
