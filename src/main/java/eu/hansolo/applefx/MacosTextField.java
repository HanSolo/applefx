package eu.hansolo.applefx;

import eu.hansolo.applefx.tools.Helper;
import eu.hansolo.applefx.tools.MacosAccentColor;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.control.TextField;


public class MacosTextField extends TextField implements MacosControlWithAccentColor {
    private static final PseudoClass                      DARK_PSEUDO_CLASS = PseudoClass.getPseudoClass("dark");
    private              boolean                          _dark;
    private              BooleanProperty                  dark;
    private              MacosAccentColor                 _accentColor;
    private              ObjectProperty<MacosAccentColor> accentColor;


    // ******************** Constructors **************************************
    public MacosTextField() {
        super();
        init();
    }
    public MacosTextField(final String text) {
        super(text);
        init();
    }


    // ******************** Initialization ************************************
    private void init() {
        getStyleClass().add("macos-text-field");
        _dark        = Helper.isDarkMode();
        _accentColor = Helper.getMacosAccentColor();
        pseudoClassStateChanged(DARK_PSEUDO_CLASS, _dark);
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
                @Override public Object getBean() { return MacosTextField.this; }
                @Override public String getName() { return "dark"; }
            };
        }
        return dark;
    }

    @Override public MacosAccentColor getAccentColor() { return null == accentColor ? _accentColor : accentColor.get(); }
    @Override public void setAccentColor(final MacosAccentColor accentColor) {
        if (null == this.accentColor) {
            _accentColor = accentColor;
            String style;
            if (isDark()) {
                style = new StringBuilder().append("-fx-focus-color: ").append(accentColor.getDarkHighlightStyleClass()).append(";")
                                           .append("-fx-faint-focus-color: ").append(accentColor.getDarkHighlightStyleClass()).append(";")
                                           .append("-highlight-color-dark: ").append(accentColor.getDarkHighlightStyleClass()).append(";").toString();
            } else {
                style = new StringBuilder().append("-fx-focus-color: ").append(accentColor.getAquaStyleClass()).append(";")
                                           .append("-highlight-color: ").append(accentColor.getAquaHighlightStyleClass()).append(";").toString();
            }
            setStyle(style);
        } else {
            this.accentColor.set(accentColor);
        }
    }
    @Override public ObjectProperty<MacosAccentColor> accentColorProperty() {
        if (null == accentColor) {
            accentColor = new ObjectPropertyBase<>(_accentColor) {
                @Override protected void invalidated() {
                    String style;
                    if (isDark()) {
                        style = new StringBuilder().append("-fx-focus-color: ").append(get().getDarkStyleClass()).append(";")
                                                   .append("-focus-color-dark: ").append(get().getDarkHighlightStyleClass()).append(";").toString();
                    } else {
                        style = new StringBuilder().append("-fx-focus-color: ").append(get().getAquaStyleClass()).append(";")
                                                   .append("-focus-color: ").append(get().getAquaHighlightStyleClass()).append(";").toString();
                    }
                    setStyle(style);
                }
                @Override public Object getBean() { return MacosTextField.this; }
                @Override public String getName() { return "accentColor"; }
            };
            _accentColor = null;
        }
        return accentColor;
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() { return MacosTextField.class.getResource("apple.css").toExternalForm(); }
}
