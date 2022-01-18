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
import eu.hansolo.applefx.fonts.Fonts;
import eu.hansolo.applefx.IosMultiButton.Type;
import eu.hansolo.applefx.tools.Helper;
import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.EvtType;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * User: hansolo
 * Date: 25.05.18
 * Time: 06:14
 */
@DefaultProperty("children")
public class IosEntry extends Region {
    private static final double                                  PREFERRED_WIDTH  = 375;
    private static final double                                  PREFERRED_HEIGHT = 44;
    private static final double                                  MINIMUM_WIDTH    = 100;
    private static final double                                  MINIMUM_HEIGHT   = 10;
    private static final double                                  MAXIMUM_WIDTH    = 2048;
    private static final double                                  MAXIMUM_HEIGHT   = 1024;
    private static final double                                  BUTTON_WIDTH     = 82;
    private final        MacEvt                                  deleteEntryEvt   = new MacEvt(IosEntry.this, MacEvt.DELETE_ENTRY);
    private final        MacEvt                                  pressedEvt       = new MacEvt(IosEntry.this, MacEvt.PRESSED);
    private final        MacEvt                                  releasedEvt      = new MacEvt(IosEntry.this, MacEvt.RELEASED);
    private              Map<EvtType, List<EvtObserver<MacEvt>>> observers;
    private              double                                  size;
    private              double                                  width;
    private              double                                  height;
    private              Label                                   titleLabel;
    private              Label                                   subtitleLabel;
    private              HBox                                    pane;
    private              Node                                    leftNode;
    private              String                                  _title;
    private              StringProperty                          title;
    private              String                                  _subtitle;
    private              StringProperty                          subtitle;
    private              VBox                                    textBox;
    private              Node                                    rightNode;
    private              Label                                   action;
    private              Label                                   delete;
    private              boolean                                 _hasDelete;
    private              BooleanProperty                         hasDelete;
    private              boolean                                 _hasAction;
    private              BooleanProperty                         hasAction;
    private              boolean                                 preDelete;
    private              boolean                                 hasForward;
    private              Timeline                                timeline;
    private              EventHandler<MouseEvent>                mouseHandler;
    private              double                                  draggedStartX;


