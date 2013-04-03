package eu.robojob.irscw.ui.admin;

import eu.robojob.irscw.ui.general.AbstractMenuView;
import eu.robojob.irscw.ui.general.ContentPresenter;

public abstract class AbstractMenuPresenter<T extends AbstractMenuView<?>> extends eu.robojob.irscw.ui.general.AbstractMenuPresenter<T> {

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
