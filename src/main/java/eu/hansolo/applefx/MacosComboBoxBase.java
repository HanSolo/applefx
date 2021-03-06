package eu.hansolo.applefx;

import eu.hansolo.applefx.tools.Helper;
import eu.hansolo.applefx.tools.MacosAccentColor;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.control.ComboBoxBase;


public class MacosComboBoxBase<T> extends ComboBoxBase<T> implements MacosControl {
    private static final PseudoClass                      DARK_PSEUDO_CLASS = PseudoClass.getPseudoClass("dark");
    private              boolean                          _dark;
    private              BooleanProperty                  dark;
    private              MacosAccentColor                 _accentColor;
    private              ObjectProperty<MacosAccentColor> accentColor;


    // ******************** Constructors **************************************
    public MacosComboBoxBase() {
        super();
        init();
    }


    // ******************** Initialization ************************************
    private void init() {
        //getStyleClass().add("macos-combo-box-base");
        _dark        = Helper.isDarkMode();
        _accentColor = Helper.getMacosAccentColor();
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
                @Override public Object getBean() { return MacosComboBoxBase.this; }
                @Override public String getName() { return "dark"; }
            };
        }
        return dark;
    }

    public MacosAccentColor getAccentColor() { return null == accentColor ? _accentColor : accentColor.get(); }
    public void setAccentColor(final MacosAccentColor accentColor) {
        if (null == this.accentColor) {
            _accentColor = accentColor;
            setStyle(isDark() ? new StringBuilder("-accent-color-dark: ").append(accentColor.getDarkStyleClass()).append(";").toString() : new StringBuilder("-accent-color: ").append(accentColor.getAquaStyleClass()).append(";").toString());
        } else {
            this.accentColor.set(accentColor);
        }
    }
    public ObjectProperty<MacosAccentColor> accentColorProperty() {
        if (null == accentColor) {
            accentColor = new ObjectPropertyBase<>(_accentColor) {
                @Override protected void invalidated() { setStyle(isDark() ? new StringBuilder("-accent-color-dark: ").append(get().getDarkStyleClass()).append(";").toString() : new StringBuilder("-accent-color: ").append(get().getAquaStyleClass()).append(";").toString()); }
                @Override public Object getBean() { return MacosComboBoxBase.this; }
                @Override public String getName() { return "accentColor"; }
            };
            _accentColor = null;
        }
        return accentColor;
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() { return MacosComboBoxBase.class.getResource("apple.css").toExternalForm(); }
}