package eu.hansolo.applefx;

import eu.hansolo.applefx.tools.Helper;
import eu.hansolo.applefx.tools.MacosAccentColor;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.control.ComboBox;


public class MacosComboBox<T> extends ComboBox<T> implements MacosControlWithAccentColor {
    private static final PseudoClass                      DARK_PSEUDO_CLASS = PseudoClass.getPseudoClass("dark");
    private              BooleanProperty                  dark;
    private              ObjectProperty<MacosAccentColor> accentColor;


    // ******************** Constructors **************************************
    public MacosComboBox() {
        this(FXCollections.<T>observableArrayList());
    }
    public MacosComboBox(final ObservableList<T> items) {
        super(items);
        pseudoClassStateChanged(DARK_PSEUDO_CLASS, Helper.isDarkMode());
        init();
    }


    // ******************** Initialization ************************************
    private void init() {
        this.dark        = new BooleanPropertyBase(Helper.isDarkMode()) {
            @Override protected void invalidated() { pseudoClassStateChanged(DARK_PSEUDO_CLASS, get()); }
            @Override public Object getBean() { return MacosComboBox.this; }
            @Override public String getName() { return "dark"; }
        };
        this.accentColor = new ObjectPropertyBase<>() {
            @Override protected void invalidated() {
                String style;
                if (isDark()) {
                    style = new StringBuilder().append("-accent-color-dark: ").append(get().getDarkStyleClass()).append(";").append("-arrow-button-color: ").append(get().getDarkStyleClass()).toString();
                } else {
                    style = new StringBuilder().append("-accent-color: ").append(get().getAquaStyleClass()).append(";").append("-arrow-button-color: ").append(get().getAquaStyleClass()).toString();
                }
                setStyle(style);
            }
            @Override public Object getBean() { return MacosComboBox.this; }
            @Override public String getName() { return "accentColor"; }
        };
        setCellFactory(param -> new MacosComboBoxCell<>(MacosComboBox.this));
    }


    // ******************** Methods *******************************************
    @Override public final boolean isDark() { return dark.get(); }
    @Override public final void setDark(final boolean dark) { this.dark.set(dark); }
    @Override public final BooleanProperty darkProperty() { return dark; }

    @Override public MacosAccentColor getAccentColor() { return accentColor.get(); }
    @Override public void setAccentColor(final MacosAccentColor accentColor) { this.accentColor.set(accentColor); }
    @Override public ObjectProperty<MacosAccentColor> accentColorProperty() { return accentColor; }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() { return MacosComboBox.class.getResource("apple.css").toExternalForm(); }
}

