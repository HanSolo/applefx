package eu.hansolo.applefx;

import eu.hansolo.applefx.tools.Helper;
import eu.hansolo.applefx.tools.MacosAccentColor;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class MacosComboBoxCell<T> extends ListCell<T> implements MacosControl {
    private static final ImageView                        EMPTY_CHECK_MARK  = new ImageView(new Image(MacosComboBoxCell.class.getResourceAsStream("macos-checkmark-empty.png"), 10, 10, true, false));
    private static final ImageView                        BLACK_CHECK_MARK  = new ImageView(new Image(MacosComboBoxCell.class.getResourceAsStream("macos-checkmark-black.png"), 10, 10, true, false));
    private static final ImageView                        WHITE_CHECK_MARK  = new ImageView(new Image(MacosComboBoxCell.class.getResourceAsStream("macos-checkmark-white.png"), 10, 10, true, false));
    private static final PseudoClass                      DARK_PSEUDO_CLASS = PseudoClass.getPseudoClass("dark");
    private              BooleanProperty                  dark;
    private              ObjectProperty<MacosAccentColor> accentColor;


    // ******************** Constructors **************************************
    public MacosComboBoxCell(final MacosComboBox<T> comboBox) {
        getStyleClass().add("macos-combo-box-cell");
        this.dark        = new BooleanPropertyBase(Helper.isDarkMode()) {
            @Override protected void invalidated() {
                pseudoClassStateChanged(DARK_PSEUDO_CLASS, get());
            }
            @Override public Object getBean() { return MacosComboBoxCell.this; }
            @Override public String getName() { return "dark"; }
        };
        this.accentColor = new ObjectPropertyBase<>(Helper.getMacosAccentColor()) {
            @Override protected void invalidated() { setStyle(isDark() ? new StringBuilder("-accent-color-dark: ").append(get().getDarkStyleClass()).append(";").toString() : new StringBuilder("-accent-color: ").append(get().getDarkStyleClass()).append(";").toString()); }
            @Override public Object getBean() { return MacosComboBoxCell.this; }
            @Override public String getName() { return "accentColor"; }
        };
        setGraphicTextGap(5);
        setContentDisplay(ContentDisplay.LEFT);

        comboBox.darkProperty().addListener((o, ov, nv) -> this.dark.set(nv));
        this.accentColor.bind(comboBox.accentColorProperty());
        pseudoClassStateChanged(DARK_PSEUDO_CLASS, comboBox.isDark());
    }


    // ******************** Methods *******************************************
    @Override public final boolean isDark() {
        return dark.get();
    }
    @Override public final void setDark(final boolean dark) { this.dark.set(dark); }
    @Override public final BooleanProperty darkProperty() { return dark; }

    public MacosAccentColor getAccentColor() { return accentColor.get(); }
    public void setAccentColor(final MacosAccentColor accentColor) { this.accentColor.set(accentColor); }
    public ObjectProperty<MacosAccentColor> accentColorProperty() { return accentColor; }


    @Override protected void updateItem(final T item, final boolean empty) {
        super.updateItem(item, empty);

        if (empty || null == item) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.toString());
            setGraphic(isSelected() ? (isDark() ? WHITE_CHECK_MARK : BLACK_CHECK_MARK) : EMPTY_CHECK_MARK);
        }
    }

    @Override public String toString() {
        return getItem().toString();
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() { return MacosComboBoxCell.class.getResource("apple.css").toExternalForm(); }
}
