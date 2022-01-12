package eu.hansolo.applefx.tools;

import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;


public enum MacOSAccentColor {
    MULTI_COLOR(null, MacOSSystemColor.BLUE.colorAqua, Color.web("#b3d7ff"), Color.web("#7daaf0"), MacOSSystemColor.BLUE.colorDark, Color.web("#3f638b"), Color.web("#296e99")),
    GRAPHITE(-1, MacOSSystemColor.GRAY.colorAqua, Color.web("#e0e0e0"), Color.web("#c4c2c4"), MacOSSystemColor.GRAY.colorDark, Color.web("#696665"), Color.web("#7d7b7a")),
    RED(0, MacOSSystemColor.RED.colorAqua, Color.web("#f5c3c5"), Color.web("#df878b"), MacOSSystemColor.RED.colorDark, Color.web("#8b5758"), Color.web("#99585a")),
    ORANGE(1, MacOSSystemColor.ORANGE.colorAqua, Color.web("#fcd9bb"), Color.web("#ecae7d"), MacOSSystemColor.ORANGE.colorDark, Color.web("#886547"), Color.web("#9a7336")),
    YELLOW(2, MacOSSystemColor.YELLOW.colorAqua, Color.web("#feeebe"), Color.web("#f1d283"), MacOSSystemColor.YELLOW.colorDark, Color.web("#8b7a40"), Color.web("#9b982b")),
    GREEN(3, MacOSSystemColor.GREEN.colorAqua, Color.web("#d0eac7"), Color.web("#9dcb8f"), MacOSSystemColor.GREEN.colorDark, Color.web("#5c7653"), Color.web("#629450")),
    BLUE(4, MacOSSystemColor.BLUE.colorAqua, Color.web("#b3d7ff"), Color.web("#7daaf0"), MacOSSystemColor.BLUE.colorDark, Color.web("#3f638b"), Color.web("#296e99")),
    PURPLE(5, MacOSSystemColor.PURPLE.colorAqua, Color.web("#dfc5df"), Color.web("#b98ab8"), MacOSSystemColor.PURPLE.colorDark, Color.web("#6f566f"), Color.web("#895687")),
    PINK(6, MacOSSystemColor.PINK.colorAqua, Color.web("#fccae2"), Color.web("#eb93bc"), MacOSSystemColor.PINK.colorDark, Color.web("#87566d"), Color.web("#995582"));

    final Integer key;
    final Color   colorAqua;
    final Color   colorAquaHighlight;
    final Color   colorAquaFocus;
    final Color   colorDark;
    final Color   colorDarkHighlight;
    final Color   colorDarkFocus;


    MacOSAccentColor(final Integer key, final Color colorAqua, final Color colorAquaHighlight, final Color colorAquaFocus, final Color colorDark, final Color colorDarkHighlight, final Color colorDarkFocus) {
        this.key                = key;
        this.colorAqua          = colorAqua;
        this.colorAquaHighlight = colorAquaHighlight;
        this.colorAquaFocus     = colorAquaFocus;
        this.colorDark          = colorDark;
        this.colorDarkHighlight = colorDarkHighlight;
        this.colorDarkFocus     = colorDarkFocus;
    }

    public Integer getKey()              { return key; }

    public Color getColorAqua()          { return colorAqua; }

    public Color getColorAquaHighlight() { return colorAquaHighlight; }

    public Color getColorAquaFocus()     { return colorAquaFocus; }

    public Color getColorDark()          { return colorDark; }

    public Color getColorDarkHighlight() { return colorDarkHighlight; }

    public Color getColorDarkFocus()     { return colorDarkFocus; }

    public boolean isGivenColor(final Color color) {
        return (colorAqua.equals(color) || colorDark.equals(color));
    }

    public static final List<MacOSAccentColor> getAsList() { return Arrays.asList(values()); }
}
