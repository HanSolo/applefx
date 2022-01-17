/*
 * Copyright (c) 2018 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.applefx;

import eu.hansolo.applefx.IosMultiButton.Type;
import eu.hansolo.applefx.event.MacEvt;
import eu.hansolo.applefx.tools.Helper;
import eu.hansolo.applefx.tools.MacOSSystemColor;
import eu.hansolo.toolbox.evt.Evt;
import eu.hansolo.toolbox.evt.EvtType;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;


public class Demo extends Application {
    private ObservableList<IosEntry> entries;
    private IosListView              listView;
    private IosEntry                 entry1;
    private IosEntry                 entry2;
    private IosEntry                 entry3;
    private IosEntry                 entry4;
    private IosEntry                 entry5;
    private IosEntry                 entry6;
    private IosEntry                 entry7;
    private IosEntry                 entry8;
    private IosEntry                 entry9;
    private IosSlider                slider;
    private IosSlider                balanceSlider;
    private IosSegmentedButtonBar    buttonBar1;
    private IosSegmentedButtonBar    buttonBar2;
    private IosPlusMinusButton       plusMinusButton;
    private IosButton                button;
    private SVGPath                  wifiEnabled;
    private SVGPath                  wifiDisabled;
    private IosRoundToggleButton     toggleButton;
    private IosSwitch                switch1;
    private MacosButton              macosButton;
    private MacosSwitch              macosSwitch;
    private MacosCheckBox            macosCheckBox;
    private MacosSlider              macosSlider;
    private MacosTextField           macosTextField;
    private MacosSeparator           macosSeparator;
    private MacosScrollPane          macosScrollPane;


    @Override public void init() {
        System.out.println("Operating System: " + eu.hansolo.toolbox.Helper.getOperatingSystem().getUiString());
        System.out.println("Architecture    : " + eu.hansolo.toolbox.Helper.getArchitecture());
        System.out.println("Dark Mode       : " + Helper.isDarkMode());


        entry1 = createIosEntry("Title 1", "Subtitle 1", createMultiButton(Type.SMALL_DOT, MacOSSystemColor.PURPLE.getColorAqua(), false), createSwitch(MacOSSystemColor.PURPLE.getColorAqua(), false), true, true);
        entry2 = createIosEntry("Title 2", "Subtitle 2", createMultiButton(Type.ADD, MacOSSystemColor.GREEN.getColorAqua(), false), createSwitch(MacOSSystemColor.PINK.getColorAqua(), true), true, false);
        entry3 = createIosEntry("Title 3", "Subtitle 3", createMultiButton(Type.DELETE, MacOSSystemColor.RED.getColorAqua(), false), createSwitch(MacOSSystemColor.GREEN.getColorAqua(), false), false, false);
        entry4 = createIosEntry("Title 4", "Subtitle 4", createMultiButton(Type.DOT, MacOSSystemColor.ORANGE.getColorAqua(), false), createMultiButton(Type.CHECKBOX, MacOSSystemColor.GREEN.getColorAqua(), true), false, true);
        entry5 = createIosEntry("Title 5", "Subtitle 5", createMultiButton(Type.INFO, MacOSSystemColor.BLUE.getColorAqua(), false), createMultiButton(Type.CHECKBOX, MacOSSystemColor.GREEN.getColorAqua(), false), false, false);
        entry6 = createIosEntry("Title 6", "Subtitle 6", createMultiButton(Type.PLUS, MacOSSystemColor.ORANGE.getColorAqua(), false), createMultiButton(Type.ADD, MacOSSystemColor.GREEN.getColorAqua(), false), false, false);
        entry7 = createIosEntry("Title 7", "Subtitle 7", null, createMultiButton(Type.DELETE, MacOSSystemColor.GREEN.getColorAqua(), false), false, false);
        entry8 = createIosEntry("Title 8", "Subtitle 8", createMultiButton(Type.DOT, MacOSSystemColor.GREEN.getColorAqua(), false), createMultiButton(Type.CHECK_MARK, MacOSSystemColor.BLUE.getColorAqua(), true), false, false);
        entry9 = createIosEntry("Title 9", "Subtitle 9", null, createMultiButton(Type.FORWARD, Color.rgb(0, 0, 0, 0.2), true), false, false);

        entry1.setActionLabel("Press");

        entry9.addMacEvtObserver(MacEvt.PRESSED,  e -> System.out.println("Move to next screen"));

        entries = FXCollections.observableArrayList();
        entries.addAll(entry1, entry2, entry3, entry4, entry5, entry6, entry7, entry8, entry9);

        listView = new IosListView(entries);
        listView.setPrefSize(400, 500);
        listView.setPlaceholder(new Label("No entries loaded"));
        listView.setCellFactory(p -> new IosEntryCell());

        slider = new IosSlider();
        slider.setDark(true);

        balanceSlider = new IosSlider();
        balanceSlider.setBalance(true);
        balanceSlider.setDark(true);

        Button button1 = new Button("Label");
        Button button2 = new Button("Label");
        Button button3 = new Button("Label");
        Button button4 = new Button("Label");
        Button button5 = new Button("Label");

        buttonBar1 = new IosSegmentedButtonBar(button1, button2, button3, button4, button5);
        buttonBar1.setPadding(new Insets(5));


        ToggleButton toggleButton1 = new ToggleButton("Label");
        ToggleButton toggleButton2 = new ToggleButton("Label");
        ToggleButton toggleButton3 = new ToggleButton("Label");
        ToggleButton toggleButton4 = new ToggleButton("Label");
        ToggleButton toggleButton5 = new ToggleButton("Label");

        ToggleGroup toggleGroup = new ToggleGroup();
        toggleButton1.setToggleGroup(toggleGroup);
        toggleButton2.setToggleGroup(toggleGroup);
        toggleButton3.setToggleGroup(toggleGroup);
        toggleButton4.setToggleGroup(toggleGroup);
        toggleButton5.setToggleGroup(toggleGroup);

        buttonBar2 = new IosSegmentedButtonBar(toggleButton1, toggleButton2, toggleButton3, toggleButton4, toggleButton5);
        buttonBar2.setPadding(new Insets(5));

        plusMinusButton = new IosPlusMinusButton();
        plusMinusButton.setPadding(new Insets(5));

        button = new IosButton("Button");
        button.setPadding(new Insets(5));

        wifiEnabled  = new SVGPath();
        wifiEnabled.setContent("M13.289,17.854c-0.195,0.195 -0.512,0.195 -0.707,0l-3.441,-3.433c-0.184,-0.183 -0.196,-0.476 -0.029,-0.674c0.949,-1.124 2.341,-1.776 3.825,-1.776c1.483,0 2.875,0.651 3.824,1.774c0.167,0.198 0.155,0.491 -0.029,0.674l-3.443,3.435Zm6.276,-6.261c-0.202,0.201 -0.531,0.194 -0.723,-0.016c-1.514,-1.654 -3.645,-2.598 -5.906,-2.598c-2.262,0 -4.393,0.944 -5.907,2.598c-0.192,0.21 -0.521,0.217 -0.723,0.016l-1.415,-1.412c-0.19,-0.19 -0.196,-0.496 -0.013,-0.692c2.081,-2.234 4.982,-3.503 8.058,-3.503c3.076,0 5.976,1.269 8.057,3.503c0.183,0.196 0.178,0.502 -0.013,0.692l-1.415,1.412Zm5.668,-6.343c0.187,0.196 0.184,0.505 -0.008,0.697l-1.415,1.411c-0.199,0.198 -0.523,0.194 -0.716,-0.01c-2.644,-2.781 -6.293,-4.355 -10.158,-4.355c-3.866,0 -7.515,1.574 -10.159,4.355c-0.193,0.204 -0.517,0.208 -0.716,0.01l-1.415,-1.411c-0.192,-0.192 -0.195,-0.501 -0.008,-0.697c3.209,-3.354 7.623,-5.25 12.298,-5.25c4.675,0 9.088,1.896 12.297,5.25Z");

        wifiDisabled = new SVGPath();
        wifiDisabled.setContent("M10.198,5.94l-2.51,-2.537c1.544,-0.456 3.162,-0.693 4.812,-0.693c4.699,0 9.136,1.926 12.361,5.334c0.188,0.199 0.185,0.514 -0.008,0.708l-1.422,1.434c-0.2,0.202 -0.526,0.198 -0.72,-0.009c-2.657,-2.826 -6.325,-4.426 -10.211,-4.426c-0.778,0 -1.547,0.064 -2.302,0.189Zm-4.866,1.787c-1.111,0.664 -2.136,1.485 -3.043,2.45c-0.194,0.207 -0.52,0.211 -0.72,0.009l-1.422,-1.434c-0.193,-0.194 -0.196,-0.509 -0.008,-0.708c0.913,-0.964 1.922,-1.81 3.007,-2.527l2.186,2.21Zm13.47,6.911l-5.77,-5.833c2.893,0.139 5.596,1.407 7.567,3.546c0.184,0.2 0.179,0.511 -0.013,0.704l-1.422,1.435c-0.1,0.1 -0.231,0.15 -0.362,0.148Zm-8.959,-2.351c-1.245,0.438 -2.373,1.184 -3.28,2.186c-0.193,0.213 -0.524,0.221 -0.727,0.017l-1.422,-1.435c-0.192,-0.193 -0.197,-0.504 -0.013,-0.704c0.911,-0.988 1.978,-1.791 3.149,-2.382l2.293,2.318Zm2.56,2.588l3.185,3.22l-2.733,2.757c-0.196,0.197 -0.514,0.197 -0.71,0l-3.459,-3.489c-0.185,-0.186 -0.197,-0.483 -0.029,-0.685c0.932,-1.117 2.291,-1.774 3.746,-1.803l0,0Zm-11.103,-13.14c-0.393,-0.397 -0.393,-1.041 0,-1.437c0.392,-0.397 1.029,-0.397 1.421,0l18.428,18.629c0.393,0.396 0.393,1.04 0,1.437c-0.392,0.396 -1.029,0.396 -1.421,0l-18.428,-18.629Z");

        toggleButton = new IosRoundToggleButton();
        toggleButton.setGraphic(wifiDisabled);
        toggleButton.setInactive(true);

        switch1 = createSwitch(MacOSSystemColor.PINK.getColorAqua(), false);

        macosButton = new MacosButton("Test");
        macosButton.setDark(true);

        macosSwitch = MacosSwitchBuilder.create()
                                        .dark(true)
                                        .selectedColor(MacOSSystemColor.PINK.getColorDark())
                                        .selected(true)
                                        //.showOnOffText(true)
                                        //.selectedColor(MacOSSystemColor.GREEN.getColorAqua())
                                        .build();

        macosCheckBox = new MacosCheckBox("Check me");
        macosCheckBox.setDark(true);

        macosSlider = new MacosSlider();
        macosSlider.setDark(true);
        macosSlider.setBlockIncrement(10);
        macosSlider.setShowTickMarks(true);
        macosSlider.setSnapToTicks(true);

        macosTextField = new MacosTextField();
        macosTextField.setPromptText("prompt text");
        macosTextField.setDark(true);

        macosSeparator = new MacosSeparator(Orientation.HORIZONTAL);
        macosSeparator.setDark(true);

        VBox macosPane = new VBox(10, macosButton, macosSwitch, macosCheckBox, macosSlider, macosSeparator, macosTextField);

        macosScrollPane = new MacosScrollPane(macosPane);
        macosScrollPane.setDark(true);
        macosScrollPane.setFitToWidth(true);
        macosScrollPane.setFitToHeight(true);


        registerListeners();
    }

    private void registerListeners() {
        entry1.addOnActionPressed(e -> System.out.println("entry1 pressed"));
        balanceSlider.valueProperty().addListener(o -> System.out.println(balanceSlider.getBalanceValue()));
        plusMinusButton.addMacEvtObserver(MacEvt.ANY, e -> {
            EvtType<? extends Evt> type = e.getEvtType();
            if (MacEvt.INCREASE.equals(type)) {
                System.out.println("Increase");
            } else if (MacEvt.DECREASE.equals(type)) {
                System.out.println("Decrease");
            }
        });
        button.addMacEvtObserver(MacEvt.ANY, e -> {
            EvtType<? extends Evt> type = e.getEvtType();
            if (MacEvt.PRESSED.equals(type)) {
                System.out.println("Pressed");
            } else if (MacEvt.RELEASED.equals(type)) {
                System.out.println("Released");
            }
        });
        toggleButton.selectedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                toggleButton.setGraphic(wifiEnabled);
            } else {
                toggleButton.setGraphic(wifiDisabled);
            }
        });
    }

    @Override public void start(Stage stage) {
        VBox pane = new VBox(10, listView, slider, balanceSlider, buttonBar1, buttonBar2, plusMinusButton, button, toggleButton, switch1,
                             macosScrollPane);

        pane.setPadding(new Insets(20));
        pane.setAlignment(Pos.CENTER);
        pane.setBackground(new Background(new BackgroundFill(MacOSSystemColor.BACKGROUND.getColorDark(), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(pane);

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
