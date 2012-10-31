package eu.robojob.irscw.ui;

public abstract class AbstractPopUpPresenter<T extends PopUpView<?>> {

	protected T view;
	protected MainPresenter parent;
	
	public AbstractPopUpPresenter(T view) {
		this.view = view;
		setViewPresenter();
	}
	
	protected abstract void setViewPresenter();
	
	public void setParent(MainPresenter parent) {
		this.parent = parent;
	}
	
	public void lostFocus() {
		parent.closePopUp(this);
	}
	
	public T getView() {
		return view;
	}
}
