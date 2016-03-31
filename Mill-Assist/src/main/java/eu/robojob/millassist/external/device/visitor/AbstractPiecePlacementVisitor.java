package eu.robojob.millassist.external.device.visitor;

import java.util.List;

import eu.robojob.millassist.external.device.ClampingManner;
import eu.robojob.millassist.external.device.SimpleWorkArea;
import eu.robojob.millassist.external.device.processing.cnc.AbstractCNCMachine;
import eu.robojob.millassist.external.device.processing.prage.PrageDevice;
import eu.robojob.millassist.external.device.processing.reversal.ReversalUnit;
import eu.robojob.millassist.external.device.stacking.StackingPosition;
import eu.robojob.millassist.external.device.stacking.bin.OutputBin;
import eu.robojob.millassist.external.device.stacking.conveyor.normal.Conveyor;
import eu.robojob.millassist.external.device.stacking.pallet.Pallet;
import eu.robojob.millassist.external.device.stacking.pallet.PalletStackingPosition;
import eu.robojob.millassist.external.device.stacking.pallet.UnloadPallet;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlate;
import eu.robojob.millassist.external.device.stacking.stackplate.AbstractStackPlate.UnloadType;
import eu.robojob.millassist.external.device.stacking.stackplate.StackPlateStackingPosition;
import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;
import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.workpiece.IWorkPieceDimensions;
import eu.robojob.millassist.workpiece.WorkPiece;
import eu.robojob.millassist.workpiece.WorkPiece.Type;

//TODO - toekenning van visitor zit in processFlow
public abstract class AbstractPiecePlacementVisitor<T extends IWorkPieceDimensions> {

    public abstract Coordinates getPutLocation(ReversalUnit reversal, SimpleWorkArea workArea, T dimensions,
            ClampingManner clampType, ApproachType approachType);

    public abstract Coordinates getPutLocation(PrageDevice device, SimpleWorkArea workArea, T dimensions,
            ClampingManner clampType, ApproachType approachType);

    public abstract Coordinates getPutLocation(AbstractCNCMachine cncMachine, SimpleWorkArea workArea, T dimensions,
            ClampingManner clampType, ApproachType approachType);

    public abstract Coordinates getPutLocation(Conveyor conveyor, SimpleWorkArea workArea, T dimensions,
            ClampingManner clampType, ApproachType approachType);

    public abstract Coordinates getPutLocation(
            eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorEaton conveyor,
            SimpleWorkArea workArea, T dimensions, ClampingManner clampType, ApproachType approachType);

    public abstract Coordinates getPickLocation(ReversalUnit reversal, SimpleWorkArea workArea, T dimensions,
            ClampingManner clampType, ApproachType approachType);

    public abstract Coordinates getPickLocation(AbstractCNCMachine cncMachine, SimpleWorkArea workArea, T dimensions,
            ClampingManner clampType, ApproachType approachType);

    public abstract Coordinates getPickLocation(Conveyor conveyor, SimpleWorkArea workArea, T dimensions,
            ClampingManner clampType, ApproachType approachType);

    public abstract Coordinates getPickLocation(
            eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorEaton conveyorEaton,
            SimpleWorkArea workArea, T dimensions, ClampingManner clampType, ApproachType approachType);

    public Coordinates getLocation(final OutputBin bin, final SimpleWorkArea workArea, final Type type, final ClampingManner clampType) {
        return workArea.getDefaultClamping().getRelativePosition();
    }

    public Coordinates getLocation(final AbstractStackPlate stackPlate, final SimpleWorkArea workArea, final Type type,
            final ClampingManner clampType, final UnloadType unloadType) {
        System.out.println(unloadType);
        if(unloadType == UnloadType.STACKWISE) {
            for (StackPlateStackingPosition stackingPos : stackPlate.getLayout().getStackingPositions()) {
                if ((stackingPos.getWorkPiece() != null) && (stackingPos.getWorkPiece().getType() == type)
                        && (stackingPos.getAmount() > 0)) {
                    Coordinates c = new Coordinates(stackingPos.getPickPosition());
                    c.offset(workArea.getDefaultClamping().getRelativePosition());
                    return c;
                }
            }
        } else if(unloadType == UnloadType.LAYERWISE) {
            List<StackPlateStackingPosition> stackPositions = stackPlate.getLayout().getStackingPositions();
            for (StackPlateStackingPosition stackingPos : stackPositions) {
                if ((stackingPos.getWorkPiece() != null) && (stackingPos.getWorkPiece().getType() == type)
                        && (stackingPos.getAmount() == stackPlate.getCurrentLayer())) {
                    Coordinates c = new Coordinates(stackingPos.getPickPosition());
                    c.offset(workArea.getDefaultClamping().getRelativePosition());
                    if(stackPositions.indexOf(stackingPos) == (stackPositions.size()-1)) {
                        stackPlate.decrementCurrentLayer();
                    }
                    return c;
                }
            }
        }
        return null;
    }

