package eu.hansolo.applefx;

import eu.hansolo.applefx.tools.Helper;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;


public class MacosToggleButton extends ToggleButton implements MacosControl {
    private static final PseudoClass     DARK_PSEUDO_CLASS = PseudoClass.getPseudoClass("dark");
    private              boolean         _dark;
    private              BooleanProperty dark;
    private              BooleanBinding  showing;


    // ******************** Constructors **************************************
    public MacosToggleButton() {
        super();
        init();
    }
    public MacosToggleButton(final String text) {
        super(text);
        init();
    }
    public MacosToggleButton(final String text, final Node graphic) {
        super(text, graphic);
        init();
    }


    // ******************** Initialization ************************************
    private void init() {
        getStyleClass().add("macos-toggle-button");
        _dark = Helper.isDarkMode();
        registerListener();
    }

    private void registerListener() {
        addEventFilter(MouseEvent.MOUSE_PRESSED, e -> { if (isSelected()) { e.consume(); }});

        if (null != getScene()) {
            setupBinding();
        } else {
            sceneProperty().addListener((o1, ov1, nv1) -> {
                if (null == nv1) { return; }
                if (null != getScene().getWindow()) {
                    setupBinding();
                } else {
                    sceneProperty().get().windowProperty().addListener((o2, ov2, nv2) -> {
                        if (null == nv2) { return; }
                        setupBinding();
                    });
                }
            });
        }
    }

    private void setupBinding() {
        showing = Bindings.createBooleanBinding(() -> {
            if (getScene() != null && getScene().getWindow() != null) {
                return getScene().getWindow().isShowing();
            } else {
                return false;
            }
        }, sceneProperty(), getScene().windowProperty(), getScene().getWindow().showingProperty());
        showing.addListener(o -> {
            if (showing.get()) {
                getScene().getWindow().addEventFilter(KeyEvent.KEY_PRESSED, e -> { if (e.getCode().equals(KeyCode.SPACE) && isSelected()) { e.consume(); }});
            }
        });
    }


    // ******************** Methods *******************************************
    @Override public final boolean isDark() {
        return null == dark ? _dark : dark.get();
    }
    @Override public final void setDark(final boolean dark) {
        if (null == this.dark) {
            _dark = dark;
            pseudoClassStateChanged(DARK_PSEUDO_CLASS, dark);
        } else {
            this.dark.set(dark);
        }
    }
    @Override public final BooleanProperty darkProperty() {
        if (null == dark) {
            dark = new BooleanPropertyBase() {
                @Override protected void invalidated() {
                    pseudoClassStateChanged(DARK_PSEUDO_CLASS, get());
                }
                @Override public Object getBean() { return MacosToggleButton.this; }
                @Override public String getName() { return "dark"; }
            };
        }
        return dark;
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() { return MacosToggleButton.class.getResource("apple.css").toExternalForm(); }
}
