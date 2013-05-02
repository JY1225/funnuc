package eu.robojob.irscw.ui.automate;

import eu.robojob.irscw.ui.general.AbstractMenuView;
import eu.robojob.irscw.ui.general.ContentPresenter;

public abstract class AbstractMenuPresenter<T extends AbstractMenuView<?>> extends eu.robojob.irscw.ui.general.AbstractMenuPresenter<T> {

	private AutomatePresenter parent;
	
	public AbstractMenuPresenter(final T view) {
		super(view);
	}

	public AutomatePresenter getParent() {
		return parent;
	}
	
	@Override
	public void setParent(final ContentPresenter parent) {
		this.parent = (AutomatePresenter) parent;
	}
}