    public abstract Coordinates getLocation(Conveyor conveyor, SimpleWorkArea workArea, Type type,
            ClampingManner clampType);

    public abstract Coordinates getLocation(
            eu.robojob.millassist.external.device.stacking.conveyor.eaton.ConveyorEaton conveyorEaton,
            SimpleWorkArea workArea, Type type, ClampingManner clampType);

    /*
     * PRAGE DEVICE
     */

    public Coordinates getPickLocation(final PrageDevice device, final SimpleWorkArea workArea, final T dimensions,
            final ClampingManner clampType, final ApproachType approachType) {
        throw new IllegalStateException("This method should never be called");
    }

    /*
     * OUTPUT BIN
     */

    public Coordinates getPutLocation(final OutputBin bin, final SimpleWorkArea workArea, final T dimensions, final ClampingManner clampType,
            final ApproachType approachType) {
        return workArea.getDefaultClamping().getRelativePosition();
    }

    public Coordinates getPickLocation(final OutputBin bin, final SimpleWorkArea workArea, final T dimensions, final ClampingManner clampType,
            final ApproachType approachType) {
        throw new IllegalStateException("Pick from this device is not possible.");
    }

    /*
     * STACKPLATE
     */
    public Coordinates getPutLocation(final AbstractStackPlate stackPlate, final SimpleWorkArea workArea, final T dimensions,
            final ClampingManner clampType, final ApproachType approachType) {
        for (StackPlateStackingPosition stackingPos : stackPlate.getLayout().getStackingPositions()) {
            if (stackingPos.getWorkPiece() == null) {
                int index = stackPlate.getLayout().getStackingPositions().indexOf(stackingPos);
                stackPlate.getLayout().getStackingPositions()
                .set(index, stackPlate.getLayout().getFinishedStackingPositions().get(index));
                stackingPos = stackPlate.getLayout().getStackingPositions().get(index);
                stackPlate.setCurrentPutLocation(stackingPos);
                Coordinates c = new Coordinates(stackingPos.getPutPosition());
                c.offset(workArea.getDefaultClamping().getRelativePosition());
                return c;
            } else if (stackingPos.getWorkPiece().getType().equals(WorkPiece.Type.FINISHED)
                    && (stackingPos.getAmount() < stackPlate.getLayout().getLayers())) {
                stackPlate.setCurrentPutLocation(stackingPos);
                Coordinates c = new Coordinates(stackingPos.getPutPosition());
                c.offset(workArea.getDefaultClamping().getRelativePosition());
                return c;
            }
        }
        return null;
    }

    public Coordinates getPickLocation(final AbstractStackPlate stackPlate, final SimpleWorkArea workArea, final T dimensions,
            final ClampingManner clampType, final ApproachType approachType, final UnloadType unloadType) {
        System.out.println(unloadType);
        if(unloadType == UnloadType.STACKWISE) {
            for (StackPlateStackingPosition stackingPos : stackPlate.getLayout().getStackingPositions()) {
                if ((stackingPos.getWorkPiece() != null)
                        && (stackingPos.getWorkPiece().getType().equals(WorkPiece.Type.RAW))
                        && (stackingPos.getAmount() > 0)) {
                    // Pick location is correct
                    stackPlate.setCurrentPickLocation(stackingPos);
                    Coordinates c = new Coordinates(stackingPos.getPickPosition());
                    c.offset(workArea.getDefaultClamping().getRelativePosition());
                    return c;
                }
            }
        } else if(unloadType == UnloadType.LAYERWISE){
            List<StackPlateStackingPosition> stackPositions = stackPlate.getLayout().getStackingPositions();
            for (StackPlateStackingPosition stackingPos : stackPositions) {
                if ((stackingPos.getWorkPiece() != null)
                        && (stackingPos.getWorkPiece().getType().equals(WorkPiece.Type.RAW))
                        && (stackingPos.getAmount() == stackPlate.getCurrentLayer())) {
                    // Pick location is correct
                    stackPlate.setCurrentPickLocation(stackingPos);
                    Coordinates c = new Coordinates(stackingPos.getPickPosition());
                    c.offset(workArea.getDefaultClamping().getRelativePosition());
                    if(stackPositions.indexOf(stackingPos) == (stackPositions.size()-1))  {
                        stackPlate.decrementCurrentLayer();
                    }
                    return c;
                }
            }
        }
        return null;
    }

    /*
     * UNLOAD PALLET
     */
    public Coordinates getLocation(final UnloadPallet unloadPallet, final SimpleWorkArea workArea, final Type type,
            final ClampingManner clampType) {
        for (StackingPosition stackingPos : unloadPallet.getPalletLayout().getStackingPositions()) {
            if ((stackingPos.getWorkPiece() == null) && (stackingPos.getWorkPiece().getType() == type)) {
                Coordinates c = new Coordinates(stackingPos.getPosition());
                c.offset(workArea.getDefaultClamping().getRelativePosition());
                return c;
            }
        }
        return null;
    }

