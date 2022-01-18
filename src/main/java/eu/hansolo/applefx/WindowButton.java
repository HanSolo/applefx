package eu.hansolo.applefx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.input.MouseEvent;

import java.util.function.Consumer;


public interface WindowButton {
    WindowButtonType getType();
    void setType(final WindowButtonType type);
    ObjectProperty<WindowButtonType> typeProperty();

    boolean isDarkMode();
    void setDarkMode(final boolean darkMode);
    BooleanProperty darkModeProperty();

    boolean isHovered();
    void setHovered(final boolean hovered);
    BooleanProperty hoveredProperty();

    void setOnMousePressed(final Consumer<MouseEvent> mousePressedConsumer);
    void setOnMouseReleased(final Consumer<MouseEvent> mouseReleasedConsumer);
}
