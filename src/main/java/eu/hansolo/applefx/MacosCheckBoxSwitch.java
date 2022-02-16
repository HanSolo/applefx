package eu.hansolo.applefx;

import eu.hansolo.applefx.tools.Helper;
import eu.hansolo.applefx.tools.MacosAccentColor;
import eu.hansolo.applefx.tools.MacosSystemColor;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.css.PseudoClass;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;


public class MacosCheckBoxSwitch extends CheckBox implements MacosControl {
    private static final PseudoClass                                   DARK_PSEUDO_CLASS              = PseudoClass.getPseudoClass("dark");
    private static final PseudoClass                                   WINDOW_FOCUS_LOST_PSEUDO_CLASS = PseudoClass.getPseudoClass("window-focus-lost");
    private static final StyleablePropertyFactory<MacosCheckBoxSwitch> FACTORY                        = new StyleablePropertyFactory<>(Region.getClassCssMetaData());
    private              BooleanBinding                                showing;
    private              StyleableProperty<Color>                      bkgColor;
    private              BooleanProperty                               dark;
    private              BooleanProperty                               windowFocusLost;
    private              ObjectProperty<MacosAccentColor>              accentColor;
    private              BooleanProperty                               showDescriptions;


    // ******************** Constructors **************************************
    public MacosCheckBoxSwitch() {
        this(Helper.getMacosAccentColor(), Helper.isDarkMode());
    }
    public MacosCheckBoxSwitch(final String text) {
        this(Helper.getMacosAccentColor(), Helper.isDarkMode(), text);
    }
    public MacosCheckBoxSwitch(final MacosAccentColor accentColor) {
        this(accentColor, Helper.isDarkMode());
    }
    public MacosCheckBoxSwitch(final boolean darkMode) {
        this(Helper.getMacosAccentColor(), darkMode);
    }
    public MacosCheckBoxSwitch(final MacosAccentColor accentColor, final boolean darkMode) {
       this(accentColor, darkMode, "");
    }
    public MacosCheckBoxSwitch(final MacosAccentColor accentColor, final boolean darkMode, final String text) {
        super();
        init(accentColor, darkMode);
    }


    // ******************** Initialization ************************************
    private void init(final MacosAccentColor accentColor, final boolean darkMode) {
        getStyleClass().setAll("macos-checkbox-switch");
        this.dark             = new BooleanPropertyBase(darkMode) {
            @Override protected void invalidated() {
                pseudoClassStateChanged(DARK_PSEUDO_CLASS, get());
                if (isSelected()) {
                    if (getWindowFocusLost()) {
                        setBkgColor(get() ? Color.rgb(106, 105, 104) : Color.rgb(179, 179, 179));
                    } else {
                        setStyle(get() ? new StringBuilder("-accent-color-dark: ").append(accentColor.getDarkStyleClass()).append(";").toString() : new StringBuilder("-accent-color: ").append(accentColor.getAquaStyleClass()).append(";").toString());
                    }
                } else {
                    setBkgColor(get() ? MacosSystemColor.CTRL_BACKGROUND.dark() : MacosSystemColor.CTRL_BACKGROUND.aqua());
                }
            }
            @Override public Object getBean() { return MacosCheckBoxSwitch.this; }
            @Override public String getName() { return "dark"; }
        };
        this.windowFocusLost  = new BooleanPropertyBase(false) {
            @Override protected void invalidated() {
                pseudoClassStateChanged(WINDOW_FOCUS_LOST_PSEUDO_CLASS, get());
                if (isSelected()) {
                    if (isDark()) {
                        setBkgColor(get() ? Color.rgb(106, 105, 104) : getAccentColor().getColorDark());
                    } else {
                        setBkgColor(get() ? Color.rgb(179, 179, 179) : getAccentColor().getColorAqua());
                    }
                }
            }
            @Override public Object getBean() { return MacosCheckBoxSwitch.this; }
            @Override public String getName() { return "windowFocusLost"; }
        };
        this.accentColor      = new ObjectPropertyBase<>(accentColor) {
            @Override protected void invalidated() { setStyle(isDark() ? new StringBuilder("-accent-color: ").append(get().getDarkStyleClass()).append(";").toString() : new StringBuilder("-accent-color: ").append(get().getAquaStyleClass()).append(";").toString()); }
            @Override public Object getBean() { return MacosCheckBoxSwitch.this; }
            @Override public String getName() { return "accentColor"; }
        };
        this.bkgColor         = FACTORY.createStyleableColorProperty(MacosCheckBoxSwitch.this, "bkgColor", "-bkg-color", s -> s.bkgColor, Color.rgb(215, 213, 213));
        this.showDescriptions = new BooleanPropertyBase(false) {
            @Override public Object getBean() { return MacosCheckBoxSwitch.this; }
            @Override public String getName() { return "showDescriptions"; }
        };

        pseudoClassStateChanged(DARK_PSEUDO_CLASS, darkMode);
        setStyle(isDark() ? new StringBuilder("-accent-color-dark: ").append(accentColor.getDarkStyleClass()).append(";").toString() : new StringBuilder("-accent-color: ").append(accentColor.getAquaStyleClass()).append(";").toString());

        registerListeners();
    }

