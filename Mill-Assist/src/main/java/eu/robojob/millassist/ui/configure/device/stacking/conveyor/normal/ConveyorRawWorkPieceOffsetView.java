package eu.robojob.millassist.ui.configure.device.stacking.conveyor.normal;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import eu.robojob.millassist.external.device.stacking.conveyor.normal.ConveyorLayout;
import eu.robojob.millassist.ui.controls.NumericTextField;
import eu.robojob.millassist.ui.controls.TextInputControlListener;
import eu.robojob.millassist.ui.general.AbstractFormView;
import eu.robojob.millassist.util.Translator;

public class ConveyorRawWorkPieceOffsetView extends AbstractFormView<ConveyorRawWorkPieceOffsetPresenter> {

	private ConveyorLayout conveyorLayout;
	private HBox hboxOffset1;
	private NumericTextField numtxtOffset1;
	private HBox hboxOffsetOther;
	private NumericTextField numtxtOtherOffset;
	
	private static final int SPACING = 15;
	
	private static final String Y_OFFSET_FIRST = "ConveyorRawWorkPieceOffsetView.yOffsetFirst";
	private static final String Y_OFFSET_OTHER = "ConveyorRawWorkPieceOffsetView.yOffsetOthers"; 
	
	public void setConveyorLayout(final ConveyorLayout conveyorLayout) {
		this.conveyorLayout = conveyorLayout;
	}
	
	@Override
	protected void build() {
		getContents().getChildren().clear();
		getContents().setHgap(SPACING);
		getContents().setVgap(SPACING);
		int row = 0;
		if (conveyorLayout.getRawTrackAmount() > 1) {
			hboxOffsetOther = new HBox();
			hboxOffsetOther.setAlignment(Pos.CENTER_RIGHT);
			hboxOffsetOther.setSpacing(SPACING);
			Label lblYOffset = new Label(Translator.getTranslation(Y_OFFSET_OTHER));
			lblYOffset.setAlignment(Pos.CENTER_RIGHT);
			numtxtOtherOffset = new NumericTextField(6);
			numtxtOtherOffset.setOnChange(new ChangeListener<Float>() {
				@Override
				public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
					getPresenter().changedOffsetOtherSupports(newValue);
				}
			});
			hboxOffsetOther.getChildren().addAll(lblYOffset, numtxtOtherOffset);
			getContents().add(hboxOffsetOther, 0, row++);
		}
		
		hboxOffset1 = new HBox();
		hboxOffset1.setAlignment(Pos.CENTER_RIGHT);
		hboxOffset1.setSpacing(SPACING);
		Label lblYOffset2 = new Label(Translator.getTranslation(Y_OFFSET_FIRST));
		lblYOffset2.setAlignment(Pos.CENTER_RIGHT);
		numtxtOffset1 = new NumericTextField(6);
		numtxtOffset1.setOnChange(new ChangeListener<Float>() {
			@Override
			public void changed(final ObservableValue<? extends Float> observable, final Float oldValue, final Float newValue) {
				getPresenter().changedOffsetSupport1(newValue);
			}
		});
		hboxOffset1.getChildren().addAll(lblYOffset2, numtxtOffset1);
		getContents().add(hboxOffset1, 0, row++);
	}

	@Override
	public void setTextFieldListener(final TextInputControlListener listener) {
		numtxtOffset1.setFocusListener(listener);
		if (numtxtOtherOffset != null) {
			numtxtOtherOffset.setFocusListener(listener);
		}
	}

	@Override
	public void refresh() {
		hboxOffsetOther.setVisible(false);
		hboxOffsetOther.setManaged(false);
		numtxtOffset1.setText(conveyorLayout.getOffsetSupport1() + "");
		numtxtOtherOffset.setText(conveyorLayout.getOffsetOtherSupports() + "");
		for (int i = 1; i < conveyorLayout.getRequestedSupportStatus().length; i++) {
			if (conveyorLayout.getRequestedSupportStatus()[i]) {
				hboxOffsetOther.setVisible(true);
				hboxOffsetOther.setManaged(true);
			}
		}
	}

}
