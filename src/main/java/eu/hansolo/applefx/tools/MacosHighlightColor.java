package eu.hansolo.applefx.tools;

import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;


public enum MacosHighlightColor {
    BLUE(Color.rgb(129, 172, 240), Color.rgb(29, 104, 151)),
    PURPLE(Color.rgb(186, 145, 186), Color.rgb(134, 76, 133)),
    PINK(Color.rgb(237, 152, 189), Color.rgb(153, 75, 126)),
    RED(Color.rgb(225, 142, 145), Color.rgb(153, 77, 80)),
    ORANGE(Color.rgb(236, 176, 128), Color.rgb(153, 108, 42)),
    YELLOW(Color.rgb(240, 210, 135), Color.rgb(153, 149, 27)),
    GREEN(Color.rgb(162, 204, 149), Color.rgb(90, 145, 69)),
    GRAPHITE(Color.rgb(153, 153, 157), Color.rgb(120, 118, 118));

    final Color aqua;
    final Color dark;


    MacosHighlightColor(final Color aqua, final Color dark) {
        this.aqua = aqua;
        this.dark = dark;
    }

    public Color aqua() { return aqua; }
    public Color dark() { return dark; }

    public String aquaStyleClass() { return "-" + name() + "-AQUA-HIGHLIGHT"; }
    public String darkStyleClass() { return "-" + name() + "-DARK-HIGHLIGHT"; }


    public boolean isGivenColor(final Color color) {
        return (aqua.equals(color) || dark.equals(color));
    }

    public static final List<MacosHighlightColor> getAsList() { return Arrays.asList(values()); }
}

