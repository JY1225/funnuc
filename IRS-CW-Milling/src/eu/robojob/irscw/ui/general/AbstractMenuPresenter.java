package eu.robojob.irscw.ui.general;

import eu.robojob.irscw.ui.MainContentPresenter;
import eu.robojob.irscw.ui.controls.TextInputControlListener;

public abstract class AbstractMenuPresenter<T extends AbstractMenuView<?>> {

	private T view;
	
	public AbstractMenuPresenter(final T view) {
		this.view = view;
		setPresenter();
	}
	
	public abstract void setTextFieldListener(TextInputControlListener listener);
	
	protected abstract void setPresenter();
	
	public T getView() {
		return view;
	}
	
	public abstract void openFirst();
	
	public abstract void setBlocked(boolean blocked);
	
	public abstract boolean isConfigured();
	
	public abstract void setParent(final MainContentPresenter parent);
}
