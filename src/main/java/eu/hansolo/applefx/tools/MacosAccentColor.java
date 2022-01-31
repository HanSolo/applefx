package eu.hansolo.applefx.tools;

import eu.hansolo.toolboxfx.HelperFX;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;


public enum MacosAccentColor {
    MULTI_COLOR(null, MacosSystemColor.BLUE.aqua, Color.web("#b3d7ff"), HelperFX.getColorWithOpacity(MacosSystemColor.BLUE.aqua, 0.25), MacosSystemColor.BLUE.dark, Color.web("#3f638b"), HelperFX.getColorWithOpacity(MacosSystemColor.BLUE.dark, 0.25)),
    BLUE(4, MacosSystemColor.BLUE.aqua, Color.web("#b3d7ff"), HelperFX.getColorWithOpacity(MacosSystemColor.BLUE.aqua, 0.25), MacosSystemColor.BLUE.dark, Color.web("#3f638b"), HelperFX.getColorWithOpacity(MacosSystemColor.BLUE.dark, 0.25)),
    PURPLE(5, MacosSystemColor.PURPLE.aqua, Color.web("#dfc5df"), HelperFX.getColorWithOpacity(MacosSystemColor.PURPLE.aqua, 0.25), MacosSystemColor.PURPLE.dark, Color.web("#6f566f"), HelperFX.getColorWithOpacity(MacosSystemColor.PURPLE.dark, 0.25)),
    PINK(6, MacosSystemColor.PINK.aqua, Color.web("#fccae2"), HelperFX.getColorWithOpacity(MacosSystemColor.PINK.aqua, 0.25), MacosSystemColor.PINK.dark, Color.web("#87566d"), HelperFX.getColorWithOpacity(MacosSystemColor.PINK.dark, 0.25)),
    RED(0, MacosSystemColor.RED.aqua, Color.web("#f5c3c5"), HelperFX.getColorWithOpacity(MacosSystemColor.RED.aqua, 0.25), MacosSystemColor.RED.dark, Color.web("#8b5758"), HelperFX.getColorWithOpacity(MacosSystemColor.RED.dark, 0.25)),
    ORANGE(1, MacosSystemColor.ORANGE.aqua, Color.web("#fcd9bb"), HelperFX.getColorWithOpacity(MacosSystemColor.ORANGE.aqua, 0.25), MacosSystemColor.ORANGE.dark, Color.web("#886547"), HelperFX.getColorWithOpacity(MacosSystemColor.ORANGE.dark, 0.25)),
    YELLOW(2, MacosSystemColor.YELLOW.aqua, Color.web("#feeebe"), HelperFX.getColorWithOpacity(MacosSystemColor.YELLOW.aqua, 0.25), MacosSystemColor.YELLOW.dark, Color.web("#8b7a40"), HelperFX.getColorWithOpacity(MacosSystemColor.YELLOW.dark, 0.25)),
    GREEN(3, MacosSystemColor.GREEN.aqua, Color.web("#d0eac7"), HelperFX.getColorWithOpacity(MacosSystemColor.GREEN.aqua, 0.25), MacosSystemColor.GREEN.dark, Color.web("#5c7653"), HelperFX.getColorWithOpacity(MacosSystemColor.GREEN.dark, 0.25)),
    GRAPHITE(-1, MacosSystemColor.GRAPHITE.aqua, Color.web("#e0e0e0"), HelperFX.getColorWithOpacity(MacosSystemColor.GRAPHITE.aqua, 0.25), MacosSystemColor.GRAPHITE.dark, Color.web("#696665"), HelperFX.getColorWithOpacity(MacosSystemColor.GRAPHITE.dark, 0.25));

    final Integer key;
    final Color   colorAqua;
    final Color   colorAquaHighlight;
    final Color   colorAquaFocus;
    final Color   colorDark;
    final Color   colorDarkHighlight;
    final Color   colorDarkFocus;


    MacosAccentColor(final Integer key, final Color colorAqua, final Color colorAquaHighlight, final Color colorAquaFocus, final Color colorDark, final Color colorDarkHighlight, final Color colorDarkFocus) {
        this.key                = key;
        this.colorAqua          = colorAqua;
        this.colorAquaHighlight = colorAquaHighlight;
        this.colorAquaFocus     = colorAquaFocus;
        this.colorDark          = colorDark;
        this.colorDarkHighlight = colorDarkHighlight;
        this.colorDarkFocus     = colorDarkFocus;
    }

    public Integer getKey() { return key; }

    public Color getColorAqua() { return colorAqua; }

    public Color getColorAquaHighlight() { return colorAquaHighlight; }

    public Color getColorAquaFocus() { return colorAquaFocus; }

    public Color getColorDark() { return colorDark; }

    public Color getColorDarkHighlight() { return colorDarkHighlight; }

    public Color getColorDarkFocus() { return colorDarkFocus; }

    public String getAquaStyleClass() {
        switch(this) {
            case MULTI_COLOR -> { return "-BLUE-AQUA"; }
            default          -> { return "-" + name() + "-AQUA"; }
        }
    }
    public String getDarkStyleClass() {
        switch(this) {
            case MULTI_COLOR -> { return "-BLUE-DARK"; }
            default          -> { return "-" + name() + "-DARK"; }
        }
    }

    public String getAquaHighlightStyleClass() {
        switch(this) {
            case MULTI_COLOR -> { return "-BLUE-AQUA-HIGHLIGHT"; }
            default          -> { return "-" + name() + "-AQUA-HIGHLIGHT"; }
        }
    }
    public String getDarkHighlightStyleClass() {
        switch(this) {
            case MULTI_COLOR -> { return "-BLUE-DARK-HIGHLIGHT"; }
            default          -> { return "-" + name() + "-DARK-HIGHLIGHT"; }
        }
    }

    public boolean isGivenColor(final Color color) {
        return (colorAqua.equals(color) || colorDark.equals(color));
    }

    public static final List<MacosAccentColor> getAsList() { return Arrays.asList(values()); }

    public static MacosAccentColor fromColor(final Color color) {
        for (MacosAccentColor accentColor : values()) {
            if (accentColor.getColorAqua().equals(color) || accentColor.getColorDark().equals(color)) {
                return accentColor;
            }
        }
        return MacosAccentColor.BLUE;
    }
}
