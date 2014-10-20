package eu.robojob.millassist.ui.admin;

import eu.robojob.millassist.ui.general.AbstractMenuView;
import eu.robojob.millassist.ui.general.ContentPresenter;
import eu.robojob.millassist.ui.general.SubContentPresenter;

public abstract class AbstractSubMenuPresenter<T extends AbstractMenuView<?>, S extends SubContentPresenter> extends eu.robojob.millassist.ui.general.AbstractMenuPresenter<T> {

	private S parent;

	public AbstractSubMenuPresenter(final T view) {
		super(view);
	}

	public S getParent() {
		return parent;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setParent(final ContentPresenter parent) {
		this.parent = (S) parent;
	}
	
	public abstract boolean isConfigured();

	public abstract void setBlocked(boolean blocked);
}
