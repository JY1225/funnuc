package eu.robojob.millassist.ui.teach;

import java.util.Set;
import java.util.concurrent.Future;

import javafx.application.Platform;
import javafx.scene.control.TextInputControl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.process.AbstractProcessStep;
import eu.robojob.millassist.process.AbstractTransportStep;
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
import eu.robojob.millassist.ui.controls.FullTextField;
import eu.robojob.millassist.ui.controls.IntegerTextField;
import eu.robojob.millassist.ui.controls.NumericTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.controls.keyboard.FullKeyboardPresenter;
import eu.robojob.millassist.ui.controls.keyboard.NumericKeyboardPresenter;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.ui.general.ExecutionPresenter;
import eu.robojob.millassist.ui.general.MainContentView;
import eu.robojob.millassist.ui.general.model.ProcessFlowAdapter;
import eu.robojob.millassist.ui.general.status.DisconnectedDevicesView;
import eu.robojob.millassist.ui.teach.flow.TeachProcessFlowPresenter;
import eu.robojob.millassist.ui.teach.transport.TransportMenuFactory;
import eu.robojob.millassist.util.Translator;

public class TeachPresenter extends ExecutionPresenter implements ProcessFlowListener, TextInputControlListener {

	private ProcessFlowManager processFlowManager;
	private TeachProcessFlowPresenter processFlowPresenter;
	private MainContentView view;
	private DisconnectedDevicesView teachDisconnectedDevicesView;
	private GeneralInfoPresenter generalInfoPresenter;
	private TeachStatusPresenter statusPresenter;
	private TeachThread teachThread;
	private Future<?> teachFuture;
	private TransportMenuFactory transportMenuFactory;
	private ProcessFlowAdapter processFlowAdapter;
	private AbstractMenuPresenter<?> activeMenu;
	private FullKeyboardPresenter keyboardPresenter;
	private NumericKeyboardPresenter numericKeyboardPresenter;
	private boolean keyboardActive;
	private boolean numericKeyboardActive;
	
	private static final String SAVE_OK = "TeachPresenter.saveOK";
	private static final String SAVE_NOK = "TeachPresenter.saveNotOK";
	
	private static final Logger logger = LogManager.getLogger(TeachPresenter.class.getName());
		
	public TeachPresenter(final MainContentView view, final TeachProcessFlowPresenter processFlowPresenter, final ProcessFlow processFlow, final DisconnectedDevicesView disconnectedDevicesView,
			final GeneralInfoPresenter generalInfoPresenter, final TeachStatusPresenter statusPresenter, 
			final ProcessFlowManager processFlowManager, final TransportMenuFactory transportMenuFactory, 
			final FullKeyboardPresenter keyboardPresenter, final NumericKeyboardPresenter numericKeyboardPresenter) {
		super(processFlowPresenter, processFlow, statusPresenter.getStatusPresenter());
		this.processFlowPresenter = processFlowPresenter;
		processFlowPresenter.setParent(this);
		this.view = view;
		view.setTop(processFlowPresenter.getView());
		this.teachDisconnectedDevicesView = disconnectedDevicesView;
		this.generalInfoPresenter = generalInfoPresenter;
		generalInfoPresenter.setParent(this);
		this.statusPresenter = statusPresenter;
		this.processFlowManager = processFlowManager;
		this.transportMenuFactory = transportMenuFactory;
		statusPresenter.setParent(this);
		processFlow.addListener(this);
		this.processFlowAdapter = new ProcessFlowAdapter(processFlow);
		this.keyboardPresenter = keyboardPresenter;
		keyboardPresenter.setParent(this);
		keyboardActive = false;
		numericKeyboardActive = false;
		this.numericKeyboardPresenter = numericKeyboardPresenter;
		numericKeyboardPresenter.setParent(this);
		activeMenu = null;
	}
	
	public void showInfoMessage() {
		this.activeMenu = null;
		getView().setBottom(generalInfoPresenter.getView());
	}
	
	public void closeTransportMenu() {
		activeMenu = null;
		checkAllConnected();	
		processFlowPresenter.setNoneActive();
	}
	
	public boolean showTransportMenu(final int index) {
		activeMenu = transportMenuFactory.getTransportMenu(processFlowAdapter.getTransportInformation(index));
		activeMenu.setParent(this);
		activeMenu.setTextFieldListener(this);
		getView().setBottomLeft(activeMenu.getView());
		activeMenu.openFirst();
		return true;
	}
	
	public void setBottomRightView(final AbstractFormView<?> bottomRight) {
		bottomRight.refresh();
		view.setBottomRight(bottomRight);
	}
	
	public void setBottomRightViewNoRefresh(final AbstractFormView<?> bottomRight) {
		view.setBottomRight(bottomRight);
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
		teachFuture = ThreadManager.submit(teachThread);
	}
	
	public void stopTeaching() {
		if (teachFuture != null) {
			teachFuture.cancel(true);
			teachThread.stopRunning();
		}
	}
	
