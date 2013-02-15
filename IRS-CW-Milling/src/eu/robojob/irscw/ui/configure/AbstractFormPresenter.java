	package eu.robojob.irscw.ui.configure;

import eu.robojob.irscw.ui.controls.TextInputControlListener;

public abstract class AbstractFormPresenter<T extends AbstractFormView<?>, S extends AbstractMenuPresenter<?>> {

	private T view;
	private S menuPresenter;
		
	public AbstractFormPresenter(final T view) {
		this.view = view;
		setPresenter();
	}
	
	public void setMenuPresenter(final S menuPresenter) {
		this.menuPresenter = menuPresenter;
	}
	
	public void setTextFieldListener(final TextInputControlListener listener) {
		view.setTextFieldListener(listener);
	}
	
	public T getView() {
		return view;
	}
	
	public S getMenuPresenter() {
		return menuPresenter;
	}
	
	public abstract void setPresenter();
	
	public abstract boolean isConfigured();
}
