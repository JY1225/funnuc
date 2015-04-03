package eu.robojob.millassist.external.device.stacking.strategy.pallet;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout;
import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout.PalletLayoutType;
import eu.robojob.millassist.external.device.stacking.pallet.PalletStackingPosition;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPallet;
import eu.robojob.millassist.workpiece.RectangularDimensions;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Material;
import eu.robojob.millassist.workpiece.WorkPiece.Type;

public class CubicPiecePalletUnloadStrategyTest extends TestCase {
    private UnloadPallet pallet;
    private PalletLayout palletLayout;
    private RectangularDimensions workPieceDimensions;
    private WorkPiece workPiece;
    
    private RectangularDimensions workPieceDimensions2;
    private WorkPiece workPiece2;
    
    @Before
    public void setUp() throws Exception {
        palletLayout = new PalletLayout(1200.0f, 800.0f, 114.0f, 40.0f, 40.0f, 20.0f, 20.0f,0,90);
        
        pallet = new UnloadPallet("test Pallet", palletLayout);
        workPieceDimensions = new RectangularDimensions(50, 50, 50);
        workPiece = new WorkPiece(Type.FINISHED, workPieceDimensions, Material.AL, 3);
        
        workPieceDimensions2 = new RectangularDimensions(100, 70, 150);
        workPiece2 = new WorkPiece(Type.FINISHED, workPieceDimensions2, Material.AL, 3);
    }
    
    @Test
    public void test_configuration_rectangular_optimal() {
        palletLayout.setLayoutType(PalletLayoutType.OPTIMAL);
        
        List<PalletStackingPosition> positions = palletLayout.calculateLayoutForWorkPiece(workPiece);
        assertEquals(128, positions.size());
        
        List<PalletStackingPosition> positions2 = palletLayout.calculateLayoutForWorkPiece(workPiece2);
        assertEquals(60, positions2.size());
    }
    
    @Test
    public void test_configuration_rectangular_horizontal() {
        palletLayout.setLayoutType(PalletLayoutType.NOT_SHIFTED_HORIZONTAL);
        
        List<PalletStackingPosition> positions = palletLayout.calculateLayoutForWorkPiece(workPiece);
        assertEquals(128, positions.size());
        
        List<PalletStackingPosition> positions2 = palletLayout.calculateLayoutForWorkPiece(workPiece2);
        assertEquals(60, positions2.size());
    }
    
    @Test
    public void test_configuration_rectangular_vertical() {
        palletLayout.setLayoutType(PalletLayoutType.NOT_SHIFTED_VERTICAL);
        
        List<PalletStackingPosition> positions = palletLayout.calculateLayoutForWorkPiece(workPiece);
        assertEquals(128, positions.size());
        
        List<PalletStackingPosition> positions2 = palletLayout.calculateLayoutForWorkPiece(workPiece2);
        assertEquals(54, positions2.size());
    }
}
