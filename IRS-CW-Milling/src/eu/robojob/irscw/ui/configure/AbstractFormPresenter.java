package eu.robojob.irscw.ui.configure;

import eu.robojob.irscw.ui.controls.TextFieldListener;

public abstract class AbstractFormPresenter<T extends AbstractFormView<?>, S extends AbstractMenuPresenter<?>> {

	protected T view;
	protected S menuPresenter;
		
	public AbstractFormPresenter(T view) {
		this.view = view;
		setPresenter();
	}
	
	public void setMenuPresenter(S menuPresenter) {
		this.menuPresenter = menuPresenter;
	}
	
	public void setTextFieldListener(TextFieldListener listener) {
		view.setTextFieldListener(listener);
	}
	
	public T getView() {
		return view;
	}
	
	public abstract void setPresenter();
	
	public abstract boolean isConfigured();
}
