package eu.robojob.millassist.workpiece;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import eu.robojob.millassist.ui.shape.RectanglePieceRepresentation;
import eu.robojob.millassist.ui.shape.RoundPieceRepresentation;
import eu.robojob.millassist.workpiece.WorkPiece.Dimensions;
import eu.robojob.millassist.workpiece.WorkPiece.Material;
import eu.robojob.millassist.workpiece.WorkPiece.Type;
import eu.robojob.millassist.workpiece.WorkPiece.WorkPieceShape;

public class WorkPieceTest {
	
	private WorkPiece wp1;
	
	@Before
	public void setUp() throws Exception {
		wp1 = new WorkPiece(Type.RAW, new RectangularDimensions(), Material.CU, 0.6f);
	}

	@Test
	public void workPieceConstructor_test() {
		RoundDimensions dim = new RoundDimensions(50, 10);
		WorkPiece wp1 = new WorkPiece(Type.RAW, dim, Material.AL, 0.25f);
		
		assertEquals(Type.RAW, wp1.getType());
		assertEquals(WorkPieceShape.CYLINDRICAL, wp1.getShape());
		assertEquals(50, wp1.getDimensions().getDimension(Dimensions.DIAMETER), 0);
		assertEquals(50, dim.getDiameter(), 0);
		assertEquals(10, wp1.getDimensions().getDimension(Dimensions.HEIGHT), 0);
		assertEquals(10, dim.getHeight(), 0);
		assertEquals(Material.AL, wp1.getMaterial());
	}
	
	@Test
	public void transformPiece_test_newShape() {
		assertEquals(WorkPieceShape.CUBIC, wp1.getShape());
		assertTrue(wp1.getRepresentation() instanceof RectanglePieceRepresentation);
		assertTrue(wp1.getDimensions() instanceof RectangularDimensions);
		wp1.getDimensions().setDimension(Dimensions.WIDTH, 150.1f);
		wp1.transformPiece(WorkPieceShape.CYLINDRICAL);
		
		assertEquals(WorkPieceShape.CYLINDRICAL, wp1.getShape());
		assertTrue(wp1.getRepresentation() instanceof RoundPieceRepresentation);
		assertTrue(wp1.getDimensions() instanceof RoundDimensions);
		assertFalse(150.1f == wp1.getDimensions().getDimension(Dimensions.WIDTH));
		assertEquals(-1, wp1.getDimensions().getDimension(Dimensions.WIDTH),0);
	}
	
	@Test
	public void transformPiece_test_sameShape() {
		assertEquals(WorkPieceShape.CUBIC, wp1.getShape());
		assertTrue(wp1.getRepresentation() instanceof RectanglePieceRepresentation);
		assertTrue(wp1.getDimensions() instanceof RectangularDimensions);
		
		wp1.getDimensions().setDimension(Dimensions.WIDTH, 150.1f);
		wp1.transformPiece(WorkPieceShape.CUBIC);
		
		assertEquals(WorkPieceShape.CUBIC, wp1.getShape());
		assertTrue(wp1.getRepresentation() instanceof RectanglePieceRepresentation);
		assertTrue(wp1.getDimensions() instanceof RectangularDimensions);
		assertEquals(150.1f, wp1.getDimensions().getDimension(Dimensions.WIDTH),0);
	}	
}
