package eu.hansolo.applefx.tools;

import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;


public enum MacosSystemColor {
    BLUE(Color.rgb(3, 122, 255), Color.rgb(3, 122, 255)),
    PURPLE(Color.rgb(149, 61, 150), Color.rgb(165, 80, 167)),
    PINK(Color.rgb(247, 78, 158), Color.rgb(247, 78, 158)),
    RED(Color.rgb(247, 56, 62), Color.rgb(255, 82, 87)),
    ORANGE(Color.rgb(247, 130, 27), Color.rgb(247, 130, 24)),
    YELLOW(Color.rgb(255, 199, 38), Color.rgb(255, 198, 3)),
    GREEN(Color.rgb(98, 186, 70), Color.rgb(98, 186, 70)),
    GRAPHITE(Color.rgb(152, 152, 152), Color.rgb(140, 140, 140)),

    BACKGROUND(Color.rgb(236, 234, 234), Color.rgb(31, 28, 29)),
    CTRL_BACKGROUND(Color.rgb(211, 210, 211), Color.rgb(61, 59, 61));

    final Color aqua;
    final Color dark;


    MacosSystemColor(final Color aqua, final Color dark) {
        this.aqua = aqua;
        this.dark = dark;
    }

    public Color aqua() { return aqua; }
    public Color dark() { return dark; }

    public String aquaStyleClass() { return "-" + name() + "-AQUA"; }
    public String darkStyleClass() { return "-" + name() + "-DARK"; }


    public boolean isGivenColor(final Color color) {
        return (aqua.equals(color) || dark.equals(color));
    }

    public static final List<MacosSystemColor> getAsList() { return Arrays.asList(values()); }
}
