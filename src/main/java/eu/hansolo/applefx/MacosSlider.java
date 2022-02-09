package eu.hansolo.applefx;

import eu.hansolo.applefx.tools.Helper;
import eu.hansolo.applefx.tools.MacosAccentColor;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.control.Skin;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;


public class MacosSlider extends Slider implements MacosControlWithAccentColor {
    private static final PseudoClass                      BALANCE_PSEUDO_CLASS = PseudoClass.getPseudoClass("balance");
    private static final PseudoClass                      DARK_PSEUDO_CLASS    = PseudoClass.getPseudoClass("dark");
    private              BooleanProperty                  balance              = new BooleanPropertyBase(false) {
        @Override protected void invalidated() { pseudoClassStateChanged(BALANCE_PSEUDO_CLASS, get()); }
        @Override public Object getBean() { return MacosSlider.this; }
        @Override public String getName() { return "balance"; }
    };
    private              BooleanProperty                  dark;
    private              ObjectProperty<MacosAccentColor> accentColor;


    // ******************** Constructors **************************************
    public MacosSlider() {
        super();
        init();
    }
    public MacosSlider(final double min, final double max, final double value) {
        super(min, max, value);
        init();
    }


    // ******************** Initialization ************************************
    private void init() {
        getStyleClass().add("macos-slider");
        this.dark        = new BooleanPropertyBase(Helper.isDarkMode()) {
            @Override protected void invalidated() { pseudoClassStateChanged(DARK_PSEUDO_CLASS, get()); }
            @Override public Object getBean() { return MacosSlider.this; }
            @Override public String getName() { return "dark"; }
        };
        this.accentColor = new ObjectPropertyBase<>(Helper.getMacosAccentColor()) {
            @Override protected void invalidated() { setStyle(new StringBuilder().append("-track-progress-fill: ").append((isDark() ? get().getDarkStyleClass() : get().getAquaStyleClass())).append(";").toString()); }
            @Override public Object getBean() { return MacosSlider.this; }
            @Override public String getName() { return "accentColor"; }
        };
        pseudoClassStateChanged(DARK_PSEUDO_CLASS, isDark());
    }


    // ******************** Methods *******************************************
    public boolean getBalance() { return balance.get(); }
    public void setBalance(final boolean balance) { this.balance.set(balance); }
    public BooleanProperty balanceProperty() { return balance; }

    @Override public final boolean isDark() { return dark.get(); }
    @Override public final void setDark(final boolean dark) { this.dark.set(dark); }
    @Override public final BooleanProperty darkProperty() { return dark; }

    @Override public MacosAccentColor getAccentColor() { return accentColor.get(); }
    @Override public void setAccentColor(final MacosAccentColor accentColor) { this.accentColor.set(accentColor); }
    @Override public ObjectProperty<MacosAccentColor> accentColorProperty() { return accentColor; }

    public void setWindowFocusLost(final boolean windowFocusLost) {
        if (windowFocusLost) {
            setStyle(new StringBuilder().append(isDark() ? "-track-progress-fill: #6A6968;" : "-track-progress-fill: #D8D8D8;").toString());
        } else {
            setStyle(new StringBuilder().append("-track-progress-fill: ").append((isDark() ? getAccentColor().getDarkStyleClass() : getAccentColor().getAquaStyleClass())).append(";").toString());
        }
    }

    public double getRange() { return (getMax() - getMin()); }

    public double getBalanceValue() { return getValue() - (getRange() * 0.5); }


    @Override protected Skin<?> createDefaultSkin() {
        return new MacosSliderSkin(this);
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() {
        return MacosSlider.class.getResource("apple.css").toExternalForm();
    }
}
