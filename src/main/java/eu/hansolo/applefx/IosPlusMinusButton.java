/*
 * Copyright (c) 2022 by Gerrit Grunwald
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

import eu.hansolo.applefx.event.MacEvt;
import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.EvtType;
import javafx.beans.DefaultProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * User: hansolo
 * Date: 06.06.18
 * Time: 13:55
 */
@DefaultProperty("children")
public class IosPlusMinusButton extends Region {
    private static final double                                  PREFERRED_WIDTH  = 250;
    private static final double                                  PREFERRED_HEIGHT = 50;
    private static final double                                  MINIMUM_WIDTH    = 50;
    private static final double                                  MINIMUM_HEIGHT   = 50;
    private static final double                                  MAXIMUM_WIDTH    = 1024;
    private static final double                                  MAXIMUM_HEIGHT   = 1024;
    private static       double                                  aspectRatio;
    private final        MacEvt                                  increaseEvt      = new MacEvt(IosPlusMinusButton.this, MacEvt.INCREASE);
    private final        MacEvt                                  decreaseEvt      = new MacEvt(IosPlusMinusButton.this, MacEvt.DECREASE);
    private              Map<EvtType, List<EvtObserver<MacEvt>>> observers;
    private              double                                  width;
    private              double                                  height;
    private              Pane                                    pane;
    private              Button                                  minus;
    private              Button                                  plus;
    private              IosSegmentedButtonBar                   buttonBar;
    private              EventHandler<MouseEvent>                mouseHandler;


    // ******************** Constructors **************************************
    public IosPlusMinusButton() {
        getStylesheets().add(IosPlusMinusButton.class.getResource("apple.css").toExternalForm());
        aspectRatio  = PREFERRED_HEIGHT / PREFERRED_WIDTH;
        observers    = new ConcurrentHashMap<>();
        mouseHandler = e -> {
            final EventType<? extends MouseEvent> TYPE = e.getEventType();
            final Object                          SRC  = e.getSource();
            if (MouseEvent.MOUSE_PRESSED.equals(TYPE)) {
                if (SRC.equals(minus)) {
                    fireMacEvt(decreaseEvt);
                } else if (SRC.equals(plus)) {
                    fireMacEvt(increaseEvt);
                }
            }
        };

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
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        getStyleClass().add("ios-plus-minus-button");

        Rectangle minusSign = new Rectangle(15.5, 1.5);
        minusSign.getStyleClass().setAll("minus-sign");

        minus = new Button();
        minus.getStyleClass().add("minus-button");
        minus.setGraphic(minusSign);
        minus.setPadding(new Insets(6, 15, 6, 15));

        Rectangle v        = new Rectangle(7, 0, 1.5, 15.5);
        Rectangle h        = new Rectangle(0, 7, 15.5, 1.5);
        Shape     plusSign = Shape.union(v, h);
        plusSign.getStyleClass().setAll("plus-sign");

        plus  = new Button();
        plus.getStyleClass().add("plus-button");
        plus.setGraphic(plusSign);
        plus.setPadding(new Insets(6, 15, 6, 15));

        buttonBar = new IosSegmentedButtonBar(minus, plus);

        pane = new Pane(buttonBar);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        minus.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
        plus.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
    }

    public void dispose() {
        minus.removeEventHandler(MouseEvent.MOUSE_CLICKED, mouseHandler);
        plus.removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double height) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double width) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double height) { return super.computePrefWidth(height); }
    @Override protected double computePrefHeight(final double width) { return super.computePrefHeight(width); }
    @Override protected double computeMaxWidth(final double height) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double width) { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }


    // ******************** Event handling ************************************
    public void addMacEvtObserver(final EvtType type, final EvtObserver<MacEvt> observer) {
        if (!observers.containsKey(type)) { observers.put(type, new CopyOnWriteArrayList<>()); }
        if (observers.get(type).contains(observer)) { return; }
        observers.get(type).add(observer);
    }
    public void removeMacEvtObserver(final EvtType type, final EvtObserver<MacEvt> observer) {
        if (observers.containsKey(type)) {
            if (observers.get(type).contains(observer)) {
                observers.get(type).remove(observer);
            }
        }
    }
    public void removeAllMacEvtObservers() { observers.clear(); }

    public void fireMacEvt(final MacEvt evt) {
        final EvtType type = evt.getEvtType();
        observers.entrySet().stream().filter(entry -> entry.getKey().equals(MacEvt.ANY)).forEach(entry -> entry.getValue().forEach(observer -> observer.handle(evt)));
        if (observers.containsKey(type) && !type.equals(MacEvt.ANY)) {
            observers.get(type).forEach(observer -> observer.handle(evt));
        }
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();

        if (aspectRatio * width > height) {
            width = 1 / (aspectRatio / height);
        } else if (1 / (aspectRatio / height) > width) {
            height = aspectRatio * width;
        }

        if (width > 0 && height > 0) {
            double w = buttonBar.getLayoutBounds().getWidth();
            double h = buttonBar.getLayoutBounds().getHeight();
            pane.setMaxSize(w, h);
            pane.setPrefSize(w, h);
            pane.relocate(getInsets().getLeft(), getInsets().getTop());
        }
    }
}
