package eu.robojob.millassist.external.device.stacking.strategy.pallet;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.robojob.millassist.external.device.stacking.pallet.AbstractPallet;
import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout;
import eu.robojob.millassist.external.device.stacking.pallet.PalletStackingPosition;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPallet;
import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout.PalletLayoutType;
import eu.robojob.millassist.workpiece.RoundDimensions;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Material;
import eu.robojob.millassist.workpiece.WorkPiece.Type;

public class RoundPiecePalletUnloadStrategyTest {
    private AbstractPallet pallet;
    private PalletLayout palletLayout;
    private RoundDimensions workPieceDimensions;
    private WorkPiece workPiece;
    
    private RoundDimensions workPieceDimensions2;
    private WorkPiece workPiece2;
    
    @Before
    public void setUp() throws Exception {
        palletLayout = new PalletLayout("Layout ",1200.0f, 800.0f, 114.0f, 20.0f, 20.0f, 20.0f, 20.0f,0,0);
        pallet = new UnloadPallet("test Pallet");
        workPieceDimensions = new RoundDimensions(50, 50);
        workPiece = new WorkPiece(Type.FINISHED, workPieceDimensions, Material.AL, 3);
        
        workPieceDimensions2 = new RoundDimensions(150, 150);
        workPiece2 = new WorkPiece(Type.FINISHED, workPieceDimensions2, Material.AL, 3);
    }
    
    @Test
    public void test_configuration_round_optimal() {
        palletLayout.setLayoutType(PalletLayoutType.OPTIMAL);
        List<PalletStackingPosition> positions = palletLayout.calculateLayoutForWorkPiece(workPiece);
        assertEquals(233, positions.size());
        
        List<PalletStackingPosition> positions2 = palletLayout.calculateLayoutForWorkPiece(workPiece2);
        assertEquals(33, positions2.size());
    }
    
    @Test
    public void test_configuration_round_shifted_horizontal() {
        palletLayout.setLayoutType(PalletLayoutType.SHIFTED_HORIZONTAL);
        List<PalletStackingPosition> positions = palletLayout.calculateLayoutForWorkPiece(workPiece);
        assertEquals(219, positions.size());
        
        List<PalletStackingPosition> positions2 = palletLayout.calculateLayoutForWorkPiece(workPiece2);
        assertEquals(32, positions2.size());
    }
    
    @Test
    public void test_configuration_round_shifted_vertical() {
        palletLayout.setLayoutType(PalletLayoutType.SHIFTED_VERTICAL);
        List<PalletStackingPosition> positions = palletLayout.calculateLayoutForWorkPiece(workPiece);
        assertEquals(233, positions.size());
        
        List<PalletStackingPosition> positions2 = palletLayout.calculateLayoutForWorkPiece(workPiece2);
        assertEquals(33, positions2.size());
    }
    
    @Test
    public void test_configuration_round_not_shifted() {
        palletLayout.setLayoutType(PalletLayoutType.NOT_SHIFTED_HORIZONTAL);
        List<PalletStackingPosition> positions = palletLayout.calculateLayoutForWorkPiece(workPiece);
        assertEquals(160, positions.size());
        
        List<PalletStackingPosition> positions2 = palletLayout.calculateLayoutForWorkPiece(workPiece2);
        assertEquals(24, positions2.size());
    }
}
