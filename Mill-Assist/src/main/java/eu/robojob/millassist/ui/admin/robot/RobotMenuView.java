package eu.robojob.millassist.ui.admin.robot;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import eu.robojob.millassist.ui.general.AbstractMenuView;
import eu.robojob.millassist.util.Translator;

public class RobotMenuView extends AbstractMenuView<RobotMenuPresenter> {

	private static final String GENERAL = "RobotMenuView.general";
	private static final String GRIPPERS = "RobotMenuView.grippers";

	public RobotMenuView() {
		build();
	}
	
	@Override
	protected void build() {
		this.getStyleClass().add("admin-menu");
		setPrefWidth(150);
		setMinWidth(150);
		setMaxWidth(150);
		addTextMenuItem(0, Translator.getTranslation(GENERAL), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().configureGeneral();
			}
		});
		addTextMenuItem(1, Translator.getTranslation(GRIPPERS), true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				getPresenter().configureGrippers();
			}
		});
	}

	public void setConfigureGeneralActive() {
		setMenuItemSelected(0);
	}
	
	public void setConfigureGrippersActive() {
		setMenuItemSelected(1);
	}
}
