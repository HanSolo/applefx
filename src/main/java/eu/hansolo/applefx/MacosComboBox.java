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


public class MacosComboBox<T> extends ComboBox<T> implements MacosControl {
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
        this.accentColor = new ObjectPropertyBase<>(Helper.getMacosAccentColor()) {
            @Override protected void invalidated() { setStyle(isDark() ? new StringBuilder("-accent-color-dark: ").append(get().getDarkStyleClass()).append(";").toString() : new StringBuilder("-accent-color: ").append(get().getDarkStyleClass()).append(";").toString()); }
            @Override public Object getBean() { return MacosComboBox.this; }
            @Override public String getName() { return "accentColor"; }
        };
        setCellFactory(param -> new MacosComboBoxCell<>(MacosComboBox.this));
    }


    // ******************** Methods *******************************************
    @Override public final boolean isDark() { return dark.get(); }
    @Override public final void setDark(final boolean dark) { this.dark.set(dark); }
    @Override public final BooleanProperty darkProperty() { return dark; }

    public MacosAccentColor getAccentColor() { return accentColor.get(); }
    public void setAccentColor(final MacosAccentColor accentColor) { this.accentColor.set(accentColor); }
    public ObjectProperty<MacosAccentColor> accentColorProperty() { return accentColor; }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() { return MacosComboBox.class.getResource("apple.css").toExternalForm(); }
}