	@Override
	public MainContentView getView() {
		return view;
	}
	
	public void saveProcess() {
		try {
			getProcessFlow().setChangesSinceLastSave(false);
			processFlowManager.updateProcessFlow(getProcessFlow());
			processFlowPresenter.refresh();
			statusPresenter.getStatusPresenter().getView().setInfoMessage(Translator.getTranslation(SAVE_OK));
		} catch (DuplicateProcessFlowNameException e) {
			e.printStackTrace();
			statusPresenter.getStatusPresenter().getView().setAlarmMessage(Translator.getTranslation(SAVE_NOK));
		}
	}
	
	public void clearTeachedData() {
		for (AbstractProcessStep step: getProcessFlow().getProcessSteps()) {
			if (step instanceof AbstractTransportStep) {
				((AbstractTransportStep) step).setRelativeTeachedOffset(null);
				getProcessFlow().processProcessFlowEvent(new DataChangedEvent(getProcessFlow(), step, true));
			}
		}
	}

	@Override
	public void stopRunning() {
		logger.info("Stop running!");
		if (teachFuture != null) {
			teachFuture.cancel(true);
		}
		checkAllConnected();
	}

	@Override
	public void allConnected() {
		if (!isRunning()) {
			if (activeMenu == null && !getProcessFlow().getMode().equals(Mode.READY)) {
				showInfoMessage();
			}
		} else {
			view.setBottom(statusPresenter.getView());
		}
	}

	@Override
	public void disconnectedDevices(final Set<String> disconnectedDevices) {
		teachDisconnectedDevicesView.setDisconnectedDevices(disconnectedDevices);
		this.activeMenu = null;
		getView().setBottom(teachDisconnectedDevicesView);
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
	
	@Override public void clearMenuBuffer() {
		transportMenuFactory.clearBuffer();
	}

	@Override
	public void modeChanged(final ModeChangedEvent e) {
		if (e.getMode() == Mode.READY) {
			statusPresenter.showSaveButton(true);
			getProcessFlow().setChangesSinceLastSave(true);
		} else {
			statusPresenter.showSaveButton(false);
		}
		if ((e.getMode() == Mode.TEACH) || (e.getMode() == Mode.AUTO)) {
			processFlowPresenter.setRunning(true);
		} else {
			processFlowPresenter.setRunning(false);
			Platform.runLater(new Thread() {
				@Override
				public void run() {
					checkAllConnected();
				}
			});
		}
		Platform.runLater(new Thread() {
			@Override
			public void run() {
				processFlowPresenter.refresh();
			}
		});
	}

	@Override public void statusChanged(final StatusChangedEvent e) { }
	@Override public void dataChanged(final DataChangedEvent e) { }
	@Override public void finishedAmountChanged(final FinishedAmountChangedEvent e) { }
	@Override public void exceptionOccured(final ExceptionOccuredEvent e) { }

	public void textFieldFocussed(final TextInputControl textInputControl) {
		if (textInputControl instanceof FullTextField) {
			this.textFieldFocussed((FullTextField) textInputControl);
		} else if (textInputControl instanceof NumericTextField) {
			this.textFieldFocussed((NumericTextField) textInputControl);
		} else if (textInputControl instanceof IntegerTextField) {
			this.textFieldFocussed((IntegerTextField) textInputControl);
		} else {
			throw new IllegalArgumentException("Unknown keyboard-type [" + textInputControl + "].");
		}
	}

	private void textFieldFocussed(final FullTextField textField) {
		keyboardPresenter.setTarget(textField);
		if (!keyboardActive) {
			view.addNodeToTop(keyboardPresenter.getView());
			keyboardActive = true;
		}
	}
	
	private void textFieldFocussed(final NumericTextField textField) {
		numericKeyboardPresenter.setTarget(textField);
		if (!numericKeyboardActive) {
			view.addNodeToBottomLeft(numericKeyboardPresenter.getView());
			numericKeyboardActive = true;
		}
	}
	
	private void textFieldFocussed(final IntegerTextField textField) {
		numericKeyboardPresenter.setTarget(textField);
		if (!numericKeyboardActive) {
			view.addNodeToBottomLeft(numericKeyboardPresenter.getView());
			numericKeyboardActive = true;
		}
	}

	@Override
	public void textFieldLostFocus(final TextInputControl textInputControl) {
		closeKeyboard();
	}
	
	@Override
	public synchronized void closeKeyboard() {
		if (keyboardActive && numericKeyboardActive) {
			throw new IllegalStateException("Multiple keyboards are active!");
		}
		if (keyboardActive) {
			keyboardActive = false;
			// we assume the keyboard view is always on top
			getView().removeNodeFromTop(keyboardPresenter.getView());
			getView().requestFocus();
		} else if (numericKeyboardActive) {
			numericKeyboardActive = false;
			// we assume the keyboard view is always on top
			getView().removeNodeFromBottomLeft(numericKeyboardPresenter.getView());
			getView().requestFocus();
		}
	}

}
