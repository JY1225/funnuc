package eu.robojob.irscw.ui.configure;

public abstract class AbstractMenuPresenter<T extends AbstractMenuView<?>> {

	protected ConfigurePresenter parent;
	protected T view;
	
	public AbstractMenuPresenter(T view) {
		this.view = view;
		setPresenter();
	}
	
	public void setParent(ConfigurePresenter parent) {
		this.parent = parent;
	}
	
	public abstract void setTextFieldListener(ConfigurePresenter parent);
	
	protected abstract void setPresenter();
	
	public T getView() {
		return view;
	}
	
	public abstract void openFirst();
	
	public abstract void setBlocked(boolean blocked);
	
	public abstract boolean isConfigured();
	
}
