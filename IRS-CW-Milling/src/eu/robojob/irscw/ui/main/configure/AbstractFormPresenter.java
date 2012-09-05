package eu.robojob.irscw.ui.main.configure;

import eu.robojob.irscw.ui.controls.TextFieldListener;

public abstract class AbstractFormPresenter<T extends AbstractFormView<?>> {

	protected T view;
		
	public AbstractFormPresenter(T view) {
		this.view = view;
	}
	
	public void setTextFieldListener(TextFieldListener listener) {
		view.setTextFieldListener(listener);
	}
	
	public T getView() {
		return view;
	}
}
