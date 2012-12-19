package eu.robojob.irscw.ui.configure;

public abstract class AbstractMenuPresenter<T extends AbstractMenuView<?>> {

	private ConfigurePresenter parent;
	private T view;
	
	public AbstractMenuPresenter(final T view) {
		this.view = view;
		setPresenter();
	}
	
	public void setParent(final ConfigurePresenter parent) {
		this.parent = parent;
	}
	
	public abstract void setTextFieldListener(ConfigurePresenter parent);
	
	protected abstract void setPresenter();
	
	public T getView() {
		return view;
	}
	
	public ConfigurePresenter getParent() {
		return parent;
	}
	
	public abstract void openFirst();
	
	public abstract void setBlocked(boolean blocked);
	
	public abstract boolean isConfigured();
	
}
