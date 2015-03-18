package eu.robojob.millassist.external.device.stacking.strategy.stackplate;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.robojob.millassist.external.device.stacking.IncorrectWorkPieceDataException;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.BasicStackPlateLayout;
import eu.robojob.millassist.external.device.stacking.stackplate.basicstackplate.StudPosition.StudType;
import eu.robojob.millassist.external.device.stacking.stackplate.strategy.basicStackPlate.ABasicStackPlateStrategy;
import eu.robojob.millassist.external.device.stacking.stackplate.strategy.basicStackPlate.RoundPieceBasicStackerStrategy;
import eu.robojob.millassist.workpiece.RoundDimensions;

public class RoundPieceStrategyTest {

	//TODO - create test with finishedPiece as well + test interfering distance
	private static BasicStackPlateLayout context;
	private RoundDimensions rawPiece, finishedPiece;
	private RoundPieceBasicStackerStrategy strategy;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		context = new BasicStackPlateLayout(27, 7, 10.0f, 15.0f, 45.0f, 40.0f, 26.0f, 35.0f, 5.0f, 50, 90.0f, 135.0f, 5.0f, 5.0f, 5.0f, 90.0f, -90.0f);
	}
	
	@Before
	public void before() {
		strategy = new RoundPieceBasicStackerStrategy(context);
		rawPiece = new RoundDimensions(50, 33);
	}
	
	@Test
	public void setupTest() {
		assertEquals(strategy.getStudPositions().length, context.getVerticalHoleAmount());
		assertEquals(strategy.getStudPositions()[0].length, context.getHorizontalHoleAmount());
		assertEquals(strategy.getStudPositions()[1].length, context.getHorizontalHoleAmount());
		assertEquals(strategy.getStudPositions()[2].length, context.getHorizontalHoleAmount());
		assertEquals(strategy.getStudPositions()[3].length, context.getHorizontalHoleAmount());
		assertEquals(strategy.getStudPositions()[4].length, context.getHorizontalHoleAmount());
		assertEquals(strategy.getStudPositions()[5].length, context.getHorizontalHoleAmount());
		assertEquals(strategy.getStudPositions()[6].length, context.getHorizontalHoleAmount());
		for (int i = 0; i < context.getVerticalHoleAmount(); i++) {
			for (int j = 0; j < context.getHorizontalHoleAmount(); j++) {
				assertEquals(strategy.getStudPositions()[i][j].getStudType(), StudType.NONE);
			}
		}
	}

	@Test
	public void calculateYCompenstationTest() {
		try {
			Method method = RoundPieceBasicStackerStrategy.class.getDeclaredMethod("getYCompensation", double.class, double.class, double.class, int.class);
			method.setAccessible(true);
			assertEquals((double) method.invoke(null, 55, 25, 60, 2), 1.042, 0.01);
			assertEquals((double) method.invoke(null, 59, 25, 60, 2), 0.106, 0.01);
			assertEquals((double) method.invoke(null,  55, 25, 30, 3), 1.042, 0.01);
			assertEquals((double) method.invoke(null,  59, 25, 30, 3), 0.106, 0.01);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getNbCoveredStudsHorizontalTest() {
		try {
			Method method = RoundPieceBasicStackerStrategy.class.getDeclaredMethod("getNbStudsCoveredHorizontal", double.class, double.class, double.class);
			method.setAccessible(true);
			assertEquals(2, (int) method.invoke(null, 46, 25, 60));
			assertEquals(2, (int) method.invoke(null, 94, 25, 60));
			assertEquals(2, (int) method.invoke(null, 95, 25, 60));
			assertEquals(3, (int) method.invoke(null, 96, 25, 60));
			assertEquals(3, (int) method.invoke(null, 55, 25, 30));
			assertEquals(2, (int) method.invoke(null, 50, 15, 35));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test 
	public void getMaxHorizontalTest() {
		try {
			Method method = RoundPieceBasicStackerStrategy.class.getDeclaredMethod("calcMaxHorizontalAmount", int.class);
			method.setAccessible(true);
			Method method2 = ABasicStackPlateStrategy.class.getDeclaredMethod("getMaxHorizontalPieces");
			method2.setAccessible(true);

			method.invoke(strategy, 5);
			assertEquals(5, (int) method2.invoke(strategy));
			
			method.invoke(strategy, 13);
			assertEquals(2, (int) method2.invoke(strategy));
			
			method.invoke(strategy, 14);
			assertEquals(1, (int) method2.invoke(strategy));
			
			method.invoke(strategy, 4);
			assertEquals(6, (int) method2.invoke(strategy));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void configureRawPositions() throws IncorrectWorkPieceDataException {

	}


}
