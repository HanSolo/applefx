package eu.hansolo.applefx;

import eu.hansolo.applefx.tools.Helper;
import eu.hansolo.applefx.tools.MacosAccentColor;
import eu.hansolo.applefx.tools.ResizeHelper;
import eu.hansolo.jdktools.OperatingSystem;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.StyleablePropertyFactory;
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

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.function.Consumer;

import static eu.hansolo.toolbox.Helper.getOperatingSystem;


public class MacosWindow extends Region implements MacosControlWithAccentColor {
    public enum Style { DEFAULT, DECORATED }

    public enum HeaderHeight {
        STANDARD(26.25),
        DOUBLE(52.5);

        private final double height;


        HeaderHeight(final double height) {
            this.height = height;
        }


        public double getHeight() { return height; }
    }

    public static final  double                                OFFSET                         = 40;
    private static final DropShadow                            HEADER_SHADOW                  = new DropShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.1), 1, 0.0, 0, 1);
    private static final DropShadow                            STAGE_SHADOW_FOCUSED           = new DropShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.5), 45.0, 0.0, 0.0, 15);
    private static final DropShadow                            STAGE_SHADOW                   = new DropShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.35), 20.0, 0.0, 0.0, 10);
    private static final PseudoClass                           DARK_PSEUDO_CLASS              = PseudoClass.getPseudoClass("dark");
    private static final PseudoClass                           WINDOW_FOCUS_LOST_PSEUDO_CLASS = PseudoClass.getPseudoClass("window-focus-lost");
    private static final StyleablePropertyFactory<MacosWindow> FACTORY                        = new StyleablePropertyFactory<>(Region.getClassCssMetaData());
    private static final CssMetaData                           HEADER_HEIGHT                  = FACTORY.createSizeCssMetaData("-header-height", s -> s.headerHeight, HeaderHeight.STANDARD.getHeight(), false);
    private        final boolean                               decorated;
    private              BooleanBinding                        showing;
    private              WatchService                          watchService;
    private              BooleanProperty                       dark;
    private              BooleanProperty                       windowFocusLost;
    private              ObjectProperty<MacosAccentColor>      accentColor;
    private              StyleableProperty<Number>             headerHeight;
    private              Stage                                 stage;
    private              MacosWindowButton                     closeButton;
    private              MacosWindowButton                     minimizeButton;
    private              MacosWindowButton                     maximizeButton;
    private              HBox                                  buttonBox;
    private              HBox                                  headerBox;
    private              AnchorPane                            headerPane;
    private              HBox                                  headerPaneLeftToolBar;
    private              HBox                                  headerPaneRightToolBar;
    private              MacosLabel                            headerText;
    private              AnchorPane                            contentPane;
    private              BorderPane                            mainPane;
    private              Parent                                content;


    // ******************** Constructors **************************************
    public MacosWindow(final Stage stage, final Parent content) {
        this(stage, content, Helper.isDarkMode(), MacosAccentColor.MULTI_COLOR, Style.DECORATED);
    }
    public MacosWindow(final Stage stage, final Parent content, final boolean darkMode) {
        this(stage, content, Helper.isDarkMode(), MacosAccentColor.MULTI_COLOR, Style.DECORATED);
    }
    public MacosWindow(final Stage stage, final Parent content, final MacosAccentColor accentColor) {
        this(stage, content, Helper.isDarkMode(), Helper.getMacosAccentColor(), Style.DECORATED);
    }
    public MacosWindow(final Stage stage, final Parent content, final boolean darkMode, final MacosAccentColor accentColor, final Style style) {
        if (null == stage) { throw new IllegalArgumentException("stage cannot be null"); }
        if (null == content) { throw new IllegalArgumentException("content cannot be null"); }
        this.stage           = stage;
        this.content         = content;
        this.dark            = new BooleanPropertyBase(darkMode) {
            @Override protected void invalidated() { pseudoClassStateChanged(DARK_PSEUDO_CLASS, get()); }
            @Override public Object getBean() { return MacosWindow.this; }
            @Override public String getName() { return "dark"; }
        };
        this.windowFocusLost = new BooleanPropertyBase() {
            @Override public Object getBean() { return MacosWindow.this; }
            @Override public String getName() { return "windowFocusLost"; }
        };
        this.accentColor     = new ObjectPropertyBase<>(accentColor) {
                @Override protected void invalidated() { setAllAccentColors(get()); }
                @Override public Object getBean() { return MacosWindow.this; }
                @Override public String getName() { return "accentColor"; }
            };
        this.decorated       = Style.DECORATED == style;
        this.headerHeight    = new StyleableObjectProperty<>() {
            @Override protected void invalidated() {
                headerPane.setStyle("-header-height: " + get() + ";");
                headerPaneLeftToolBar.setVisible(HeaderHeight.DOUBLE.getHeight() == get().doubleValue());
                headerPaneRightToolBar.setVisible(HeaderHeight.DOUBLE.getHeight() == get().doubleValue());
            }
            @Override public Object getBean() { return MacosWindow.this; }
            @Override public String getName() { return "headerHeight"; }
            @Override public CssMetaData<? extends Styleable, Number> getCssMetaData() { return HEADER_HEIGHT; }
        };

        init();
        registerListeners();

        enableDarkMode(isDark());
        setAllAccentColors(accentColor);
    }


    // ******************** Initialization ************************************
    private void init() {
        if (decorated) {
            stage.initStyle(StageStyle.TRANSPARENT);

            closeButton    = new MacosWindowButton(MacosButtonType.CLOSE, MacosButtonSize.NORMAL);
            minimizeButton = new MacosWindowButton(MacosButtonType.MINIMIZE, MacosButtonSize.NORMAL);
            maximizeButton = new MacosWindowButton(MacosButtonType.MAXIMIZE, MacosButtonSize.NORMAL);

            maximizeButton.setDisable(!stage.isResizable());

            buttonBox = new HBox(8, closeButton, minimizeButton, maximizeButton);
            buttonBox.setAlignment(Pos.CENTER);

            headerText = new MacosLabel(stage.getTitle());
            headerText.setMaxWidth(Double.MAX_VALUE);
            headerText.setPrefWidth(MacosLabel.USE_COMPUTED_SIZE);
            headerText.getStyleClass().add("macos-header-text");
            headerText.setAlignment(Pos.CENTER_LEFT);
            headerText.setMouseTransparent(true);

            headerPaneLeftToolBar = new HBox(5);
            headerPaneLeftToolBar.setPrefWidth(0);
            headerPaneLeftToolBar.setVisible(false);
            headerPaneLeftToolBar.setAlignment(Pos.CENTER_LEFT);

            headerPaneRightToolBar = new HBox(5);
            headerPaneRightToolBar.setPrefWidth(0);
            headerPaneRightToolBar.setVisible(false);
            headerPaneRightToolBar.setAlignment(Pos.CENTER_RIGHT);

            HBox.setHgrow(closeButton, Priority.NEVER);
            HBox.setHgrow(minimizeButton, Priority.NEVER);
            HBox.setHgrow(maximizeButton, Priority.NEVER);
            HBox.setHgrow(headerPaneLeftToolBar, Priority.ALWAYS);
            HBox.setHgrow(headerText, Priority.NEVER);
            HBox.setHgrow(headerPaneRightToolBar, Priority.ALWAYS);
            HBox.setMargin(headerPaneLeftToolBar, new Insets(0, 20, 0, 20));
            HBox.setMargin(headerPaneRightToolBar, new Insets(0, 5, 0, 60));

            headerBox = new HBox(buttonBox, headerPaneLeftToolBar, headerText, headerPaneRightToolBar);
            headerBox.setAlignment(Pos.CENTER_LEFT);
            AnchorPane.setTopAnchor(headerBox, 11d);
            AnchorPane.setRightAnchor(headerBox, 11d);
            AnchorPane.setBottomAnchor(headerBox, 11d);
            AnchorPane.setLeftAnchor(headerBox, 11d);

            headerPane = new AnchorPane();
            headerPane.getStyleClass().add("macos-header");
            headerPane.setEffect(HEADER_SHADOW);
            headerPane.getChildren().setAll(headerBox);

            AnchorPane.setTopAnchor(content, 1d);
            AnchorPane.setRightAnchor(content, 1d);
            AnchorPane.setBottomAnchor(content, 1d);
            AnchorPane.setLeftAnchor(content, 1d);
            contentPane = new AnchorPane(content);
            contentPane.getStyleClass().add("macos-content-pane");

            mainPane = new BorderPane();
            mainPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10), Insets.EMPTY)));
            mainPane.setCenter(contentPane);
            mainPane.setTop(headerPane);

            getChildren().add(mainPane);

            setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(10, 10, 10, 10, false), Insets.EMPTY)));
            setEffect(isFocused() ? STAGE_SHADOW_FOCUSED : STAGE_SHADOW);
            setPadding(new Insets(0, OFFSET, OFFSET, OFFSET));
            setTranslateX(OFFSET);
        } else {
            AnchorPane.setTopAnchor(content, 1d);
            AnchorPane.setRightAnchor(content, 1d);
            AnchorPane.setBottomAnchor(content, 1d);
            AnchorPane.setLeftAnchor(content, 1d);
            contentPane = new AnchorPane(content);
            contentPane.getStyleClass().add("macos-content-pane");
            contentPane.pseudoClassStateChanged(DARK_PSEUDO_CLASS, isDark());
            getChildren().add(contentPane);
        }
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        darkProperty().addListener((o, ov, nv) -> enableDarkMode(nv));
        if (decorated) {
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
                headerPane.pseudoClassStateChanged(WINDOW_FOCUS_LOST_PSEUDO_CLASS, !nv);
                windowFocusLost.set(!nv);
                setAllWindowFocusLost(!nv);
                closeButton.setDisable(!nv);
                minimizeButton.setDisable(!nv);
                maximizeButton.setDisable(!nv);
            });

            stage.resizableProperty().addListener((o, ov, nv) -> maximizeButton.setDisable(!nv));
        }
        if (null != getScene()) {
            setupBinding();
        } else {
            sceneProperty().addListener((o1, ov1, nv1) -> {
                if (null == nv1) { return; }
                if (null != getScene().getWindow()) {
                    setupBinding();
                } else {
                    sceneProperty().get().windowProperty().addListener((o2, ov2, nv2) -> {
                        if (null == nv2) { return; }
                        setupBinding();
                    });
                }
            });
        }
    }

    private void setupBinding() {
        showing = Bindings.createBooleanBinding(() -> {
            if (getScene() != null && getScene().getWindow() != null) {
                return getScene().getWindow().isShowing();
            } else {
                return false;
            }
        }, sceneProperty(), getScene().windowProperty(), getScene().getWindow().showingProperty());
        showing.addListener(o -> {
            if (showing.get()) {
                calculateMinSize();
                watchForAppearanceChanged();
            }
        });
    }


    // ******************** Methods *******************************************
    @Override public final boolean isDark() { return dark.get(); }
    @Override public final void setDark(final boolean dark) { this.dark.set(dark); }
    @Override public final BooleanProperty darkProperty() { return dark; }

    public final boolean isWindowFocusLost() { return windowFocusLost.get(); }
    public ReadOnlyBooleanProperty windowFocusLostProperty() { return windowFocusLost; }

    @Override public MacosAccentColor getAccentColor() { return accentColor.get(); }
    @Override public void setAccentColor(final MacosAccentColor accentColor) { this.accentColor.set(accentColor); }
    @Override public ObjectProperty<MacosAccentColor> accentColorProperty() { return accentColor; }

    public Double getHeaderHeight() { return headerHeight.getValue().doubleValue(); }
    public void setHeaderHeight(final HeaderHeight headerHeight)  {
        AnchorPane.setLeftAnchor(headerBox, HeaderHeight.DOUBLE == headerHeight ? 22d : 11d);
        setHeaderHeight(headerHeight.getHeight());
    }
    public void setHeaderHeight(final double headerHeight) { this.headerHeight.setValue(headerHeight); }
    public StyleableProperty<Number> headerHeightProperty() { return headerHeight; }

    public void addToToolbarLeft(final MacosToolbarButton toolbarButton) {
        if (HeaderHeight.STANDARD.getHeight() == getHeaderHeight()) { return; }
        if (headerPaneLeftToolBar.getChildren().contains(toolbarButton)) { return; }
        headerPaneLeftToolBar.getChildren().add(toolbarButton);
    }
    public void removeFromToolbarLeft(final MacosControl control) {
        if (headerPaneLeftToolBar.getChildren().contains(control)) { headerPaneLeftToolBar.getChildren().remove(control); }
    }

    public void addToToolbarRight(final MacosToolbarButton toolbarButton) {
        if (HeaderHeight.STANDARD.getHeight() == getHeaderHeight()) { return; }
        if (headerPaneRightToolBar.getChildren().contains(toolbarButton)) { return; }
        headerPaneRightToolBar.getChildren().add(toolbarButton);
    }
    public void removeFromToolbarRight(final MacosControl control) {
        if (headerPaneRightToolBar.getChildren().contains(control)) { headerPaneRightToolBar.getChildren().remove(control); }
    }

    public void dispose() {
        if (null != watchService) {
            try {
                watchService.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void enableDarkMode(final boolean enable) {
        if (decorated) {
            headerPane.pseudoClassStateChanged(DARK_PSEUDO_CLASS, enable);
            headerText.pseudoClassStateChanged(DARK_PSEUDO_CLASS, enable);
            contentPane.pseudoClassStateChanged(DARK_PSEUDO_CLASS, enable);
            closeButton.pseudoClassStateChanged(DARK_PSEUDO_CLASS, enable);
            minimizeButton.pseudoClassStateChanged(DARK_PSEUDO_CLASS, enable);
            maximizeButton.pseudoClassStateChanged(DARK_PSEUDO_CLASS, enable);
            Helper.getAllNodes(contentPane).stream().filter(node -> node instanceof MacosControl).forEach(node -> ((MacosControl) node).setDark(enable));
            Helper.getAllNodes(headerPaneLeftToolBar).stream().filter(node -> node instanceof MacosControl).forEach(node -> ((MacosControl) node).setDark(enable));
            Helper.getAllNodes(headerPaneRightToolBar).stream().filter(node -> node instanceof MacosControl).forEach(node -> ((MacosControl) node).setDark(enable));
        } else {
            contentPane.pseudoClassStateChanged(DARK_PSEUDO_CLASS, enable);
            Helper.getAllNodes(content).stream().filter(node -> node instanceof MacosControl).forEach(node -> ((MacosControl) node).setDark(enable));
        }
    }

    private void setAllAccentColors(final MacosAccentColor accentColor) {
        List<Node> allNodes = Helper.getAllNodes(contentPane);
        if (Platform.isFxApplicationThread()) {
            allNodes.stream().filter(node -> node instanceof MacosControl).forEach(node -> node.setStyle(isDark() ? new StringBuilder("-accent-color-dark: ").append(accentColor.getDarkStyleClass()).append(";").toString() : new StringBuilder("-accent-color: ").append(accentColor.getDarkStyleClass()).append(";").toString()));
            //allNodes.stream().filter(node -> node instanceof MacosControlWithAccentColor).map(node -> ((MacosControlWithAccentColor) node)).forEach(macosControlWithAccentColor -> macosControlWithAccentColor.setAccentColor(accentColor));
            allNodes.stream().filter(node -> node instanceof MacosSwitch).map(node -> (MacosSwitch) node).forEach(macosSwitch -> macosSwitch.setAccentColor(isDark() ? accentColor.getColorDark() : accentColor.getColorAqua()));
            allNodes.stream().filter(node -> node instanceof MacosCheckBox).map(node -> (MacosCheckBox) node).forEach(macosCheckBox -> macosCheckBox.setAccentColor(accentColor));
            allNodes.stream().filter(node -> node instanceof MacosRadioButton).map(node -> (MacosRadioButton) node).forEach(macosRadioButton -> macosRadioButton.setAccentColor(accentColor));
            allNodes.stream().filter(node -> node instanceof MacosComboBox).map(node -> (MacosComboBox) node).forEach(macosComboBox -> macosComboBox.setAccentColor(accentColor));
            allNodes.stream().filter(node -> node instanceof MacosSlider).map(node -> (MacosSlider) node).forEach(macosSlider -> macosSlider.setAccentColor(accentColor));
            allNodes.stream().filter(node -> node instanceof MacosTextField).map(node -> (MacosTextField) node).forEach(macosTextField -> macosTextField.setAccentColor(accentColor));
        } else {
            Platform.runLater(() -> {
                allNodes.stream().filter(node -> node instanceof MacosControl).forEach(node -> node.setStyle(isDark() ? new StringBuilder("-accent-color-dark: ").append(accentColor.getDarkStyleClass()).append(";").toString() : new StringBuilder("-accent-color: ").append(accentColor.getDarkStyleClass()).append(";").toString()));
                //allNodes.stream().filter(node -> node instanceof MacosControlWithAccentColor).map(node -> ((MacosControlWithAccentColor) node)).forEach(macosControlWithAccentColor -> macosControlWithAccentColor.setAccentColor(accentColor));
                allNodes.stream().filter(node -> node instanceof MacosSwitch).map(node -> (MacosSwitch) node).forEach(macosSwitch -> macosSwitch.setAccentColor(isDark() ? accentColor.getColorDark() : accentColor.getColorAqua()));
                allNodes.stream().filter(node -> node instanceof MacosCheckBox).map(node -> (MacosCheckBox) node).forEach(macosCheckBox -> macosCheckBox.setAccentColor(accentColor));
                allNodes.stream().filter(node -> node instanceof MacosRadioButton).map(node -> (MacosRadioButton) node).forEach(macosRadioButton -> macosRadioButton.setAccentColor(accentColor));
                allNodes.stream().filter(node -> node instanceof MacosComboBox).map(node -> (MacosComboBox) node).forEach(macosComboBox -> macosComboBox.setAccentColor(accentColor));
                allNodes.stream().filter(node -> node instanceof MacosSlider).map(node -> (MacosSlider) node).forEach(macosSlider -> macosSlider.setAccentColor(accentColor));
                allNodes.stream().filter(node -> node instanceof MacosTextField).map(node -> (MacosTextField) node).forEach(macosTextField -> macosTextField.setAccentColor(accentColor));
            });
        }
    }

    private void setAllWindowFocusLost(final boolean windowFocusLost) {
        List<Node> allNodes = Helper.getAllNodes(contentPane);
        allNodes.stream().filter(node -> node instanceof MacosControl).forEach(node -> node.pseudoClassStateChanged(WINDOW_FOCUS_LOST_PSEUDO_CLASS, windowFocusLost));
        allNodes.stream().filter(node -> node instanceof MacosSwitch).map(node -> (MacosSwitch) node).forEach(macosSwitch -> macosSwitch.setWindowFocusLost(windowFocusLost));
        allNodes.stream().filter(node -> node instanceof MacosSlider).map(node -> (MacosSlider) node).forEach(macosSlider -> macosSlider.setWindowFocusLost(windowFocusLost));
    }

    private void calculateMinSize() {
        double width  = content.getChildrenUnmodifiable().stream().map(node -> node.getLayoutBounds().getWidth()).reduce(0.0, Double::sum);
        double height = content.getChildrenUnmodifiable().stream().map(node -> node.getLayoutBounds().getHeight()).reduce(0.0, Double::sum);
        contentPane.setMinSize(width, height);
    }

    private void watchForAppearanceChanged() {
        if (OperatingSystem.MACOS != getOperatingSystem()) { return; }
        final Path path = FileSystems.getDefault().getPath(System.getProperty("user.home"), "/Library/Preferences/");
        new Thread(() -> {
            try {
                watchService = FileSystems.getDefault().newWatchService();
                try {
                    path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                    WatchKey key;
                    while ((key = watchService.take()) != null) {
                        for (WatchEvent<?> event : key.pollEvents()) {
                            final Path changed = (Path) event.context();
                            if (changed.endsWith(".GlobalPreferences.plist")) {
                                setDark(Helper.isDarkMode());
                                setAccentColor(Helper.getMacosAccentColor());
                            }
                        }
                        key.reset();
                    }
                } finally {
                    watchService.close();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() { return MacosWindow.class.getResource("apple.css").toExternalForm(); }


    // ******************** Layout ********************************************
    private void resize() {
        if (null != stage) {
            stage.setMinWidth(contentPane.getMinWidth() + 2 * OFFSET);
            stage.setMinHeight(contentPane.getMinHeight() + 2 * OFFSET);
            setMinWidth(contentPane.getMinWidth() + 2 * OFFSET);
            setMinHeight(contentPane.getMinHeight() + 2 * OFFSET);
            setPrefWidth(stage.getWidth());
            setPrefHeight(stage.getHeight());
        }
        double width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        double height = getHeight() - getInsets().getTop() - getInsets().getBottom();

        if (width > 0 && height > 0) {
            if (decorated) {
                mainPane.setPrefSize(width, height);
            } else {
                contentPane.setPrefSize(width, height);
            }
        }
    }
}
