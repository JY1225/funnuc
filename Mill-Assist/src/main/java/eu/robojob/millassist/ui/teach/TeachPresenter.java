package eu.robojob.millassist.ui.teach;

import java.util.Set;

import eu.robojob.millassist.process.DuplicateProcessFlowNameException;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.process.ProcessFlow.Mode;
import eu.robojob.millassist.process.ProcessFlowManager;
import eu.robojob.millassist.process.event.DataChangedEvent;
import eu.robojob.millassist.process.event.ExceptionOccuredEvent;
import eu.robojob.millassist.process.event.FinishedAmountChangedEvent;
import eu.robojob.millassist.process.event.ModeChangedEvent;
import eu.robojob.millassist.process.event.ProcessFlowListener;
import eu.robojob.millassist.process.event.StatusChangedEvent;
import eu.robojob.millassist.process.execution.TeachOptimizedThread;
import eu.robojob.millassist.process.execution.TeachThread;
import eu.robojob.millassist.threading.ThreadManager;
import eu.robojob.millassist.ui.general.ExecutionPresenter;
import eu.robojob.millassist.ui.general.MainContentView;
import eu.robojob.millassist.ui.general.flow.FixedProcessFlowPresenter;
import eu.robojob.millassist.ui.general.status.DisconnectedDevicesView;
import eu.robojob.millassist.util.Translator;

public class TeachPresenter extends ExecutionPresenter implements ProcessFlowListener {

	private ProcessFlowManager processFlowManager;
	private MainContentView view;
	private DisconnectedDevicesView teachDisconnectedDevicesView;
	private GeneralInfoPresenter generalInfoPresenter;
	private TeachStatusPresenter statusPresenter;
	private TeachThread teachThread;
	
	private static final String SAVE_OK = "TeachPresenter.saveOK";
	private static final String SAVE_NOK = "TeachPresenter.saveNotOK";
		
	public TeachPresenter(final MainContentView view, final FixedProcessFlowPresenter processFlowPresenter, final ProcessFlow processFlow, final DisconnectedDevicesView disconnectedDevicesView,
			final GeneralInfoPresenter generalInfoPresenter, final TeachStatusPresenter statusPresenter, 
			final ProcessFlowManager processFlowManager) {
		super(processFlowPresenter, processFlow, statusPresenter.getStatusPresenter());
		this.view = view;
		view.setTop(processFlowPresenter.getView());
		this.teachDisconnectedDevicesView = disconnectedDevicesView;
		this.generalInfoPresenter = generalInfoPresenter;
		generalInfoPresenter.setParent(this);
		this.statusPresenter = statusPresenter;
		this.processFlowManager = processFlowManager;
		statusPresenter.setParent(this);
		processFlow.addListener(this);
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
	
	public void saveProcess() {
		try {
			processFlowManager.updateProcessFlow(getProcessFlow());
			statusPresenter.getStatusPresenter().getView().setInfoMessage(Translator.getTranslation(SAVE_OK));
		} catch (DuplicateProcessFlowNameException e) {
			e.printStackTrace();
			statusPresenter.getStatusPresenter().getView().setAlarmMessage(Translator.getTranslation(SAVE_NOK));
		}
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

	@Override public void startListening(final ProcessFlow processFlow) {
		processFlow.addListener(this);
	}
	
	@Override public void stopListening(final ProcessFlow processFlow) { 
		processFlow.removeListener(this);
	}
	
	@Override public void clearMenuBuffer() { }

	@Override
	public void modeChanged(final ModeChangedEvent e) {
		if (e.getMode() == Mode.READY) {
			statusPresenter.showSaveButton(true);
		} else {
			statusPresenter.showSaveButton(false);
		}
	}

	@Override public void statusChanged(final StatusChangedEvent e) { }
	@Override public void dataChanged(final DataChangedEvent e) { }
	@Override public void finishedAmountChanged(final FinishedAmountChangedEvent e) { }
	@Override public void exceptionOccured(final ExceptionOccuredEvent e) { }

}
