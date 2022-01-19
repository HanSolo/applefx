package eu.hansolo.applefx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Label;


public class MacosLabel extends Label implements MacosControl {
    private static final PseudoClass     DARK_PSEUDO_CLASS = PseudoClass.getPseudoClass("dark");
    private              boolean         _dark;
    private              BooleanProperty dark;


    // ******************** Constructors **************************************
    public MacosLabel() {
        super();
        init();
    }
    public MacosLabel(final String text) {
        super(text);
        init();
    }
    public MacosLabel(final String text, final Node graphic) {
        super(text, graphic);
        init();
    }


    // ******************** Initialization ************************************
    private void init() {
        getStyleClass().add("macos-label");
        _dark = false;
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
                @Override public Object getBean() { return MacosLabel.this; }
                @Override public String getName() { return "dark"; }
            };
        }
        return dark;
    }
}
