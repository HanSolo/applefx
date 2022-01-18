package eu.hansolo.applefx;

import eu.hansolo.applefx.IosMultiButton.Type;
import eu.hansolo.applefx.event.MacEvt;
import eu.hansolo.applefx.tools.Helper;
import eu.hansolo.applefx.tools.MacOSSystemColor;
import eu.hansolo.toolbox.evt.Evt;
import eu.hansolo.toolbox.evt.EvtType;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Demo2 extends Application {
    private MacosButton              macosButton;
    private MacosSwitch              macosSwitch;
    private MacosCheckBox            macosCheckBox;
    private MacosWindowButton        macosWindowButtonClose;
    private MacosWindowButton        macosWindowButtonMinimize;
    private MacosWindowButton        macosWindowButtonMaximize;
    private MacosProgress            macosProgress;
    private MacosSlider              macosSlider;
    private MacosTextField           macosTextField;
    private MacosSeparator           macosSeparator;
    private MacosScrollPane          macosScrollPane;

    private MacosButton              macosButtonDark;
    private MacosSwitch              macosSwitchDark;
    private MacosCheckBox            macosCheckBoxDark;
    private MacosWindowButton        macosWindowButtonCloseDark;
    private MacosWindowButton        macosWindowButtonMinimizeDark;
    private MacosWindowButton        macosWindowButtonMaximizeDark;
    private MacosProgress            macosProgressDark;
    private MacosSlider              macosSliderDark;
    private MacosTextField           macosTextFieldDark;
    private MacosSeparator           macosSeparatorDark;
    private MacosScrollPane          macosScrollPaneDark;


    @Override public void init() {
        System.out.println("Operating System: " + eu.hansolo.toolbox.Helper.getOperatingSystem().getUiString());
        System.out.println("Architecture    : " + eu.hansolo.toolbox.Helper.getArchitecture());
        System.out.println("Dark Mode       : " + Helper.isDarkMode());

        // Bright controls
        macosButton = new MacosButton("Test");

        macosSwitch = MacosSwitchBuilder.create()
                                        .selectedColor(MacOSSystemColor.PINK.getColorDark())
                                        .selected(true)
                                        //.showOnOffText(true)
                                        //.selectedColor(MacOSSystemColor.GREEN.getColorAqua())
                                        .build();

        macosCheckBox = new MacosCheckBox("Check me");

        macosWindowButtonClose    = new MacosWindowButton(WindowButtonType.CLOSE, WindowButtonSize.LARGE);
        macosWindowButtonMinimize = new MacosWindowButton(WindowButtonType.MINIMIZE, WindowButtonSize.LARGE);
        macosWindowButtonMaximize = new MacosWindowButton(WindowButtonType.MAXIMIZE, WindowButtonSize.LARGE);

        HBox macosWindowButtonBox = new HBox(5, macosWindowButtonClose, macosWindowButtonMinimize, macosWindowButtonMaximize);
        macosWindowButtonBox.setOnMouseEntered(e -> {
            macosWindowButtonClose.setHovered(true);
            macosWindowButtonMinimize.setHovered(true);
            macosWindowButtonMaximize.setHovered(true);
        });
        macosWindowButtonBox.setOnMouseExited(e -> {
            macosWindowButtonClose.setHovered(false);
            macosWindowButtonMinimize.setHovered(false);
            macosWindowButtonMaximize.setHovered(false);
        });

        macosProgress = new MacosProgress(0.3);
        macosProgress.setPrefSize(32, 32);

        macosSlider = new MacosSlider();
        macosSlider.setBlockIncrement(10);
        macosSlider.setShowTickMarks(true);
        macosSlider.setSnapToTicks(true);

        macosTextField = new MacosTextField();
        macosTextField.setPromptText("prompt text");

        macosSeparator = new MacosSeparator(Orientation.HORIZONTAL);

        VBox macosPane = new VBox(10, macosButton, macosSwitch, macosCheckBox, macosWindowButtonBox, macosProgress, macosSlider, macosSeparator, macosTextField);

        macosScrollPane = new MacosScrollPane(macosPane);
        macosScrollPane.setDark(true);
        macosScrollPane.setFitToWidth(true);
        macosScrollPane.setFitToHeight(true);

        // Dark controls
        macosButtonDark = new MacosButton("Test");
        macosButtonDark.setDark(true);

        macosSwitchDark = MacosSwitchBuilder.create()
                                            .dark(true)
                                            .selectedColor(MacOSSystemColor.PINK.getColorDark())
                                            .selected(true)
                                            //.showOnOffText(true)
                                            //.selectedColor(MacOSSystemColor.GREEN.getColorAqua())
                                            .build();

        macosCheckBoxDark = new MacosCheckBox("Check me");
        macosCheckBoxDark.setDark(true);

        macosWindowButtonCloseDark    = new MacosWindowButton(WindowButtonType.CLOSE, WindowButtonSize.LARGE);
        macosWindowButtonMinimizeDark = new MacosWindowButton(WindowButtonType.MINIMIZE, WindowButtonSize.LARGE);
        macosWindowButtonMaximizeDark = new MacosWindowButton(WindowButtonType.MAXIMIZE, WindowButtonSize.LARGE);

        HBox macosWindowButtonBoxDark = new HBox(5, macosWindowButtonCloseDark, macosWindowButtonMinimizeDark, macosWindowButtonMaximizeDark);
        macosWindowButtonBoxDark.setOnMouseEntered(e -> {
            macosWindowButtonCloseDark.setHovered(true);
            macosWindowButtonMinimizeDark.setHovered(true);
            macosWindowButtonMaximizeDark.setHovered(true);
        });
        macosWindowButtonBoxDark.setOnMouseExited(e -> {
            macosWindowButtonCloseDark.setHovered(false);
            macosWindowButtonMinimizeDark.setHovered(false);
            macosWindowButtonMaximizeDark.setHovered(false);
        });

        macosProgressDark = new MacosProgress(0.3);
        macosProgress.setDark(true);
        macosProgressDark.setPrefSize(32, 32);

        macosSliderDark = new MacosSlider();
        macosSliderDark.setDark(true);
        macosSliderDark.setBlockIncrement(10);
        macosSliderDark.setShowTickMarks(true);
        macosSliderDark.setSnapToTicks(true);

        macosTextFieldDark = new MacosTextField();
        macosTextFieldDark.setPromptText("prompt text");
        macosTextFieldDark.setDark(true);

        macosSeparatorDark = new MacosSeparator(Orientation.HORIZONTAL);
        macosSeparatorDark.setDark(true);

        VBox macosPaneDark = new VBox(10, macosButtonDark, macosSwitchDark, macosCheckBoxDark, macosWindowButtonBoxDark, macosProgressDark, macosSliderDark, macosSeparatorDark, macosTextFieldDark);

        macosScrollPaneDark = new MacosScrollPane(macosPaneDark);
        macosScrollPaneDark.setDark(true);
        macosScrollPaneDark.setFitToWidth(true);
        macosScrollPaneDark.setFitToHeight(true);


        registerListeners();
    }

    private void registerListeners() {

    }

    @Override public void start(Stage stage) {
        VBox pane = new VBox(10, macosScrollPane);
        pane.setPadding(new Insets(20));
        pane.setAlignment(Pos.CENTER);
        pane.setBackground(new Background(new BackgroundFill(MacOSSystemColor.BACKGROUND.getColorAqua(), CornerRadii.EMPTY, Insets.EMPTY)));

        VBox paneDark = new VBox(10, macosScrollPaneDark);
        paneDark.setPadding(new Insets(20));
        paneDark.setAlignment(Pos.CENTER);
        paneDark.setBackground(new Background(new BackgroundFill(MacOSSystemColor.BACKGROUND.getColorDark(), CornerRadii.EMPTY, Insets.EMPTY)));

        HBox mainPane = new HBox(pane, paneDark);

        mainPane.setPadding(new Insets(20));
        mainPane.setAlignment(Pos.CENTER);
        mainPane.setBackground(new Background(new BackgroundFill(MacOSSystemColor.BACKGROUND.getColorDark(), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(mainPane);

        stage.setTitle("Apple FX");
        stage.setScene(scene);
        stage.show();
    }

    @Override public void stop() {
        System.exit(0);
    }

    private IosEntry createIosEntry(final String TITLE, final String SUB_TITLE, final Node LEFT_NODE, final Node RIGHT_NODE, final boolean HAS_ACTION, final boolean HAS_DELETE) {
        IosEntry entry = new IosEntry(LEFT_NODE, TITLE, SUB_TITLE, RIGHT_NODE);
        entry.setHasAction(HAS_ACTION);
        entry.setHasDelete(HAS_DELETE);
        return entry;
    }

    private IosSwitch createSwitch(final Color SELECTED_COLOR, final boolean SHOW_ON_OFF_TEXT) {
        IosSwitch iosSwitch = IosSwitchBuilder.create()
                                              .minSize(51, 31)
                                              .maxSize(51, 31)
                                              .prefSize(51, 31)
                                              .showOnOffText(SHOW_ON_OFF_TEXT)
                                              .selectedColor(null == SELECTED_COLOR ? IosSwitch.DEFAULT_SELECTED_COLOR : SELECTED_COLOR)
                                              .build();
        return  iosSwitch;
    }

    private IosMultiButton createMultiButton(final Type MULTI_BUTTON_TYPE, final Color SELECTED_COLOR, final boolean SELECTED) {
        IosMultiButton iosMultiButton = IosMultiButtonBuilder.create()
                                                             .type(MULTI_BUTTON_TYPE)
                                                             .selectedColor(SELECTED_COLOR)
                                                             .selected(SELECTED)
                                                             .build();
        return iosMultiButton;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
