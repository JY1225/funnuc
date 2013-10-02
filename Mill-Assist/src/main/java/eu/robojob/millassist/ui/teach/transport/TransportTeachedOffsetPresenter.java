package eu.robojob.millassist.ui.teach.transport;

import eu.robojob.millassist.positioning.Coordinates;
import eu.robojob.millassist.positioning.TeachedCoordinatesCalculator;
import eu.robojob.millassist.process.AbstractTransportStep;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.ui.general.AbstractFormPresenter;

public class TransportTeachedOffsetPresenter extends AbstractFormPresenter<TransportTeachedOffsetView, TransportMenuPresenter> {

	private AbstractTransportStep transportStep;
	private Coordinates coordinates; 
	private Coordinates position;
	
	public TransportTeachedOffsetPresenter(final TransportTeachedOffsetView view, final AbstractTransportStep transportStep) {
		super(view);
		this.transportStep = transportStep;
		view.build();
		refresh();
	}
	
	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}
	
	public void refresh() {
		coordinates = transportStep.getRelativeTeachedOffset();
		if (coordinates == null) {
			coordinates = new Coordinates(0, 0, 0, 0, 0, 0);
		}
		position = transportStep.getDevice().getLocationOrientation(transportStep.getDeviceSettings().getWorkArea());
		coordinates = TeachedCoordinatesCalculator.calculateAbsoluteOffset(position, coordinates);
		getView().setCoordinates(coordinates);
		getView().refresh();
	}
	
	public void saveAbsoluteOffset(final float x, final float y, final float z, final float w, final float p, final float r) {
		coordinates = new Coordinates(x, y, z, w, p, r);
		Coordinates newRelativeOffset = TeachedCoordinatesCalculator.calculateRelativeTeachedOffset(position, coordinates);
		transportStep.setRelativeTeachedOffset(newRelativeOffset);
		getView().setCoordinates(coordinates);
		getView().refresh();
		transportStep.getProcessFlow().processProcessFlowEvent(new DataChangedEvent(transportStep.getProcessFlow(), transportStep, false));
	}

	@Override
	public boolean isConfigured() {
		return false;
	}
}
