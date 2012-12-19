package eu.robojob.irscw.ui;

public abstract class AbstractPopUpPresenter<T extends PopUpView<?>> {

	private T view;
	private MainPresenter parent;
	
	public AbstractPopUpPresenter(final T view) {
		this.view = view;
		setViewPresenter();
	}
	
	protected abstract void setViewPresenter();
	
	public void setParent(final MainPresenter parent) {
		this.parent = parent;
	}
	
	public void lostFocus() {
		parent.closePopUps();
	}
	
	public T getView() {
		return view;
	}
	
	public MainPresenter getParent() {
		return parent;
	}
}
