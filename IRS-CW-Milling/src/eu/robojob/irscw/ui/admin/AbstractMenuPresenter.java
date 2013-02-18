package eu.robojob.irscw.ui.admin;

import eu.robojob.irscw.ui.ContentPresenter;
import eu.robojob.irscw.ui.general.AbstractMenuView;

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
}
