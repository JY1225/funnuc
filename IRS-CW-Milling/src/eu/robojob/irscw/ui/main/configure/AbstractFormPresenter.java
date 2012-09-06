package eu.robojob.irscw.ui.main.configure;

import eu.robojob.irscw.ui.controls.TextFieldListener;

public abstract class AbstractFormPresenter<T extends AbstractFormView<?>> {

	protected T view;
	protected ConfigurePresenter parent;
		
	public AbstractFormPresenter(T view) {
		this.view = view;
		setPresenter();
	}
	
	public void setTextFieldListener(TextFieldListener listener) {
		view.setTextFieldListener(listener);
	}
	
	public T getView() {
		return view;
	}
	
	public abstract void setPresenter();
	
	public void setParent(ConfigurePresenter parent) {
		this.parent = parent;
	}
}
