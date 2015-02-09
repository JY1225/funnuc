package eu.robojob.millassist.ui.general.dialog;

public class NodeOverlayPresenter extends AbstractDialogPresenter<NodeOverlayView, Boolean> {

	public NodeOverlayPresenter(NodeOverlayView view) {
		super(view);
	}

	@Override
	public void setPresenter() {
		getView().setPresenter(this);
	}

}
