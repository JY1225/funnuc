package eu.robojob.millassist.ui.general.dialog;

public abstract class AbstractDialogPresenter<T extends AbstractDialogView<?>, S extends Object> {

	private T view;
	private S result;
	private Object syncObject;
	
	public AbstractDialogPresenter(final T view) {
		this.view = view;
		syncObject = new Object();
		setPresenter();
	}
	
	public abstract void setPresenter();
	
	public S getResult() throws InterruptedException {
		synchronized(syncObject) {
			syncObject.wait();
		}
		return result;
	}
	
	public void setResult(final S result) {
		this.result = result;
		synchronized(syncObject) {
			syncObject.notify();
		}
	}
	
	public T getView() {
		return view;
	}
	
	public void setTitle(final String title) {
		view.setTitle(title);
	}
}
