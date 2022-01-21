package eu.hansolo.applefx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.control.Skin;
import javafx.scene.control.Slider;


public class MacosSlider extends Slider implements MacosControl {
    private static final PseudoClass     BALANCE_PSEUDO_CLASS = PseudoClass.getPseudoClass("balance");
    private static final PseudoClass     DARK_PSEUDO_CLASS    = PseudoClass.getPseudoClass("dark");
    private              BooleanProperty balance              = new BooleanPropertyBase(false) {
        @Override protected void invalidated() { pseudoClassStateChanged(BALANCE_PSEUDO_CLASS, get()); }
        @Override public Object getBean() { return MacosSlider.this; }
        @Override public String getName() { return "balance"; }
    };
    private              boolean         _dark;
    private              BooleanProperty dark;


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
        _dark = false;
    }


    // ******************** Methods *******************************************
    public boolean getBalance() { return balance.get(); }
    public void setBalance(final boolean balance) { this.balance.set(balance); }
    public BooleanProperty balanceProperty() { return balance; }

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
                @Override public Object getBean() { return MacosSlider.this; }
                @Override public String getName() { return "dark"; }
            };
        }
        return dark;
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
