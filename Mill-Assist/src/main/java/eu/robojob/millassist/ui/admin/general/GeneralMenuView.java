package eu.robojob.millassist.ui.admin.general;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import eu.robojob.millassist.ui.general.AbstractMenuView;
import eu.robojob.millassist.util.Translator;

public class GeneralMenuView extends AbstractMenuView<GeneralMenuPresenter>{
    private static final String EMAIL = "GeneralMenuView.email";
    
    public GeneralMenuView() {
        build();
    }
    
    @Override
    protected void build() {
        this.getStyleClass().add("admin-menu");
        setPrefWidth(150);
        setMinWidth(150);
        setMaxWidth(150);
        addTextMenuItem(0, Translator.getTranslation(EMAIL), true, new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                getPresenter().configureEmail();
            }
        });
    }
    
    public void setConfigureEmailActive() {
        setMenuItemSelected(0);
    }

}
