package eu.robojob.irscw.ui.main.configure;

import javafx.scene.layout.GridPane;
import eu.robojob.irscw.ui.controls.TextFieldListener;

public abstract class AbstractFormView<T extends AbstractFormPresenter<?>> extends GridPane {

	protected T presenter;
	
	public AbstractFormView() {
		super();
		build();
	}
	
	protected abstract void build();
	
	public abstract void setTextFieldListener(TextFieldListener listener);
	
	public void setPresenter(T presenter) {
		this.presenter = presenter;
	}
}
