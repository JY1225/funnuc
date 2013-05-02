package eu.robojob.millassist.ui.admin;

import eu.robojob.millassist.ui.general.AbstractMenuView;
import eu.robojob.millassist.ui.general.ContentPresenter;

public abstract class AbstractMenuPresenter<T extends AbstractMenuView<?>> extends eu.robojob.millassist.ui.general.AbstractMenuPresenter<T> {

	private AdminPresenter parent;

	public AbstractMenuPresenter(final T view) {
		super(view);
	}

	public AdminPresenter getParent() {
		return parent;
	}

	@Override
	public void setParent(final ContentPresenter parent) {
		this.parent = (AdminPresenter) parent;
	}
	
	public abstract boolean isConfigured();

	public abstract void setBlocked(boolean blocked);
}