    // ******************** Constructors **************************************
    public IosEntry() {
        this(null, "", "", null);
    }
    public IosEntry(final Node leftNode, final String title, final String subTitle, final Node rightNode) {
        getStylesheets().add(IosEntry.class.getResource("apple.css").toExternalForm());

        this.leftNode  = leftNode;
        _title         = title;
        _subtitle      = subTitle;
        this.rightNode = rightNode;
        _hasDelete     = true;
        _hasAction     = true;
        preDelete      = false;
        hasForward     = false;
        timeline       = new Timeline();

        if (null != this.rightNode) {
            if (this.rightNode instanceof IosMultiButton) {
                IosMultiButton mb = (IosMultiButton) this.rightNode;
                hasForward = Type.FORWARD == mb.getType();
                mb = null;
            }
        }

        mouseHandler   = e -> {
            final EventType<? extends MouseEvent> type = e.getEventType();
            double  translateX = getTranslateX();
            boolean hasAction  = getHasAction();
            boolean hasDelete  = getHasDelete();
            double  x          = e.getSceneX();

            if (type.equals(MouseEvent.MOUSE_PRESSED)) {
                if (hasForward) { fireMacEvt(pressedEvt); }
                draggedStartX = x;
            } else if (type.equals(MouseEvent.MOUSE_DRAGGED)) {
                double delta = (draggedStartX - x) * -1;

                if (hasDelete && !preDelete && delta < -pane.getPrefWidth() * 0.5) {
                    preDelete = true;
                    animateToDirectDelete();
                    return;
                } else if (preDelete && delta > -pane.getPrefWidth() * 0.75) {
                    preDelete = false;
                    animateBackFromDirectDelete();
                    return;
                } else if (Double.compare(translateX, 0) == 0 && delta > 0 ||
                    Double.compare(x, draggedStartX) == 0 ||
                    (hasAction && hasDelete && Double.compare(translateX, -2 * BUTTON_WIDTH) == 0 && delta < 0) ||
                    ((hasAction && !hasDelete) && Double.compare(translateX, -BUTTON_WIDTH) == 0 && delta < 0) ||
                    ((!hasAction && hasDelete) && Double.compare(translateX, -BUTTON_WIDTH) == 0 && delta < 0)) {
                    return;
                }

                if (hasAction && hasDelete) {
                    if (delta > 0) {
                        setTranslateX(-2 * BUTTON_WIDTH + Helper.clamp(0, 2 * BUTTON_WIDTH, delta));
                        action.setTranslateX(Helper.clamp(0, BUTTON_WIDTH, delta * 0.75));
                    } else {
                        setTranslateX(Helper.clamp(-2 * BUTTON_WIDTH, 0, delta));
                        action.setTranslateX(Helper.clamp(0, BUTTON_WIDTH, BUTTON_WIDTH + delta * 0.75));
                    }
                } else if (hasAction || hasDelete) {
                    if (preDelete) { return; }
                    if (delta > 0) {
                        setTranslateX(-BUTTON_WIDTH + Helper.clamp(0, BUTTON_WIDTH, delta));
                    } else {
                        setTranslateX(Helper.clamp(-BUTTON_WIDTH, 0, delta));
                    }
                }
            } else if (type.equals(MouseEvent.MOUSE_RELEASED)) {
                if (preDelete) {
                    fireMacEvt(deleteEntryEvt);
                    return;
                } else if (hasForward) {
                    fireMacEvt(releasedEvt);
                } else if (Double.compare(x, draggedStartX) == 0 ||
                    hasAction && hasDelete && Double.compare(translateX, -2 * BUTTON_WIDTH) == 0 ||
                    (hasAction || hasDelete) && Double.compare(translateX, -BUTTON_WIDTH) == 0) {
                    return;
                }
                animate();
            }
        };
        observers      = new ConcurrentHashMap<>();
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

        getStyleClass().add("ios-entry");
        
        titleLabel = new Label(getTitle());
        titleLabel.getStyleClass().add("title");
        titleLabel.setAlignment(Pos.CENTER_LEFT);
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        if (null == getTitle() || getTitle().isEmpty()) { Helper.enableNode(titleLabel, false); }

        subtitleLabel = new Label(getSubtitle());
        subtitleLabel.getStyleClass().add("subtitle");
        subtitleLabel.setAlignment(Pos.CENTER_LEFT);
        subtitleLabel.setMaxWidth(Double.MAX_VALUE);

        if (null == getSubtitle() || getSubtitle().isEmpty()) { Helper.enableNode(subtitleLabel, false); }

        textBox = new VBox(2, titleLabel, subtitleLabel);
        textBox.setAlignment(Pos.CENTER_LEFT);

        action = new Label("Action");
        action.getStyleClass().add("action");
        action.setFont(Fonts.sfProRegular(18));
        action.setManaged(false);
        action.setVisible(false);
        action.setPrefSize(BUTTON_WIDTH, PREFERRED_HEIGHT);

        delete = new Label("Delete");
        delete.getStyleClass().add("delete");
        delete.setFont(Fonts.sfProRegular(18));
        delete.setManaged(false);
        delete.setVisible(false);
        delete.setPrefSize(BUTTON_WIDTH, PREFERRED_HEIGHT);

        pane = new HBox(15);

        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        HBox.setHgrow(subtitleLabel, Priority.ALWAYS);
        HBox.setHgrow(textBox, Priority.ALWAYS);
        HBox.setHgrow(action, Priority.NEVER);
        HBox.setHgrow(delete, Priority.NEVER);
        HBox.setMargin(action, Insets.EMPTY);
        HBox.setMargin(delete, Insets.EMPTY);

        if (null != getLeftNode())  {
            pane.getChildren().add(getLeftNode());
            HBox.setMargin(getLeftNode(), new Insets(0, 0, 0, 15));
        }
        if (null != textBox) { pane.getChildren().add(textBox); }
        if (null != getRightNode()) { pane.getChildren().add(getRightNode()); }
        pane.getChildren().addAll(action, delete);
        pane.setAlignment(Pos.CENTER);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
        addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseHandler);
        addEventHandler(MouseEvent.MOUSE_RELEASED, mouseHandler);
        delete.setOnMousePressed(e -> fireMacEvt(deleteEntryEvt));
    }


    // ******************** Methods *******************************************
    @Override public void layoutChildren() {
        super.layoutChildren();
    }

    @Override protected double computeMinWidth(final double height) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double width) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double height) { return super.computePrefWidth(height); }
    @Override protected double computePrefHeight(final double width) { return super.computePrefHeight(width); }
    @Override protected double computeMaxWidth(final double height) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double width) { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public Node getLeftNode() { return leftNode; }
    public void setLeftNode(final Node node) { leftNode = node; }

    public String getTitle() { return null == title ? _title : title.get(); }
    public void setTitle(final String title) {
        if (null == this.title) {
            _title = title;
            titleLabel.setText(_title);
        } else {
            this.title.set(title);
        }
    }
    public StringProperty titleProperty() {
        if (null == title) {
            title = new StringPropertyBase(_title) {
                @Override protected void invalidated() { titleLabel.setText(get());}
                @Override public Object getBean() { return IosEntry.this; }
                @Override public String getName() { return "title"; }
            };
            _title = null;
        }
        return title;
    }

    public String getSubtitle() { return null == subtitle ? _subtitle : subtitle.get(); }
    public void setSubtitle(final String subTitle) {
        if (null == subtitle) {
            _subtitle = subTitle;
            subtitleLabel.setText(_subtitle);
        } else {
            subtitle.set(subTitle);
        }
    }
    public StringProperty subtitleProperty() {
        if (null == subtitle) {
            subtitle = new StringPropertyBase(_subtitle) {
                @Override protected void invalidated() { subtitleLabel.setText(get()); }
                @Override public Object getBean() { return IosEntry.this; }
                @Override public String getName() { return "subtitle"; }
            };
            _subtitle = null;
        }
        return subtitle;
    }

    public Node getRightNode()                { return rightNode; }
    public void setRightNode(final Node node) { rightNode = node; }

    public boolean getHasDelete() { return null == hasDelete ? _hasDelete : hasDelete.get(); }
    public void setHasDelete(final boolean hasDelete) {
        if (null == this.hasDelete) {
            _hasDelete = hasDelete;
            delete.setManaged(hasDelete);
            delete.setVisible(hasDelete);
            adjustMargins();
        } else {
            this.hasDelete.set(hasDelete);
        }
    }
    public BooleanProperty hasDeleteProperty() {
        if (null == hasDelete) {
            hasDelete = new BooleanPropertyBase(_hasDelete) {
                @Override protected void invalidated() {
                    delete.setManaged(get());
                    delete.setVisible(get());
                    adjustMargins();
                }
                @Override public Object getBean() { return IosEntry.this; }
                @Override public String getName() { return "hasDelete"; }
            };
        }
        return hasDelete;
    }

    public boolean getHasAction() { return null == hasAction ? _hasAction : hasAction.get(); }
    public void setHasAction(final boolean hasAction) {
        if (null == this.hasAction) {
            _hasAction = hasAction;
            action.setManaged(hasAction);
            action.setVisible(hasAction);
            adjustMargins();
        } else {
            this.hasAction.set(hasAction);
        }
    }
    public BooleanProperty hasActionProperty() {
        if (null == hasAction) {
            hasAction = new BooleanPropertyBase(_hasAction) {
                @Override protected void invalidated() {
                    action.setManaged(get());
                    action.setVisible(get());
                    adjustMargins();
                }
                @Override public Object getBean() { return IosEntry.this; }
                @Override public String getName() { return "hasAction"; }
            };
        }
        return hasAction;
    }

    public void setActionLabel(final String text) { action.setText(text); }

    public void addOnActionPressed(final EventHandler<MouseEvent> HANDLER) { action.addEventHandler(MouseEvent.MOUSE_PRESSED, HANDLER); }
    public void removeOnActionPressed(final EventHandler<MouseEvent> HANDLER) { action.removeEventHandler(MouseEvent.MOUSE_PRESSED, HANDLER);}

    private void adjustMargins() {
        action.setTranslateX(0);
        if (getHasAction() && getHasDelete()) {
            HBox.setMargin(action, new Insets(0, -15, 0, 0));
            action.setTranslateX(BUTTON_WIDTH);
        } else if (getHasAction() && !getHasDelete()) {
            HBox.setMargin(action, new Insets(0, 0, 0, 0));
        } else if (!getHasAction() && !getHasDelete() && getRightNode() != null) {
            HBox.setMargin(getRightNode(), new Insets(0, 15, 0, 0));
        } else if (null == getLeftNode()) {
            HBox.setMargin(textBox, new Insets(0, 0, 0, 15));
        }
    }

    private void animate() {
        double translateX = getTranslateX();
        if (Double.compare(translateX, 0) == 0) { return; }
        if (getHasAction() && getHasDelete()) {
            if (Double.compare(translateX, -2 * BUTTON_WIDTH) == 0) { return; }
            if (translateX < -BUTTON_WIDTH) {
                animateToShowButtons(true);
            } else {
                animateToHideButtons();
            }
        } else if (getHasAction() || getHasDelete()) {
            if (Double.compare(translateX, -BUTTON_WIDTH) == 0) { return; }
            if (translateX < -BUTTON_WIDTH * 0.5) {
                animateToShowButtons(false);
            } else {
                animateToHideButtons();
            }
        }
    }

    private void animateToShowButtons(final boolean twoButtons) {
        KeyValue kvTranslateXStart       = new KeyValue(translateXProperty(), getTranslateX(), Interpolator.EASE_BOTH);
        KeyValue kvTranslateXEnd         = new KeyValue(translateXProperty(), twoButtons ? (-2 * BUTTON_WIDTH) : -BUTTON_WIDTH, Interpolator.EASE_BOTH);
        KeyValue kvActionTranslateXStart = new KeyValue(action.translateXProperty(), action.getTranslateX(), Interpolator.EASE_BOTH);
        KeyValue kvActionTranslateXEnd   = new KeyValue(action.translateXProperty(), 0, Interpolator.EASE_BOTH);

        KeyFrame kf0 = twoButtons ? new KeyFrame(Duration.ZERO, kvTranslateXStart, kvActionTranslateXStart) : new KeyFrame(Duration.ZERO, kvTranslateXStart);
        KeyFrame kf1 = twoButtons ? new KeyFrame(Duration.millis(Helper.ANIMATION_DURATION), kvTranslateXEnd, kvActionTranslateXEnd) : new KeyFrame(Duration.millis(Helper.ANIMATION_DURATION), kvTranslateXEnd);

        timeline.getKeyFrames().setAll(kf0, kf1);
        timeline.play();
    }
    private void animateToHideButtons() {
        KeyValue kvTranslateXStart       = new KeyValue(translateXProperty(), getTranslateX(), Interpolator.EASE_BOTH);
        KeyValue kvTranslateXEnd         = new KeyValue(translateXProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kvActionTranslateXStart = new KeyValue(action.translateXProperty(), action.getTranslateX(), Interpolator.EASE_BOTH);
        KeyValue kvActionTranslateXEnd   = new KeyValue(action.translateXProperty(), BUTTON_WIDTH, Interpolator.EASE_BOTH);

        KeyFrame kf0 = new KeyFrame(Duration.ZERO, kvTranslateXStart, kvActionTranslateXStart);
        KeyFrame kf1 = new KeyFrame(Duration.millis(Helper.ANIMATION_DURATION), kvTranslateXEnd, kvActionTranslateXEnd);

        timeline.getKeyFrames().setAll(kf0, kf1);
        timeline.play();
    }

    private void animateToDirectDelete() {
        pane.setMaxWidth(PREFERRED_WIDTH + BUTTON_WIDTH + BUTTON_WIDTH);
        pane.setPrefWidth(PREFERRED_WIDTH + BUTTON_WIDTH + BUTTON_WIDTH);
        setTranslateX(-BUTTON_WIDTH - BUTTON_WIDTH);

        double targetWidth = pane.getPrefWidth();

        KeyValue kvDeleteWidthStart = new KeyValue(delete.prefWidthProperty(), delete.getPrefWidth(), Interpolator.EASE_BOTH);
        KeyValue kvDeleteWidthEnd   = new KeyValue(delete.prefWidthProperty(), targetWidth, Interpolator.EASE_BOTH);

        KeyFrame kf0 = new KeyFrame(Duration.ZERO, kvDeleteWidthStart);
        KeyFrame kf1 = new KeyFrame(Duration.millis(Helper.ANIMATION_DURATION), kvDeleteWidthEnd);
        timeline.getKeyFrames().setAll(kf0, kf1);

        timeline.play();
    }
    private void animateBackFromDirectDelete() {
        final int NO_OF_BUTTONS = (getHasAction() ? 1 : 0) + (getHasDelete() ? 1 : 0);

    }


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
        size   = width < height ? width : height;

        if (width > 0 && height > 0) {
            if (getHasAction() && getHasDelete()) {
                pane.setPrefSize(width + BUTTON_WIDTH + BUTTON_WIDTH, PREFERRED_HEIGHT);
                pane.setMaxSize(width + BUTTON_WIDTH + BUTTON_WIDTH, PREFERRED_HEIGHT);
            } else if (getHasAction() || getHasDelete()) {
                pane.setPrefSize(width + BUTTON_WIDTH, PREFERRED_HEIGHT);
                pane.setMaxSize(width + BUTTON_WIDTH, PREFERRED_HEIGHT);
            } else {
                pane.setPrefSize(width, PREFERRED_HEIGHT);
                pane.setMaxSize(width, PREFERRED_HEIGHT);
            }

            pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);
        }
    }
}
