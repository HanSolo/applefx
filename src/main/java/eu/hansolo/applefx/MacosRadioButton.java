package eu.hansolo.applefx;

import eu.hansolo.applefx.tools.Helper;
import eu.hansolo.applefx.tools.MacosAccentColor;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.control.RadioButton;


public class MacosRadioButton extends RadioButton implements MacosControlWithAccentColor {
    private static final PseudoClass                      DARK_PSEUDO_CLASS = PseudoClass.getPseudoClass("dark");
    private              boolean                          _dark;
    private              BooleanProperty                  dark;
    private              MacosAccentColor                 _accentColor;
    private              ObjectProperty<MacosAccentColor> accentColor;


    // ******************** Constructors **************************************
    public MacosRadioButton() {
        super();
        init();
    }
    public MacosRadioButton(final String text) {
        super(text);
        init();
    }


    // ******************** Initialization ************************************
    private void init() {
        getStyleClass().add("macos-radio-button");
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
                @Override public Object getBean() { return MacosRadioButton.this; }
                @Override public String getName() { return "dark"; }
            };
        }
        return dark;
    }

    @Override public MacosAccentColor getAccentColor() { return null == accentColor ? _accentColor : accentColor.get(); }
    @Override public void setAccentColor(final MacosAccentColor accentColor) {
        if (null == this.accentColor) {
            _accentColor = accentColor;
            setStyle(isDark() ? new StringBuilder("-dot-fill: ").append(accentColor.getDarkStyleClass()).append(";").toString() : new StringBuilder("-dot-fill: ").append(accentColor.getAquaStyleClass()).append(";").toString());
        } else {
            this.accentColor.set(accentColor);
        }
    }
    @Override public ObjectProperty<MacosAccentColor> accentColorProperty() {
        if (null == accentColor) {
            accentColor = new ObjectPropertyBase<>(_accentColor) {
                @Override protected void invalidated() { setStyle(isDark() ? new StringBuilder("-dot-fill: ").append(get().getDarkStyleClass()).append(";").toString() : new StringBuilder("-dot-fill: ").append(get().getAquaStyleClass()).append(";").toString()); }
                @Override public Object getBean() { return MacosRadioButton.this; }
                @Override public String getName() { return "accentColor"; }
            };
            _accentColor = null;
        }
        return accentColor;
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() { return MacosRadioButton.class.getResource("apple.css").toExternalForm(); }
}
