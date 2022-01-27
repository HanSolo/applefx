package eu.hansolo.applefx;

import eu.hansolo.applefx.MacosWindow.HeaderHeight;
import eu.hansolo.applefx.tools.Helper;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;


public class Demo3 extends Application {
    private MacosWindow           macosWindow;
    private MacosButton           macosButton;
    private MacosSwitch           macosSwitch;
    private MacosCheckBox         macosCheckBox;
    private MacosTextField        macosTextField;
    private MacosRadioButton      macosRadioButton1;
    private MacosRadioButton      macosRadioButton2;
    private MacosSlider           macosSlider1;
    private MacosSlider           macosSlider2;
    private MacosComboBox<String> macosComboBox;


    @Override public void init() {
        System.out.println("Operating System: " + eu.hansolo.toolbox.Helper.getOperatingSystem().getUiString());
        System.out.println("Architecture    : " + eu.hansolo.toolbox.Helper.getArchitecture());

        macosButton       = new MacosButton("Click me");
        macosSwitch       = new MacosSwitch();
        macosCheckBox     = new MacosCheckBox("Check me");
        macosTextField    = new MacosTextField();
        macosRadioButton1 = new MacosRadioButton("Select me");
        macosRadioButton2 = new MacosRadioButton("Select me");
        macosSlider1       = new MacosSlider(0, 100, 50);
        macosSlider1.setShowTickMarks(true);
        macosSlider2       = new MacosSlider(0, 100, 50);
        macosComboBox      = new MacosComboBox<>();
        macosComboBox.getItems().setAll(List.of("Neo", "Anton", "Lilli", "Sandra", "Gerrit"));

        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(macosRadioButton1, macosRadioButton2);
    }

    private void registerListeners() { }

    @Override public void start(Stage stage) {
        VBox radioBox   = new VBox(10, macosRadioButton1, macosRadioButton2);
        radioBox.setAlignment(Pos.CENTER);
        VBox controlBox = new VBox(20, macosButton, macosSwitch, macosCheckBox, macosTextField, radioBox, macosSlider1, macosSlider2, macosComboBox);
        controlBox.setAlignment(Pos.CENTER);
        controlBox.setPadding(new Insets(10));

        StackPane pane = new StackPane(controlBox);
        pane.setPadding(new Insets(10));
        pane.setPrefSize(400, 500);
        pane.setPadding(new Insets(0));

        macosWindow = new MacosWindow(stage, pane, Helper.isDarkMode(), Helper.getMacosAccentColor());

        Scene scene = new Scene(macosWindow);

        stage.setTitle("MacosWindow");
        stage.setScene(scene);

        stage.show();

        //macosWindow.setAccentColor(MacosAccentColor.GREEN);
        //macosWindow.setHeaderHeight(HeaderHeight.STANDARD);
        registerListeners();
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

