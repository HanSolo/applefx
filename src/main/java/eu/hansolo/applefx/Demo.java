package eu.hansolo.applefx;

import eu.hansolo.applefx.MacosWindow.HeaderHeight;
import eu.hansolo.applefx.MacosWindow.Style;
import eu.hansolo.applefx.event.MacEvt;
import eu.hansolo.applefx.fonts.Fonts;
import eu.hansolo.applefx.tools.Helper;
import eu.hansolo.applefx.tools.SFIcon;
import eu.hansolo.jdktools.util.Helper.OsArcMode;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;


public class Demo extends Application {
    private MacosWindow           macosWindow;
    private MacosButton           macosButton;
    private MacosButton           macosDefaultButton;
    private MacosSwitch           macosSwitch;
    private MacosSwitch           iosSwitch;
    private MacosCheckBoxSwitch   macosSwitch1;
    private MacosCheckBox         macosCheckBox;
    private MacosTextField        macosTextField;
    private MacosRadioButton      macosRadioButton1;
    private MacosRadioButton      macosRadioButton2;
    private MacosSlider           macosSlider1;
    private MacosSlider           macosSlider2;
    private MacosComboBox<String> macosComboBox;
    private MacosToolbarButton    macosToolbarButtonBack;
    private MacosToolbarButton    macosToolbarButtonForth;
    private ToggleGroup           toggleButtonGroup;
    private MacosToggleButton     macosToggleButton1;
    private MacosToggleButton     macosToggleButton2;
    private MacosToggleButton     macosToggleButton3;
    private MacosToggleButton     macosToggleButton4;
    private MacosToggleButtonBar  macosToggleButtonBar;
    private MacosAddRemoveButton  macosPlusMinusButton;
    private MacosSelectableLabel  macosSFSymbolLabel;


    @Override public void init() {
        OsArcMode sysInfo = eu.hansolo.jdktools.util.Helper.getOperaringSystemArchitectureOperatingMode();
        System.out.println("Operating System: " + sysInfo.operatingMode().getUiString());
        System.out.println("Architecture    : " + sysInfo.architecture().getUiString());
        System.out.println("Operating Mode  : " + sysInfo.operatingMode().getUiString());

        macosButton             = new MacosButton("Click me");
        macosDefaultButton      = new MacosButton("Default", true);
        macosSwitch             = new MacosSwitch();
        iosSwitch               = new MacosSwitch();
        iosSwitch.setIos(true);
        iosSwitch.setShowOnOffText(true);
        macosSwitch1            = new MacosCheckBoxSwitch();
        macosCheckBox           = new MacosCheckBox("Check me");
        macosTextField          = new MacosTextField();
        macosRadioButton1       = new MacosRadioButton("Select me");
        macosRadioButton2       = new MacosRadioButton("Select me");
        macosSlider1            = new MacosSlider(0, 100, 50);
        macosSlider1.setShowTickMarks(true);
        macosSlider2            = new MacosSlider(0, 100, 50);
        macosComboBox           = new MacosComboBox<>();
        macosComboBox.getItems().setAll(List.of("Neo", "Anton", "Lilli", "Sandra", "Gerrit"));
        macosToolbarButtonBack  = new MacosToolbarButton("M4.615,9.079c0,0.298 0.11,0.554 0.35,0.783l6.85,6.705c0.19,0.194 0.43,0.291 0.72,0.291c0.57,-0 1.01,-0.44 1.01,-1.02c0,-0.281 -0.11,-0.536 -0.3,-0.729l-6.18,-6.03l6.18,-6.029c0.19,-0.202 0.3,-0.457 0.3,-0.738c0,-0.572 -0.44,-1.012 -1.01,-1.012c-0.29,0 -0.53,0.098 -0.72,0.291l-6.85,6.706c-0.24,0.229 -0.34,0.483 -0.35,0.782Z");
        macosToolbarButtonForth = new MacosToolbarButton("M13.545,9.079c0,-0.299 -0.11,-0.553 -0.34,-0.782l-6.85,-6.706c-0.2,-0.193 -0.43,-0.291 -0.72,-0.291c-0.57,0 -1.02,0.44 -1.02,1.012c-0,0.281 0.11,0.536 0.3,0.738l6.19,6.029l-6.19,6.03c-0.18,0.193 -0.3,0.448 -0.3,0.729c-0,0.58 0.45,1.02 1.02,1.02c0.29,-0 0.52,-0.097 0.72,-0.291l6.85,-6.705c0.24,-0.229 0.34,-0.485 0.34,-0.783Z");

        toggleButtonGroup  = new ToggleGroup();
        macosToggleButton1 = createToggleButton("Option 1", toggleButtonGroup);
        macosToggleButton2 = createToggleButton("Option 3", toggleButtonGroup);
        macosToggleButton3 = createToggleButton("Option 3", toggleButtonGroup);
        macosToggleButton4 = createToggleButton("Option 4", toggleButtonGroup);

        macosToggleButton2.setSelected(true);

        macosToggleButtonBar = new MacosToggleButtonBar(macosToggleButton1, new MacosToggleButtonBarSeparator(), macosToggleButton2, new MacosToggleButtonBarSeparator(), macosToggleButton3, new MacosToggleButtonBarSeparator(), macosToggleButton4);
        HBox.setHgrow(macosToggleButtonBar, Priority.ALWAYS);
        VBox.setVgrow(macosToggleButtonBar, Priority.NEVER);
        VBox.setMargin(macosToggleButtonBar, new Insets(10, 10, 15, 10));

        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(macosRadioButton1, macosRadioButton2);

        macosPlusMinusButton = new MacosAddRemoveButton();
        macosPlusMinusButton.setRemoveDisable(true);
        macosPlusMinusButton.addMacEvtObserver(MacEvt.ADD, e -> System.out.println("Add pressed"));
        macosPlusMinusButton.addMacEvtObserver(MacEvt.REMOVE, e -> System.out.println("Removed pressed"));

        macosSFSymbolLabel = new MacosSelectableLabel(SFIcon.camera.utf8());
        macosSFSymbolLabel.setFont(Fonts.sfIconSets(32));
    }

