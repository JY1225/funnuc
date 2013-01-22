package eu.robojob.irscw.ui.teach;

import java.util.Set;

import eu.robojob.irscw.process.ProcessFlow;
import eu.robojob.irscw.process.execution.TeachOptimizedThread;
import eu.robojob.irscw.process.execution.TeachThread;
import eu.robojob.irscw.threading.ThreadManager;
import eu.robojob.irscw.ui.MainContentView;
import eu.robojob.irscw.ui.general.ExecutionPresenter;
import eu.robojob.irscw.ui.general.flow.FixedProcessFlowPresenter;
import eu.robojob.irscw.ui.general.status.DisconnectedDevicesView;

public class TeachPresenter extends ExecutionPresenter {

	private MainContentView view;
	private DisconnectedDevicesView teachDisconnectedDevicesView;
	private GeneralInfoPresenter generalInfoPresenter;
	private TeachStatusPresenter statusPresenter;
	private TeachThread teachThread;
		
	public TeachPresenter(final MainContentView view, final FixedProcessFlowPresenter processFlowPresenter, final ProcessFlow processFlow, final DisconnectedDevicesView disconnectedDevicesView,
			final GeneralInfoPresenter generalInfoPresenter, final TeachStatusPresenter statusPresenter) {
		super(processFlowPresenter, processFlow, statusPresenter.getStatusPresenter());
		this.view = view;
		view.setTop(processFlowPresenter.getView());
		this.teachDisconnectedDevicesView = disconnectedDevicesView;
		this.generalInfoPresenter = generalInfoPresenter;
		generalInfoPresenter.setParent(this);
		this.statusPresenter = statusPresenter;
		statusPresenter.setParent(this);
	}
	
	public void showInfoMessage() {
		view.setBottom(generalInfoPresenter.getView());
	}
	
	public void startTeachOptimal() {
		startTeaching(new TeachOptimizedThread(getProcessFlow()));
	}
	
	public void startTeachAll() {
		startTeaching(new TeachThread(getProcessFlow()));
	}
	
	private void startTeaching(final TeachThread teachThread) {
		statusPresenter.initializeView();
		view.setBottom(statusPresenter.getView());
		updateAlarms();
		if ((this.teachThread != null) && (this.teachThread.isRunning())) {
			throw new IllegalStateException("Teach thread was already running: " + teachThread);
		}
		this.teachThread = teachThread;
		ThreadManager.submit(teachThread);
	}
	
	public void stopTeaching() {
		if (teachThread.isRunning()) {
			ThreadManager.stopRunning(teachThread);
		}
		checkAllConnected();
	}
	
	@Override
	public MainContentView getView() {
		return view;
	}

	@Override
	public void stopRunning() {
		if (teachThread.isRunning()) {
			teachThread.interrupt();
		}
	}

	@Override
	public void allConnected() {
		if (!isRunning()) {
			showInfoMessage();
		}
	}

	@Override
	public void disconnectedDevices(final Set<String> disconnectedDevices) {
		if (!isRunning()) {
			teachDisconnectedDevicesView.setDisconnectedDevices(disconnectedDevices);
			view.setBottom(teachDisconnectedDevicesView);
		}
	}

	@Override
	public boolean isRunning() {
		if ((teachThread != null) && (teachThread.isRunning())) {
			return true;
		}
		return false;
	}

	@Override public void startListening(final ProcessFlow processFlow) { }
	@Override public void stopListening(final ProcessFlow processFlow) { }
	
}
