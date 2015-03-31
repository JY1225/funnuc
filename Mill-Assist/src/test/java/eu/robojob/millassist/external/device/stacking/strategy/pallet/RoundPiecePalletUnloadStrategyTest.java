package eu.robojob.millassist.external.device.stacking.strategy.pallet;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.robojob.millassist.external.device.stacking.pallet.PalletLayout;
import eu.robojob.millassist.external.device.stacking.pallet.PalletStackingPosition;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPallet;
import eu.robojob.millassist.workpiece.RoundDimensions;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Material;
import eu.robojob.millassist.workpiece.WorkPiece.Type;

public class RoundPiecePalletUnloadStrategyTest {
    private UnloadPallet pallet;
    private PalletLayout palletLayout;
    private RoundDimensions workPieceDimensions;
    private WorkPiece workPiece;
    
    private RoundDimensions workPieceDimensions2;
    private WorkPiece workPiece2;
    
    @Before
    public void setUp() throws Exception {
        palletLayout = new PalletLayout(1200.0f, 800.0f, 20.0f, 20.0f, 20.0f, 20.0f,0,0);
        pallet = new UnloadPallet("test Pallet", palletLayout);
        workPieceDimensions = new RoundDimensions(50, 50);
        workPiece = new WorkPiece(Type.FINISHED, workPieceDimensions, Material.AL, 3);
        
        workPieceDimensions2 = new RoundDimensions(150, 150);
        workPiece2 = new WorkPiece(Type.FINISHED, workPieceDimensions2, Material.AL, 3);
    }
    
    @Test
    public void test_configuration() {
        List<PalletStackingPosition> positions = palletLayout.calculateLayoutForWorkPiece(workPiece);
        assertEquals(160, positions.size());
//        
//        List<StackingPosition> positions2 = palletLayout.calculateLayoutForWorkPiece(workPiece2);
//        assertEquals(24, positions2.size());
    }
}
