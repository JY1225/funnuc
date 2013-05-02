package eu.robojob.millassist.ui.configure;

import eu.robojob.millassist.ui.general.AbstractMenuView;
import eu.robojob.millassist.ui.general.ContentPresenter;

public abstract class AbstractMenuPresenter<T extends AbstractMenuView<?>> extends eu.robojob.millassist.ui.general.AbstractMenuPresenter<T> {

	private ConfigurePresenter parent;

	public AbstractMenuPresenter(final T view) {
		super(view);
	}

	public ConfigurePresenter getParent() {
		return parent;
	}

	@Override
	public void setParent(final ContentPresenter parent) {
		this.parent = (ConfigurePresenter) parent;
	}
	
	public abstract boolean isConfigured();

	public abstract void setBlocked(boolean blocked);
}
