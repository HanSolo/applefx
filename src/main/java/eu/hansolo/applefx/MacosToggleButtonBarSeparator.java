package eu.hansolo.applefx;

import eu.hansolo.applefx.tools.Helper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.layout.Region;


public class MacosToggleButtonBarSeparator extends Region implements MacosControl {
    private static final PseudoClass     DARK_PSEUDO_CLASS = PseudoClass.getPseudoClass("dark");
    private              boolean         _dark;
    private              BooleanProperty dark;


    // ******************** Constructors **************************************
    public MacosToggleButtonBarSeparator() {
        super();
        init();
    }


    // ******************** Initialization ************************************
    private void init() {
        getStyleClass().add("macos-toggle-button-bar-separator");
        _dark = Helper.isDarkMode();
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
                @Override public Object getBean() { return MacosToggleButtonBarSeparator.this; }
                @Override public String getName() { return "dark"; }
            };
        }
        return dark;
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() { return MacosToggleButtonBarSeparator.class.getResource("apple.css").toExternalForm(); }
}
