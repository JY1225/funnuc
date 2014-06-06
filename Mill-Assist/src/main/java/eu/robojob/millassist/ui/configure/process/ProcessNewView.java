package eu.robojob.millassist.ui.configure.process;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;
import eu.robojob.millassist.util.UIConstants;

/**
 * Class to represent the submenu that allows users to create a new process
 * 
 * 
 * @author Kristof.Helsen
 *
 */
public class ProcessNewView extends AbstractFormView<ProcessNewPresenter> {
	
	private Button btnNew;
	
	private static final int HGAP = 15;
	private static final int VGAP = 15;
	
	private static final double BTN_HEIGHT = UIConstants.BUTTON_HEIGHT;
	private static final double BTN_WIDTH = BTN_HEIGHT * 3.5;

	private static final String CSS_CLASS_BUTTON_NEW = "btn-new";
	//TODO add translation - Swedish/German
	private static final String NEW = "ProcessNewView.new";
		
	public ProcessNewView() {
		build();
	}

	@Override
	protected void build() {
		
		getContents().setVgap(VGAP);
		getContents().setHgap(HGAP);
		
		btnNew = createButton(ProcessMenuView.NEW_ICON, CSS_CLASS_BUTTON_NEW, Translator.getTranslation(NEW), BTN_WIDTH, BTN_HEIGHT, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().newProcess();
			}
		});
		btnNew.setDisable(false);
		int row = 0;
		int column = 0;
		getContents().add(btnNew, column++, row);
		row++;
		column = 0;		
		setPadding(new Insets(15, 0, 0, 0));
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTextFieldListener(TextInputControlListener listener) {
		// TODO Auto-generated method stub
		
	}

}
