package eu.robojob.irscw.ui.admin;

public abstract class AbstractMenuPresenter<T extends AbstractMenuView<?>> {
	
	private AdminPresenter parent;
	private T view;
	
	public AbstractMenuPresenter(final T view) {
		this.view = view;
		setPresenter();
	}
	
	public void setParent(final AdminPresenter parent) {
		this.parent = parent;
	}
	
	public abstract void setTextFieldListener(AdminPresenter parent);
	
	protected abstract void setPresenter();
	
	public T getView() {
		return view;
	}
	
	public AdminPresenter getParent() {
		return parent;
	}
	
	public abstract void openFirst();
	
	public abstract void setBlocked(boolean blocked);
	
	public abstract boolean isConfigured();
}
