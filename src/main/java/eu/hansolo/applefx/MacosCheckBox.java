package eu.hansolo.applefx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.control.CheckBox;


public class MacosCheckBox extends CheckBox {
    private static final PseudoClass     DARK_PSEUDO_CLASS = PseudoClass.getPseudoClass("dark");
    private              boolean         _dark;
    private              BooleanProperty dark;


    public MacosCheckBox() {
        super();
        init();
    }
    public MacosCheckBox(final String text) {
        super(text);
        init();
    }


    private void init() {
        getStyleClass().addAll("apple", "macos-check-box");
        _dark = false;
    }


    public final boolean isDark() {
        return null == dark ? _dark : dark.get();
    }
    public final void setDark(final boolean dark) {
        if (null == this.dark) {
            _dark = dark;
            pseudoClassStateChanged(DARK_PSEUDO_CLASS, dark);
        } else {
            darkProperty().set(dark);
        }
    }
    public final BooleanProperty darkProperty() {
        if (null == dark) {
            dark = new BooleanPropertyBase() {
                @Override protected void invalidated() {
                    pseudoClassStateChanged(DARK_PSEUDO_CLASS, get());
                }
                @Override public Object getBean() { return MacosCheckBox.this; }
                @Override public String getName() { return "dark"; }
            };
        }
        return dark;
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() { return MacosButton.class.getResource("apple.css").toExternalForm(); }
}