    private void registerListeners() {
        if (null != getScene()) {
            setupBinding();
        } else {
            sceneProperty().addListener((o1, ov1, nv1) -> {
                if (null == nv1) { return; }
                if (null != getScene().getWindow()) {
                    setupBinding();
                } else {
                    sceneProperty().get().windowProperty().addListener((o2, ov2, nv2) -> {
                        if (null == nv2) { return; }
                        setupBinding();
                    });
                }
            });
        }
    }

    private void setupBinding() {
        showing = Bindings.createBooleanBinding(() -> {
            if (getScene() != null && getScene().getWindow() != null) {
                return getScene().getWindow().isShowing();
            } else {
                return false;
            }
        }, sceneProperty(), getScene().windowProperty(), getScene().getWindow().showingProperty());
        showing.addListener(o -> {
            if (showing.get()) {
                if (isSelected()) { setBkgColor(isDark() ? getAccentColor().getColorDark() : getAccentColor().getColorAqua()); }
            }
        });
    }


    // ******************** Methods *******************************************
    @Override public final boolean isDark() {
        return dark.get();
    }
    @Override public final void setDark(final boolean dark) { this.dark.set(dark); }
    @Override public final BooleanProperty darkProperty() { return dark; }

    public boolean getWindowFocusLost() { return windowFocusLost.get(); }
    public void setWindowFocusLost(final boolean windowFocusLost) { this.windowFocusLost.set(windowFocusLost); }
    public BooleanProperty windowFocusLostProperty() { return windowFocusLost; }

    public MacosAccentColor getAccentColor() { return accentColor.get(); }
    public void setAccentColor(final MacosAccentColor accentColor) { this.accentColor.set(accentColor); }
    public ObjectProperty<MacosAccentColor> accentColorProperty() { return accentColor; }

    public boolean getShowDescriptions() { return showDescriptions.get(); }
    public void setShowDescriptions(final boolean showDescriptions) { this.showDescriptions.set(showDescriptions); }
    public BooleanProperty showDescriptionsProperty() { return showDescriptions; }

    protected Color getBkgColor() { return this.bkgColor.getValue(); }
    protected void setBkgColor(final Color bkgColor) { this.bkgColor.setValue(bkgColor); }
    protected StyleableObjectProperty<Color> bkgColorProperty() { return (StyleableObjectProperty<Color>) bkgColor; }

    @Override protected Skin<?> createDefaultSkin() {
        return new MacosCheckBoxSwitchSkin(this);
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() { return MacosRadioButton.class.getResource("apple.css").toExternalForm(); }
}
