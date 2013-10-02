package eu.robojob.millassist.ui.teach;

import eu.robojob.millassist.ui.general.AbstractMenuView;
import eu.robojob.millassist.ui.general.ContentPresenter;

public abstract class AbstractMenuPresenter<T extends AbstractMenuView<?>> extends eu.robojob.millassist.ui.general.AbstractMenuPresenter<T> {

	private TeachPresenter parent;
	
	public AbstractMenuPresenter(final T view) {
		super(view);
	}
	
	public TeachPresenter getParent() {
		return parent;
	}
	
	@Override
	public void setParent(final ContentPresenter parent) {
		this.parent = (TeachPresenter) parent;
	}
	
}
