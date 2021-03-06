package eu.hansolo.applefx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.geometry.Orientation;
import javafx.scene.control.Control;
import javafx.scene.control.Separator;


public class MacosSeparator extends Separator implements MacosControl {
    private static final PseudoClass     DARK_PSEUDO_CLASS = PseudoClass.getPseudoClass("dark");
    private              boolean         _dark;
    private              BooleanProperty dark;


    // ******************** Constructors **************************************
    public MacosSeparator() {
        super();
        init();
    }
    public MacosSeparator(final Orientation orientation) {
        super(orientation);
        init();
    }


    // ******************** Initialization ************************************
    private void init() {
        getStyleClass().add("macos-separator");
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
                @Override public Object getBean() { return MacosSeparator.this; }
                @Override public String getName() { return "dark"; }
            };
        }
        return dark;
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() { return MacosSeparator.class.getResource("apple.css").toExternalForm(); }
}
