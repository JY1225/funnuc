package eu.robojob.millassist;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.robojob.millassist.external.device.ClampingTest;
import eu.robojob.millassist.external.device.stacking.strategy.pallet.CubicPiecePalletUnloadStrategyTest;
import eu.robojob.millassist.external.device.stacking.strategy.pallet.RoundPiecePalletUnloadStrategyTest;
import eu.robojob.millassist.external.device.stacking.strategy.stackplate.RoundPieceStrategyTest;
import eu.robojob.millassist.external.device.workarea.SimpleWorkAreaTest;
import eu.robojob.millassist.external.device.workarea.WorkAreaManagerDeviceTest;
import eu.robojob.millassist.external.device.workarea.WorkAreaManagerProcessTest;
import eu.robojob.millassist.workpiece.WorkPieceTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
       WorkAreaManagerDeviceTest.class,
       WorkAreaManagerProcessTest.class,
       SimpleWorkAreaTest.class,
       ClampingTest.class,
       CubicPiecePalletUnloadStrategyTest.class,
       RoundPiecePalletUnloadStrategyTest.class,
       RoundPieceStrategyTest.class,
       WorkPieceTest.class
    })
public class MillAssistTestSuite {

}
