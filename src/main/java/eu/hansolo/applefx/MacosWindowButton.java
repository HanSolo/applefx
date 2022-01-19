package eu.hansolo.applefx;

import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

import java.util.function.Consumer;


@DefaultProperty("children")
public class MacosWindowButton extends Region implements MacosControl {
    private static final double                          MINIMUM_WIDTH         = MacosButtonSize.SMALL.px;
    private static final double                          MINIMUM_HEIGHT        = MacosButtonSize.SMALL.px;
    private static final double                          MAXIMUM_WIDTH         = MacosButtonSize.NORMAL.px;
    private static final double                          MAXIMUM_HEIGHT        = MacosButtonSize.NORMAL.px;
    private static final PseudoClass                     CLOSE_PSEUDO_CLASS    = PseudoClass.getPseudoClass("close");
    private static final PseudoClass                     MINIMIZE_PSEUDO_CLASS = PseudoClass.getPseudoClass("minimize");
    private static final PseudoClass                     MAXIMIZE_PSEUDO_CLASS = PseudoClass.getPseudoClass("maximize");
    private static final PseudoClass                     HOVERED_PSEUDO_CLASS  = PseudoClass.getPseudoClass("hovered");
    private static final PseudoClass                     PRESSED_PSEUDO_CLASS  = PseudoClass.getPseudoClass("pressed");
    private static final PseudoClass                     DARK_PSEUDO_CLASS     = PseudoClass.getPseudoClass("dark");
    private              MacosButtonSize                 iconSize;
    private              BooleanProperty                 dark;
    private              BooleanProperty                 hovered;
    private static       String                          userAgentStyleSheet;
    private              ObjectProperty<MacosButtonType> type;
    private              double                          size;
    private              double                          width;
    private              double                          height;
    private              Circle                          circle;
    private              Region                          symbol;
    private              Consumer<MouseEvent>            mousePressedConsumer;
    private              Consumer<MouseEvent>            mouseReleasedConsumer;


    // ******************** Constructors **************************************
    public MacosWindowButton() {
        this(MacosButtonType.CLOSE);
    }
    public MacosWindowButton(final MacosButtonType type) {
        this(type, MacosButtonSize.NORMAL);
    }
    public MacosWindowButton(final MacosButtonType type, final MacosButtonSize size) {
        this.type     = new ObjectPropertyBase<>(type) {
            @Override protected void invalidated() {
                switch(get()) {
                    case CLOSE    -> {
                        pseudoClassStateChanged(CLOSE_PSEUDO_CLASS, true);
                        pseudoClassStateChanged(MINIMIZE_PSEUDO_CLASS, false);
                        pseudoClassStateChanged(MAXIMIZE_PSEUDO_CLASS, false);
                    }
                    case MINIMIZE -> {
                        pseudoClassStateChanged(CLOSE_PSEUDO_CLASS, false);
                        pseudoClassStateChanged(MINIMIZE_PSEUDO_CLASS, true);
                        pseudoClassStateChanged(MAXIMIZE_PSEUDO_CLASS, false);
                    }
                    case MAXIMIZE -> {
                        pseudoClassStateChanged(CLOSE_PSEUDO_CLASS, false);
                        pseudoClassStateChanged(MINIMIZE_PSEUDO_CLASS, false);
                        pseudoClassStateChanged(MAXIMIZE_PSEUDO_CLASS, true);
                    }
                }
            }
            @Override public Object getBean() { return MacosWindowButton.this; }
            @Override public String getName() { return "type"; }
        };
        this.dark = new BooleanPropertyBase(false) {
            @Override protected void invalidated() { pseudoClassStateChanged(DARK_PSEUDO_CLASS, get()); }
            @Override public Object getBean() { return MacosWindowButton.this; }
            @Override public String getName() { return "darkMode"; }
        };
        this.hovered  = new BooleanPropertyBase() {
            @Override protected void invalidated() { pseudoClassStateChanged(HOVERED_PSEUDO_CLASS, get()); }
            @Override public Object getBean() { return MacosWindowButton.this; }
            @Override public String getName() { return "hovered"; }
        };
        this.iconSize = size;

        pseudoClassStateChanged(CLOSE_PSEUDO_CLASS, MacosButtonType.CLOSE == type);
        pseudoClassStateChanged(MINIMIZE_PSEUDO_CLASS, MacosButtonType.MINIMIZE == type);
        pseudoClassStateChanged(MAXIMIZE_PSEUDO_CLASS, MacosButtonType.MAXIMIZE == type);

        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void initGraphics() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 || Double.compare(getWidth(), 0.0) <= 0 ||
            Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setMinSize(iconSize.px, iconSize.px);
                setMaxSize(iconSize.px, iconSize.px);
                setPrefSize(iconSize.px, iconSize.px);
            }
        }

        getStyleClass().add("macos-window-button");

        circle = new Circle();
        circle.getStyleClass().add("circle");
        circle.setStrokeType(StrokeType.INSIDE);

        symbol = new Region();
        symbol.getStyleClass().add(MacosButtonSize.NORMAL == iconSize ? "symbol" : "symbol-small");

        getChildren().setAll(circle, symbol);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            pseudoClassStateChanged(PRESSED_PSEUDO_CLASS, true);
            if (null == mousePressedConsumer) { return; }
            mousePressedConsumer.accept(e);
        });
        addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            pseudoClassStateChanged(PRESSED_PSEUDO_CLASS, false);
            if (null == mouseReleasedConsumer) { return; }
            mouseReleasedConsumer.accept(e);
        });
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double height) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double width) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double height) { return super.computePrefWidth(height); }
    @Override protected double computePrefHeight(final double width) { return super.computePrefHeight(width); }
    @Override protected double computeMaxWidth(final double height) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double width) { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public MacosButtonType getType()                      { return type.get(); }
    public void setType(final MacosButtonType type)       { this.type.set(type); }
    public ObjectProperty<MacosButtonType> typeProperty() { return type; }

    @Override public boolean isDark() { return dark.get(); }
    @Override public void setDark(final boolean dark) { this.dark.set(dark); }
    @Override public BooleanProperty darkProperty() { return dark; }

    public boolean isHovered() { return hovered.get(); }
    public void setHovered(final boolean hovered) { this.hovered.set(hovered); }
    public BooleanProperty hoveredProperty() { return hovered; }

    public void setOnMousePressed(final Consumer<MouseEvent> mousePressedConsumer)   { this.mousePressedConsumer  = mousePressedConsumer; }
    public void setOnMouseReleased(final Consumer<MouseEvent> mouseReleasedConsumer) { this.mouseReleasedConsumer = mouseReleasedConsumer; }


    // ******************** Layout ********************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (width > 0 && height > 0) {
            setMinSize(size, size);
            setMaxSize(size, size);
            setPrefSize(size, size);

            double center = size * 0.5;
            circle.setRadius(center);
            circle.setCenterX(center);
            circle.setCenterY(center);

            symbol.setPrefSize(size, size);
        }
    }

    @Override public String getUserAgentStylesheet() {
        if (null == userAgentStyleSheet) { userAgentStyleSheet = MacosWindowButton.class.getResource("apple.css").toExternalForm(); }
        return userAgentStyleSheet;
    }
}
