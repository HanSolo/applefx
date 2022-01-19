package eu.hansolo.applefx;

import javafx.beans.property.BooleanProperty;


public interface MacosControl {
    boolean isDark();
    void setDark(boolean dark);
    BooleanProperty darkProperty();
}
