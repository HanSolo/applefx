package eu.hansolo.applefx.tools;

import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;


public enum MacOSSystemColor {
    BLUE(Color.rgb(0, 122, 255), Color.rgb(10, 132, 255)), BROWN(Color.rgb(162, 132, 94), Color.rgb(172, 142, 104)), GRAY(Color.rgb(142, 142, 147), Color.rgb(152, 152, 157)), GREEN(Color.rgb(40, 205, 65), Color.rgb(50, 215, 75)),
    INIDIGO(Color.rgb(88, 86, 214), Color.rgb(94, 92, 230)), ORANGE(Color.rgb(255, 149, 0), Color.rgb(255, 159, 0)), PINK(Color.rgb(255, 45, 85), Color.rgb(255, 55, 95)), PURPLE(Color.rgb(175, 82, 222), Color.rgb(191, 90, 242)),
    RED(Color.rgb(255, 59, 48), Color.rgb(255, 69, 58)), TEAL(Color.rgb(85, 190, 240), Color.rgb(90, 200, 245)), YELLOW(Color.rgb(255, 204, 0), Color.rgb(255, 214, 10)),

    BACKGROUND(Color.rgb(236, 234, 234), Color.rgb(31, 28, 29)), CTR_BACKGROUND(Color.rgb(211, 210, 211), Color.rgb(61, 59, 61));

    final Color colorAqua;
    final Color colorDark;


    MacOSSystemColor(final Color colorAqua, final Color colorDark) {
        this.colorAqua = colorAqua;
        this.colorDark = colorDark;
    }

    public Color getColorAqua()       { return colorAqua; }
    public Color getColorDark()       { return colorDark; }

    public String getAquaStyleClass() { return name() + "-AQUA"; }
    public String getDarkStyleClass() { return name() + "-DARK"; }

    public boolean isGivenColor(final Color color) {
        return (colorAqua.equals(color) || colorDark.equals(color));
    }

    public static final List<MacOSSystemColor> getAsList() { return Arrays.asList(values()); }
}