    private MacosToggleButton createToggleButton(final String text, final ToggleGroup toggleGroup) {
        MacosToggleButton toggleButton = new MacosToggleButton(text);
        toggleButton.setMaxWidth(Double.MAX_VALUE);
        toggleButton.setToggleGroup(toggleGroup);
        //toggleButton.addEventFilter(MouseEvent.MOUSE_PRESSED, handler);
        HBox.setHgrow(toggleButton, Priority.ALWAYS);
        return toggleButton;
    }

    private void registerListeners() {
        macosSFSymbolLabel.setOnMousePressed(e -> macosSFSymbolLabel.setSelected(!macosSFSymbolLabel.isSelected()));
    }

    @Override public void start(Stage stage) {
        VBox radioBox   = new VBox(10, macosRadioButton1, macosRadioButton2);
        radioBox.setAlignment(Pos.CENTER);
        VBox controlBox = new VBox(20, macosButton, macosDefaultButton, macosSwitch, iosSwitch, macosSwitch1, macosCheckBox, macosTextField, radioBox, macosSlider1, macosSlider2, macosComboBox, macosPlusMinusButton, macosSFSymbolLabel, macosToggleButtonBar);
        controlBox.setAlignment(Pos.CENTER);
        controlBox.setPadding(new Insets(10));

        StackPane pane = new StackPane(controlBox);
        pane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setPadding(new Insets(10));
        pane.setPrefSize(400, 500);
        pane.setPadding(new Insets(0));

        macosWindow = new MacosWindow(stage, pane, Helper.isDarkMode(), Helper.getMacosAccentColor(), Style.DECORATED, true);
        //macosWindow = new MacosWindow(stage, pane, Helper.isDarkMode(), Helper.getMacosAccentColor(), Style.DEFAULT, false);
        macosWindow.setHeaderHeight(HeaderHeight.DOUBLE);
        macosWindow.addToToolbarLeft(macosToolbarButtonBack);
        macosWindow.addToToolbarLeft(macosToolbarButtonForth);


        Scene scene = new Scene(macosWindow);

        stage.setTitle("MacosWindow");
        stage.setScene(scene);

        stage.show();

        //macosWindow.setAccentColor(MacosAccentColor.PINK);
        //macosWindow.setHeaderHeight(HeaderHeight.DOUBLE);
        registerListeners();
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

