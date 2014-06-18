package eu.robojob.millassist.ui.configure.process;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import eu.robojob.millassist.ui.general.AbstractMenuView;
import eu.robojob.millassist.util.Translator;

public class ProcessMenuView extends AbstractMenuView<ProcessMenuPresenter> {

	private ProcessMenuPresenter presenter;
	
	private static final String SAVE_ICON = "M 10 0 C 4.4775 -5.7824116e-019 0 4.4787497 0 10 C 0 15.52375 4.4775 20 10 20 C 15.5225 20 20 15.52375 20 10 C 20 4.4787497 15.5225 0 10 0 z M 8.75 5 L 11.25 5 L 11.25 10.03125 L 14.96875 10.03125 L 10 15 L 5 10.03125 L 8.75 10.03125 L 8.75 5 z ";
	private static final String CONFIGURE_ICON = "M 2.5 0.0625 C 1.81125 0.0625 1.25 0.621875 1.25 1.3125 L 1.25 7.5625 L 0 7.5625 L 0 10.0625 L 1.25 10.0625 L 1.25 18.8125 C 1.25 19.503125 1.81125 20.0625 2.5 20.0625 C 3.193125 20.0625 3.75 19.503125 3.75 18.8125 L 3.75 10.0625 L 5 10.0625 L 5 7.5625 L 3.75 7.5625 L 3.75 1.3125 C 3.75 0.621875 3.193125 0.0625 2.5 0.0625 z M 10 0.0625 C 9.3112496 0.0625 8.75 0.621875 8.75 1.3125 L 8.75 12.5625 L 7.5 12.5625 L 7.5 15.0625 L 8.75 15.0625 L 8.75 18.8125 C 8.75 19.503125 9.3112496 20.0625 10 20.0625 C 10.693125 20.0625 11.25 19.503125 11.25 18.8125 L 11.25 15.0625 L 12.5 15.0625 L 12.5 12.5625 L 11.25 12.5625 L 11.25 1.3125 C 11.25 0.621875 10.693125 0.0625 10 0.0625 z M 17.5 0.0625 C 16.81125 0.0625 16.25 0.621875 16.25 1.3125 L 16.25 5.0625 L 15 5.0625 L 15 7.5625 L 16.25 7.5625 L 16.25 18.8125 C 16.25 19.503125 16.81125 20.0625 17.5 20.0625 C 18.193125 20.0625 18.75 19.503125 18.75 18.8125 L 18.75 7.5625 L 20 7.5625 L 20 5.0625 L 18.75 5.0625 L 18.75 1.3125 C 18.75 0.621875 18.193125 0.0625 17.5 0.0625 z";
	private static final String OPEN_ICON = "M 10 0 C 4.4775 0 0 4.4762499 0 10 C 0 15.52125 4.4775 20 10 20 C 15.5225 20 20 15.52125 20 10 C 20 4.4762499 15.5225 0 10 0 z M 10 5 L 14.96875 9.96875 L 11.25 9.96875 L 11.25 15 L 8.75 15 L 8.75 9.96875 L 5 9.96875 L 10 5 z ";
	static final String NEW_ICON = "M 2.5 0 L 2.5 20 L 17.5 20 L 17.5 6.25 L 11.25 0 L 2.5 0 z M 5 2.5 L 10 2.5 L 10 7.5 L 15 7.5 L 15 17.5 L 5 17.5 L 5 2.5 z";
	
	private static final int CONFIGURE_INDEX = 0;
	private static final int SAVE_INDEX = 1;
	private static final int OPEN_INDEX = 2;
	private static final int NEW_INDEX = 3;
	
	private static final String CONFIGURE = "ProcessMenuView.configureProcess";
	private static final String SAVE = "ProcessMenuView.saveData";
	private static final String OPEN = "ProcessMenuView.openProcess";
	private static final String NEW = "ProcessMenuView.newProcess";
	
	public ProcessMenuView() {
		super();
		build();
	}
	
	public void setPresenter(final ProcessMenuPresenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	protected void build() {
		addMenuItem(CONFIGURE_INDEX, CONFIGURE_ICON, Translator.getTranslation(CONFIGURE), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				presenter.configureProcess();
			}
		});
		
		addMenuItem(SAVE_INDEX, SAVE_ICON, Translator.getTranslation(SAVE), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				presenter.saveData();
			}
		});
				
		addMenuItem(OPEN_INDEX, OPEN_ICON, Translator.getTranslation(OPEN), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				presenter.openProcess();
			}
		});
				
		addMenuItem(NEW_INDEX, NEW_ICON, Translator.getTranslation(NEW), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				presenter.newProcess();
			}
		});
	}
	
	public void setConfigureActive() {
		setMenuItemSelected(CONFIGURE_INDEX);
	}
	
	public void setSaveActive() {
		setMenuItemSelected(SAVE_INDEX);
	}
	
	public void setOpenActive() {
		setMenuItemSelected(OPEN_INDEX);
	}
	
	public void setNewActive() {
		setMenuItemSelected(NEW_INDEX);
	}
	
}
