package eu.robojob.irscw.ui.admin;

import eu.robojob.irscw.ui.ContentPresenter;
import eu.robojob.irscw.ui.SubContentPresenter;
import eu.robojob.irscw.ui.general.AbstractMenuView;

public abstract class AbstractSubMenuPresenter<T extends AbstractMenuView<?>, S extends SubContentPresenter> extends eu.robojob.irscw.ui.general.AbstractMenuPresenter<T> {

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
}
