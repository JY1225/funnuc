package eu.robojob.irscw.ui.main.configure;

public abstract class AbstractMenuPresenter<T extends AbstractMenuView<?>> {

	protected ConfigurePresenter parent;
	protected T view;
	
	public AbstractMenuPresenter(T view) {
		this.view = view;
	}
	
	public void setParent(ConfigurePresenter parent) {
		this.parent = parent;
	}
}