    public Coordinates getPutLocation(final UnloadPallet unloadPallet, final SimpleWorkArea workArea, final T dimensions,
            final ClampingManner clampType, final ApproachType approachType) {
        for (PalletStackingPosition stackingPos : unloadPallet.getPalletLayout().getStackingPositions()) {
            if ((stackingPos.getWorkPiece() != null)
                    && stackingPos.getWorkPiece().getType().equals(WorkPiece.Type.FINISHED)
                    && (stackingPos.getAmount() < unloadPallet.getLayers())
                    && (unloadPallet.getWorkPieceAmount(WorkPiece.Type.FINISHED) >= (stackingPos.getAmount()
                            * unloadPallet.getMaxPiecesPerLayerAmount()))) {
                unloadPallet.setCurrentPutLocation(stackingPos);
                Coordinates c = new Coordinates(stackingPos.getPutPosition());
                c.offset(workArea.getDefaultClamping().getRelativePosition());
                if (unloadPallet.getPalletLayout().getLayersBeforeCardBoard() != 0) {
                    c.offset(new Coordinates(0, 0, unloadPallet.getPalletLayout().getPalletHeight()
                            + (unloadPallet.getPalletLayout().getCardBoardThickness()
                                    * (float) Math.floor(stackingPos.getAmount()
                                            / unloadPallet.getPalletLayout().getLayersBeforeCardBoard())), 0, 0, 0));
                } else {
                    c.offset(new Coordinates(0, 0, unloadPallet.getPalletLayout().getPalletHeight(), 0, 0, 0));
                }
                if(clampType.isChanged()) {
                    c.setR(c.getR() + workArea.getWorkAreaManager().getUserFrame().getLocation().getR());
                }
                return c;
            }
        }
        return null;
    }

    public Coordinates getLocation(final Pallet pallet, final SimpleWorkArea workArea, final Type type, final ClampingManner clampType) {
        for (StackPlateStackingPosition stackingPos : pallet.getGridLayout().getStackingPositions()) {
            if ((stackingPos.getWorkPiece() != null) && (stackingPos.getWorkPiece().getType() == type)
                    && (stackingPos.getAmount() > 0)) {
                Coordinates c = new Coordinates(stackingPos.getPickPosition());
                c.offset(workArea.getDefaultClamping().getRelativePosition());
                if(c.getR() == 180) {
                    c.setR(0);
                }
                return c;
            }
        }
        return null;
    }

    public Coordinates getPutLocation(final Pallet pallet, final SimpleWorkArea workArea, final T dimensions, final ClampingManner clampType,
            final ApproachType approachType) {
        for (StackPlateStackingPosition stackingPos : pallet.getGridLayout().getStackingPositions()) {
            if (stackingPos.getWorkPiece() == null) {
                int index = pallet.getGridLayout().getStackingPositions().indexOf(stackingPos);
                pallet.getGridLayout().getStackingPositions()
                .set(index, pallet.getGridLayout().getFinishedStackingPositions().get(index));
                stackingPos = pallet.getGridLayout().getStackingPositions().get(index);
                pallet.setCurrentPutLocation(stackingPos);
                Coordinates c = new Coordinates(stackingPos.getPutPosition());
                c.offset(workArea.getDefaultClamping().getRelativePosition());
                c.offset(new Coordinates(0, 0, pallet.getPalletLayout().getPalletHeight(), 0, 0, 0));
                if(c.getR() == 180) {
                    c.setR(0);
                }
                return c;
            } else if (stackingPos.getWorkPiece().getType().equals(WorkPiece.Type.FINISHED)
                    && (stackingPos.getAmount() < pallet.getGridLayout().getLayers())) {
                pallet.setCurrentPutLocation(stackingPos);
                Coordinates c = new Coordinates(stackingPos.getPutPosition());
                c.offset(workArea.getDefaultClamping().getRelativePosition());
                c.offset(new Coordinates(0, 0, pallet.getPalletLayout().getPalletHeight(), 0, 0, 0));
                if(c.getR() == 180) {
                    c.setR(0);
                }
                return c;
            }
        }
        return null;
    }

    public Coordinates getPickLocation(final Pallet pallet, final SimpleWorkArea workArea, final T dimensions, final ClampingManner clampType,
            final ApproachType approachType) {
        for (StackPlateStackingPosition stackingPos : pallet.getGridLayout().getStackingPositions()) {
            if ((stackingPos.getWorkPiece() != null)
                    && (stackingPos.getWorkPiece().getType().equals(WorkPiece.Type.RAW))
                    && (stackingPos.getAmount() > 0)) {
                // Pick location is correct
                pallet.setCurrentPickLocation(stackingPos);
                Coordinates c = new Coordinates(stackingPos.getPickPosition());
                c.offset(workArea.getDefaultClamping().getRelativePosition());
                c.offset(new Coordinates(0, 0, pallet.getPalletLayout().getPalletHeight(), 0, 0, 0));
                if(c.getR() == 180) {
                    c.setR(0);
                }
                return c;
            }
        }
        return null;
    }

}
