package eu.robojob.irscw.ui.main.configure;

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
	
	protected abstract void setPresenter();
}
