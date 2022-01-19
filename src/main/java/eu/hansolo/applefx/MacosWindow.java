package eu.hansolo.applefx;

import eu.hansolo.applefx.tools.Helper;
import eu.hansolo.applefx.tools.MacosAccentColor;
import eu.hansolo.applefx.tools.ResizeHelper;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;
import java.util.function.Consumer;


public class MacosWindow extends Region implements MacosControl {
    public static final  double                           OFFSET                  = 40;
    private static final double                           HEADER_HEIGHT           = 30;
    private static final DropShadow                       HEADER_SHADOW           = new DropShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.1), 1, 0.0, 0, 1);
    private static final DropShadow                       STAGE_SHADOW_FOCUSED    = new DropShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.5), 45.0, 0.0, 0.0, 15);
    private static final DropShadow                       STAGE_SHADOW            = new DropShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.35), 20.0, 0.0, 0.0, 10);
    private static final PseudoClass                      DARK_PSEUDO_CLASS       = PseudoClass.getPseudoClass("dark");
    private static final PseudoClass                      FOCUS_LOST_PSEUDO_CLASS = PseudoClass.getPseudoClass("focus-lost");
    private              BooleanProperty                  dark;
    private              ObjectProperty<MacosAccentColor> accentColor;
    private              Stage                            stage;
    private              MacosWindowButton                closeButton;
    private              MacosWindowButton                minimizeButton;
    private              MacosWindowButton                maximizeButton;
    private              HBox                             buttonBox;
    private              HBox                             headerBox;
    private              AnchorPane                       headerPane;
    private              MacosLabel                       headerText;
    private              AnchorPane                       contentPane;
    private              BorderPane                       mainPane;
    private              Parent                           content;


    // ******************** Constructors **************************************
    public MacosWindow(final Stage stage, final Parent content) {
        this(stage, content, false, MacosAccentColor.MULTI_COLOR);
    }
    public MacosWindow(final Stage stage, final Parent content, final boolean darkMode) {
        this(stage, content, darkMode, MacosAccentColor.MULTI_COLOR);
    }
    public MacosWindow(final Stage stage, final Parent content, final MacosAccentColor accentColor) {
        this(stage, content, false, accentColor);
    }
    public MacosWindow(final Stage stage, final Parent content, final boolean darkMode, final MacosAccentColor accentColor) {
        if (null == stage) { throw new IllegalArgumentException("stage cannot be null"); }
        if (null == content) { throw new IllegalArgumentException("content cannot be null"); }
        this.stage       = stage;
        this.content     = content;
        this.dark        = new BooleanPropertyBase(darkMode) {
            @Override protected void invalidated() { pseudoClassStateChanged(DARK_PSEUDO_CLASS, get()); }
            @Override public Object getBean() { return MacosWindow.this; }
            @Override public String getName() { return "dark"; }
        };
        this.accentColor = new ObjectPropertyBase<>(accentColor) {
                @Override protected void invalidated() { setAllAccentColors(get()); }
                @Override public Object getBean() { return MacosWindow.this; }
                @Override public String getName() { return "accentColor"; }
            };

        init();
        registerListeners();

        enableDarkMode(isDark());
        setAllAccentColors(accentColor);
    }


    // ******************** Initialization ************************************
    private void init() {
        stage.initStyle(StageStyle.TRANSPARENT);

        closeButton    = new MacosWindowButton(MacosButtonType.CLOSE, MacosButtonSize.NORMAL);
        minimizeButton = new MacosWindowButton(MacosButtonType.MINIMIZE, MacosButtonSize.NORMAL);
        maximizeButton = new MacosWindowButton(MacosButtonType.MAXIMIZE, MacosButtonSize.NORMAL);

        buttonBox = new HBox(8, closeButton, minimizeButton, maximizeButton);

        headerText = new MacosLabel(stage.getTitle());
        headerText.setMaxWidth(Double.MAX_VALUE);
        headerText.setPrefWidth(MacosLabel.USE_COMPUTED_SIZE);
        headerText.getStyleClass().add("macos-header-text");
        headerText.setAlignment(Pos.CENTER);
        headerText.setMouseTransparent(true);

        Region headerSpacerRight = new Region();
        headerSpacerRight.setPrefWidth(60);

        HBox.setHgrow(closeButton, Priority.NEVER);
        HBox.setHgrow(minimizeButton, Priority.NEVER);
        HBox.setHgrow(maximizeButton, Priority.NEVER);
        HBox.setHgrow(headerText, Priority.ALWAYS);
        HBox.setHgrow(headerSpacerRight, Priority.NEVER);

        headerBox = new HBox(buttonBox, headerText, headerSpacerRight);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        AnchorPane.setTopAnchor(headerBox, 11d);
        AnchorPane.setRightAnchor(headerBox, 11d);
        AnchorPane.setBottomAnchor(headerBox, 11d);
        AnchorPane.setLeftAnchor(headerBox, 11d);

        headerPane = new AnchorPane();
        headerPane.getStyleClass().add("macos-header");
        headerPane.setEffect(HEADER_SHADOW);
        headerPane.setMinHeight(HEADER_HEIGHT);
        headerPane.setMaxHeight(HEADER_HEIGHT);
        headerPane.setPrefHeight(HEADER_HEIGHT);
        headerPane.getChildren().setAll(headerBox);

        AnchorPane.setTopAnchor(content, 1d);
        AnchorPane.setRightAnchor(content, 1d);
        AnchorPane.setBottomAnchor(content, 1d);
        AnchorPane.setLeftAnchor(content, 1d);
        contentPane = new AnchorPane(content);
        contentPane.getStyleClass().add("macos-content-pane");

        mainPane = new BorderPane();
        mainPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
        mainPane.setTop(headerPane);
        mainPane.setCenter(contentPane);

        getChildren().add(mainPane);
        setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10, 10, 10, 10, false), Insets.EMPTY)));
        setEffect(isFocused() ? STAGE_SHADOW_FOCUSED : STAGE_SHADOW);
        setPadding(new Insets(0, OFFSET, OFFSET, OFFSET));
        setTranslateX(OFFSET);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());

        darkProperty().addListener((o, ov, nv) -> enableDarkMode(nv));

        headerPane.setOnMousePressed(press -> headerPane.setOnMouseDragged(drag -> {
            stage.setX(drag.getScreenX() - press.getSceneX());
            stage.setY(drag.getScreenY() - press.getSceneY());
        }));

        buttonBox.setOnMouseEntered(e -> {
            closeButton.setHovered(true);
            minimizeButton.setHovered(true);
            maximizeButton.setHovered(true);
        });
        headerBox.setOnMouseExited(e -> {
            closeButton.setHovered(false);
            minimizeButton.setHovered(false);
            maximizeButton.setHovered(false);
        });
        closeButton.setOnMouseReleased((Consumer<MouseEvent>) e -> {
            if (stage.isShowing()) { stage.hide(); }
        });
        minimizeButton.setOnMouseReleased((Consumer<MouseEvent>) e -> {
            if (stage.isShowing()) { stage.setIconified(!stage.isIconified()); }
        });
        maximizeButton.setOnMouseReleased((Consumer<MouseEvent>) e -> {
            if (stage.isShowing()) {
                if (stage.isFullScreen()) {
                    setPadding(new Insets(0, OFFSET, OFFSET, OFFSET));
                    setTranslateX(OFFSET);
                    stage.setFullScreen(false);
                } else {
                    setPadding(new Insets(0));
                    setTranslateX(0);
                    stage.setFullScreen(true);
                }
            }
        });

        stage.titleProperty().addListener(o -> headerText.setText(stage.getTitle()));
        stage.sceneProperty().addListener((o, ov, nv) -> {
            if (nv != null) {
                nv.setFill(Color.TRANSPARENT);
                nv.widthProperty().addListener(o1 -> resize());
                nv.heightProperty().addListener(o1 -> resize());
                Platform.runLater(() -> ResizeHelper.addResizeListener(stage));
            }
        });
        stage.focusedProperty().addListener((o, ov, nv) -> {
            setEffect(nv ? STAGE_SHADOW_FOCUSED : STAGE_SHADOW);
            headerPane.pseudoClassStateChanged(FOCUS_LOST_PSEUDO_CLASS, !nv);
            closeButton.setDisable(!nv);
            minimizeButton.setDisable(!nv);
            maximizeButton.setDisable(!nv);
        });
    }


    // ******************** Methods *******************************************
    @Override public final boolean isDark() { return dark.get(); }
    @Override public final void setDark(final boolean dark) { this.dark.set(dark); }
    @Override public final BooleanProperty darkProperty() { return dark; }

    public MacosAccentColor getAccentColor() { return accentColor.get(); }
    public void setAccentColor(final MacosAccentColor accentColor) { this.accentColor.set(accentColor); }
    public ObjectProperty<MacosAccentColor> accentColorProperty() { return accentColor; }

    private void enableDarkMode(final boolean enable) {
        headerPane.pseudoClassStateChanged(DARK_PSEUDO_CLASS, enable);
        headerText.pseudoClassStateChanged(DARK_PSEUDO_CLASS, enable);
        contentPane.pseudoClassStateChanged(DARK_PSEUDO_CLASS, enable);
        closeButton.pseudoClassStateChanged(DARK_PSEUDO_CLASS, enable);
        minimizeButton.pseudoClassStateChanged(DARK_PSEUDO_CLASS, enable);
        maximizeButton.pseudoClassStateChanged(DARK_PSEUDO_CLASS, enable);
        Helper.getAllNodes(contentPane).stream().filter(node -> node instanceof MacosControl).forEach(node -> ((MacosControl) node).setDark(enable));
    }

    private void setAllAccentColors(final MacosAccentColor accentColor) {
        List<Node> allNodes = Helper.getAllNodes(contentPane);
        allNodes.stream().filter(node -> node instanceof MacosControl).forEach(node -> node.setStyle(isDark() ? new StringBuilder("-accent-color-dark: ").append(accentColor.getDarkStyleClass()).append(";").toString() : new StringBuilder("-accent-color: ").append(accentColor.getDarkStyleClass()).append(";").toString()));
        allNodes.stream().filter(node -> node instanceof MacosSwitch).map(node -> (MacosSwitch) node).forEach(macosSwitch -> macosSwitch.setAccentColor(isDark() ? accentColor.getColorDark() : accentColor.getColorAqua()));
        allNodes.stream().filter(node -> node instanceof MacosCheckBox).map(node -> (MacosCheckBox) node).forEach(macosCheckBox -> macosCheckBox.setAccentColor(accentColor));
        allNodes.stream().filter(node -> node instanceof MacosRadioButton).map(node -> (MacosRadioButton) node).forEach(macosRadioButton -> macosRadioButton.setAccentColor(accentColor));
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() { return MacosWindow.class.getResource("apple.css").toExternalForm(); }


    // ******************** Layout ********************************************
    private void resize() {
        if (null != stage) {
            setPrefWidth(stage.getWidth());
            setPrefHeight(stage.getHeight());
        }
        double width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        double height = getHeight() - getInsets().getTop() - getInsets().getBottom();

        if (width > 0 && height > 0) {
            mainPane.setPrefWidth(width);
            mainPane.setPrefHeight(height);
        }
    }
}
