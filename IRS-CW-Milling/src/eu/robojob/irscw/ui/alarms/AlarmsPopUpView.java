package eu.robojob.irscw.ui.alarms;

import eu.robojob.irscw.ui.general.PopUpView;

public class AlarmsPopUpView extends PopUpView<AlarmsPopUpPresenter> {

	private static final int WIDTH = 400;
	private static final int HEIGHT = 240;
	
	public AlarmsPopUpView() {
		super(0, 0, WIDTH, HEIGHT);
		this.getStyleClass().add("alarms-popup");
	}

}
