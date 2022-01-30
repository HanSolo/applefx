package eu.hansolo.applefx;

import eu.hansolo.applefx.tools.MacosAccentColor;
import javafx.beans.property.ObjectProperty;


public interface MacosControlWithAccentColor extends MacosControl {
    MacosAccentColor getAccentColor();
    void setAccentColor(MacosAccentColor accentColor);
    ObjectProperty<MacosAccentColor> accentColorProperty();
}
