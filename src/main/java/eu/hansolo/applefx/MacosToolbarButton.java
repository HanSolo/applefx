package eu.hansolo.applefx;

import eu.hansolo.applefx.tools.Helper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;


public class MacosToolbarButton extends Button implements MacosControl {
    private static final PseudoClass     DARK_PSEUDO_CLASS    = PseudoClass.getPseudoClass("dark");
    private              boolean         _dark;
    private              BooleanProperty dark;
    private              String          svgPath;
    private              Region          icon;


    // ******************** Constructors **************************************
    public MacosToolbarButton() {
        this("");
    }
    public MacosToolbarButton(final String svgPath) {
        super(null);
        this.svgPath = svgPath;
        init();
    }


    // ******************** Initialization ************************************
    private void init() {
        getStyleClass().add("macos-toolbar-button");
        icon = new Region();
        icon.getStyleClass().addAll("macos-toolbar-button", "icon");
        if (null != svgPath && !svgPath.isEmpty()) {
            icon.setStyle("-shape: \"" + svgPath + "\";" );
        }
        setGraphic(icon);
        _dark = Helper.isDarkMode();
        pseudoClassStateChanged(DARK_PSEUDO_CLASS, _dark);
        registerListeners();
    }

    private void registerListeners() {
        graphicProperty().addListener((o, ov, nv) -> {
            if (nv instanceof Region) {
                nv.getStyleClass().addAll("macos-toolbar-button", "icon");
            } else {
                setGraphic(null);
            }
        });
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
                @Override public Object getBean() { return MacosToolbarButton.this; }
                @Override public String getName() { return "dark"; }
            };
        }
        return dark;
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() { return MacosButton.class.getResource("apple.css").toExternalForm(); }
}
