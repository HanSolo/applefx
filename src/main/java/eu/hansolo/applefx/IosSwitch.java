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
import eu.hansolo.applefx.tools.Helper;
import eu.hansolo.applefx.tools.MacosSystemColor;
import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.EvtType;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.DefaultProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * User: hansolo
 * Date: 14.05.18
 * Time: 11:15
 */
@DefaultProperty("children")
public class IosSwitch extends Region {
    public static final  double                                  MIN_DURATION           = 10;
    public static final  double                                  MAX_DURATION           = 500;
    public static final  Color                                   DEFAULT_SELECTED_COLOR = MacosSystemColor.GREEN.getColorAqua();
    private static final double                                  PREFERRED_WIDTH        = 38;
    private static final double                                  PREFERRED_HEIGHT       = 23;
    private static final double                                  MINIMUM_WIDTH          = 20;
    private static final double                                  MINIMUM_HEIGHT         = 12;
    private static final double                                  MAXIMUM_WIDTH          = 1024;
    private static final double                                  MAXIMUM_HEIGHT         = 1024;
    private static final double                                  ASPECT_RATIO           = PREFERRED_HEIGHT / PREFERRED_WIDTH;
    private static final long                                    LONG_PRESS_TIME        = 200_000_000l;
    private static final StyleablePropertyFactory<IosSwitch>     FACTORY                = new StyleablePropertyFactory<>(Region.getClassCssMetaData());
    private static final PseudoClass                             DARK_PSEUDO_CLASS      = PseudoClass.getPseudoClass("dark");
    private final        MacEvt                                  selectedEvt            = new MacEvt(IosSwitch.this, MacEvt.SELECTED);
    private final        MacEvt                                  deselectedEvt          = new MacEvt(IosSwitch.this, MacEvt.DESELECTED);
    private final        StyleableProperty<Color>                selectedColor;
    private              Map<EvtType, List<EvtObserver<MacEvt>>> observers;
    private              boolean                                 _dark;
    private              BooleanProperty                         dark;
    private              double                                  width;
    private              double                                  height;
    private              DropShadow                              dropShadow;
    private              Rectangle                               backgroundArea;
    private              Rectangle                               mainArea;
    private              Rectangle                               knob;
    private              Circle                                  zero;
    private              Rectangle                               one;
    private              Pane                                    pane;
    private              long                                    pressStart;
    private              AnimationTimer                          holdTimer;
    private              boolean                                 _selected;
    private              BooleanProperty                         selected;
    private              double                                  _duration;
    private              DoubleProperty                          duration;
    private              boolean                                 _showOnOffText;
    private              BooleanProperty                         showOnOffText;
    private              Timeline                                timeline;
    private              BooleanBinding                          showing;
    private              ChangeListener<Boolean>                 showingListener;
    private              HashMap<String, Property>               settings;
    private              EventHandler<MouseEvent>                clickedHandler;
    private              EventHandler<MouseEvent>                pressedHandler;



    // ******************** Constructors **************************************
    public IosSwitch() {
        this(new HashMap<>());
    }
    public IosSwitch(final Map<String, Property> settings) {
        pressStart      = System.nanoTime();
        holdTimer       = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now - pressStart > LONG_PRESS_TIME) {
                    holdTimer.stop();
                    if (isSelected()) {
                        animateToPreDeselect();
                    } else {
                        animateToPreSelect();
                    }
                }
            }
        };
        _selected       = false;
        selectedColor   = FACTORY.createStyleableColorProperty(IosSwitch.this, "selectedColor", "-selected-color", s -> s.selectedColor, DEFAULT_SELECTED_COLOR);
        _dark           = false;
        _duration       = 250;
        _showOnOffText  = false;
        showingListener =  (o, ov, nv) -> { if (nv) { applySettings(); } };
        this.settings   = new HashMap<>(settings);
        timeline        = new Timeline();
        observers       = new ConcurrentHashMap<>();
        clickedHandler  = e -> setSelected(!isSelected());
        pressedHandler  = e -> {
            pressStart = System.nanoTime();
            holdTimer.start();
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

        getStyleClass().add("ios-switch");

        dropShadow = new DropShadow(BlurType.GAUSSIAN, Color.rgb(0, 0, 0, 0.25), 10.0, 0.0, 0, 5);

        backgroundArea = new Rectangle();
        backgroundArea.getStyleClass().addAll("background-area");
        if (isSelected()) {
            backgroundArea.setFill(getSelectedColor());
        }

        one = new Rectangle();
        one.getStyleClass().addAll("one");
        one.setMouseTransparent(true);
        one.setVisible(false);

        mainArea = new Rectangle();
        mainArea.getStyleClass().addAll("main-area");
        mainArea.setMouseTransparent(true);
        if (isSelected()) {
            mainArea.setOpacity(0);
            mainArea.setScaleX(0);
            mainArea.setScaleY(0);
        }

        zero = new Circle();
        zero.getStyleClass().addAll("zero");
        zero.setMouseTransparent(true);
        zero.setVisible(false);

        knob = new Rectangle();
        knob.getStyleClass().addAll("knob");
        knob.setEffect(dropShadow);
        knob.setMouseTransparent(true);

        pane = new Pane(backgroundArea, one, mainArea, zero, knob);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        disabledProperty().addListener(o -> setOpacity(isDisabled() ? 0.5 : 1.0));
        backgroundArea.addEventHandler(MouseEvent.MOUSE_CLICKED, clickedHandler);
        backgroundArea.addEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
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
        showing = Bindings.selectBoolean(sceneProperty(), "window", "showing");
        showing.addListener(showingListener);
    }

    private void applySettings() {
        if (settings.isEmpty()) { return; }
        for (String key : settings.keySet()) {
            if ("prefSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) settings.get(key)).get();
                setPrefSize(dim.getWidth(), dim.getHeight());
            } else if ("minSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) settings.get(key)).get();
                setMinSize(dim.getWidth(), dim.getHeight());
            } else if ("maxSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) settings.get(key)).get();
                setMaxSize(dim.getWidth(), dim.getHeight());
            } else if ("prefWidth".equals(key)) {
                setPrefWidth(((DoubleProperty) settings.get(key)).get());
            } else if ("prefHeight".equals(key)) {
                setPrefHeight(((DoubleProperty) settings.get(key)).get());
            } else if ("minWidth".equals(key)) {
                setMinWidth(((DoubleProperty) settings.get(key)).get());
            } else if ("minHeight".equals(key)) {
                setMinHeight(((DoubleProperty) settings.get(key)).get());
            } else if ("maxWidth".equals(key)) {
                setMaxWidth(((DoubleProperty) settings.get(key)).get());
            } else if ("maxHeight".equals(key)) {
                setMaxHeight(((DoubleProperty) settings.get(key)).get());
            } else if ("scaleX".equals(key)) {
                setScaleX(((DoubleProperty) settings.get(key)).get());
            } else if ("scaleY".equals(key)) {
                setScaleY(((DoubleProperty) settings.get(key)).get());
            } else if ("layoutX".equals(key)) {
                setLayoutX(((DoubleProperty) settings.get(key)).get());
            } else if ("layoutY".equals(key)) {
                setLayoutY(((DoubleProperty) settings.get(key)).get());
            } else if ("translateX".equals(key)) {
                setTranslateX(((DoubleProperty) settings.get(key)).get());
            } else if ("translateY".equals(key)) {
                setTranslateY(((DoubleProperty) settings.get(key)).get());
            } else if ("padding".equals(key)) {
                setPadding(((ObjectProperty<Insets>) settings.get(key)).get());
            } // Control specific settings
            else if ("selectedColor".equals(key)) {
                setSelectedColor(((ObjectProperty<Color>) settings.get(key)).get());
                System.out.println(getSelectedColor());
            } else if("dark".equals(key)) {
                setDark(((BooleanProperty) settings.get(key)).get());
            } else if ("showOnOffText".equals(key)) {
                setShowOnOffText(((BooleanProperty) settings.get(key)).get());
            } else if ("duration".equals(key)) {
                setDuration(((DoubleProperty) settings.get(key)).get());
            }
        }

        if (settings.containsKey("selected")) { setSelected(((BooleanProperty) settings.get("selected")).get()); }

        settings.clear();
        if (null == showing) { return; }
        showing.removeListener(showingListener);
    }

    public void dispose() {
        backgroundArea.removeEventHandler(MouseEvent.MOUSE_CLICKED, clickedHandler);
        backgroundArea.removeEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
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

    public boolean isSelected() { return null == selected ? _selected : selected.get(); }
    public void setSelected(final boolean selected) {
        holdTimer.stop();
        if (null == this.selected) {
            _selected = selected;
            if (_selected) {
                fireMacEvt(selected ? selectedEvt : deselectedEvt);
                animateToSelect();
            } else {
                animateToDeselect();
            }
        } else {
            this.selected.set(selected);
        }
    }
    public BooleanProperty selectedProperty() {
        if (null == selected) {
            selected = new BooleanPropertyBase(_selected) {
                @Override protected void invalidated() {
                    fireMacEvt(get() ? selectedEvt : deselectedEvt);
                    if (get()) {
                        animateToSelect();
                    } else {
                        animateToDeselect();
                    }
                }
                @Override public Object getBean() { return IosSwitch.this; }
                @Override public String getName() { return "selected"; }
            };
        }
        return selected;
    }

    public Color getSelectedColor() { return selectedColor.getValue(); }
    public void setSelectedColor(final Color color) { selectedColor.setValue(color); }
    public ObjectProperty<Color> selectedColorProperty() { return (ObjectProperty<Color>) selectedColor; }

    public final boolean isDark() {
        return null == dark ? _dark : dark.get();
    }
    public final void setDark(final boolean dark) {
        if (null == this.dark) {
            _dark = dark;
            pseudoClassStateChanged(DARK_PSEUDO_CLASS, dark);
        } else {
            darkProperty().set(dark);
        }
    }
    public final BooleanProperty darkProperty() {
        if (null == dark) {
            dark = new BooleanPropertyBase() {
                @Override protected void invalidated() {
                    pseudoClassStateChanged(DARK_PSEUDO_CLASS, get());
                }
                @Override public Object getBean() { return IosSwitch.this; }
                @Override public String getName() { return "dark"; }
            };
        }
        return dark;
    }

    public double getDuration() { return null == duration ? _duration : duration.get(); }
    public void setDuration(final double duration) {
        if (null == this.duration) {
            _duration = Helper.clamp(MIN_DURATION, MAX_DURATION, duration);
        } else {
            this.duration.set(duration);
        }
    }
    public DoubleProperty durationProperty() {
        if (null == duration) {
            duration = new DoublePropertyBase(_duration) {
                @Override protected void invalidated() { set(Helper.clamp(MIN_DURATION, MAX_DURATION, get())); }
                @Override public Object getBean() { return IosSwitch.this; }
                @Override public String getName() { return "duration"; }
            };
        }
        return duration;
    }

    public boolean getShowOnOffText() { return null == showOnOffText ? _showOnOffText : showOnOffText.get(); }
    public void setShowOnOffText(final boolean show) {
        if (null == showOnOffText) {
            _showOnOffText = show;
            one.setVisible(show);
            zero.setVisible(show);
        } else {
            showOnOffText.set(show);
        }
    }
    public BooleanProperty showOnOffTextProperty() {
        if (null == showOnOffText) {
            showOnOffText = new BooleanPropertyBase(_showOnOffText) {
                @Override protected void invalidated() {
                    one.setVisible(get());
                    zero.setVisible(get());
                }
                @Override public Object getBean() { return IosSwitch.this; }
                @Override public String getName() { return "showOnOffText"; }
            };
        }
        return showOnOffText;
    }

    protected HashMap<String, Property> getSettings() { return settings; }

    private void animateToPreSelect() {
        KeyValue kvKnobWidthStart   = new KeyValue(knob.widthProperty(), height * 0.89130435, Interpolator.EASE_BOTH);
        KeyValue kvKnobWidthEnd     = new KeyValue(knob.widthProperty(), height * 0.89130435 * 1.2, Interpolator.EASE_BOTH);
        KeyValue kvMainScaleXStart  = new KeyValue(mainArea.scaleXProperty(), 1, Interpolator.EASE_BOTH);
        KeyValue kvMainScaleXEnd    = new KeyValue(mainArea.scaleXProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kvMainScaleYStart  = new KeyValue(mainArea.scaleYProperty(), 1, Interpolator.EASE_BOTH);
        KeyValue kvMainScaleYEnd    = new KeyValue(mainArea.scaleYProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kvMainOpacityStart = new KeyValue(mainArea.opacityProperty(), 1, Interpolator.EASE_BOTH);
        KeyValue kvMainOpacityEnd   = new KeyValue(mainArea.opacityProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kvZeroOpacityStart = new KeyValue(zero.opacityProperty(), 1, Interpolator.EASE_BOTH);
        KeyValue kvZeroOpacityEnd   = new KeyValue(zero.opacityProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kvOneOpacityStart  = new KeyValue(one.opacityProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kvOneOpacityEnd    = new KeyValue(one.opacityProperty(), 1, Interpolator.EASE_BOTH);

        KeyFrame kf0;
        KeyFrame kf1;

        if (isDark()) {
            kf0 = new KeyFrame(Duration.ZERO, kvKnobWidthStart, kvZeroOpacityStart, kvOneOpacityStart);
            kf1 = new KeyFrame(Duration.millis(125), kvKnobWidthEnd, kvZeroOpacityEnd, kvOneOpacityEnd);
        } else {
            kf0 = new KeyFrame(Duration.ZERO, kvKnobWidthStart, kvMainScaleXStart, kvMainScaleYStart, kvMainOpacityStart, kvZeroOpacityStart, kvOneOpacityStart);
            kf1 = new KeyFrame(Duration.millis(125), kvKnobWidthEnd, kvMainScaleXEnd, kvMainScaleYEnd, kvMainOpacityEnd, kvZeroOpacityEnd, kvOneOpacityEnd);
        }

        timeline.getKeyFrames().setAll(kf0, kf1);
        timeline.play();
    }
    private void animateToPreDeselect() {
        KeyValue kvKnobWidthStart   = new KeyValue(knob.widthProperty(), height * 0.89130435, Interpolator.EASE_BOTH);
        KeyValue kvKnobWidthEnd     = new KeyValue(knob.widthProperty(), height * 0.89130435 * 1.2, Interpolator.EASE_BOTH);
        KeyValue kvKnobXStart       = new KeyValue(knob.xProperty(), mainArea.getLayoutBounds().getMaxX() - height * 0.89130435, Interpolator.EASE_BOTH);
        KeyValue kvKnobXEnd         = new KeyValue(knob.xProperty(), mainArea.getLayoutBounds().getMaxX() - height * 0.89130435 * 1.2, Interpolator.EASE_BOTH);
        KeyValue kvZeroOpacityStart = new KeyValue(zero.opacityProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kvZeroOpacityEnd   = new KeyValue(zero.opacityProperty(), 1, Interpolator.EASE_BOTH);
        KeyValue kvOneOpacityStart  = new KeyValue(one.opacityProperty(), 1, Interpolator.EASE_BOTH);
        KeyValue kvOneOpacityEnd    = new KeyValue(one.opacityProperty(), 0, Interpolator.EASE_BOTH);

        KeyFrame kf0 = new KeyFrame(Duration.ZERO, kvKnobWidthStart, kvKnobXStart, kvZeroOpacityStart, kvOneOpacityStart);
        KeyFrame kf1 = new KeyFrame(Duration.millis(Helper.ANIMATION_DURATION), kvKnobWidthEnd, kvKnobXEnd, kvZeroOpacityEnd, kvOneOpacityEnd);

        timeline.getKeyFrames().setAll(kf0, kf1);
        timeline.play();
    }

    private void animateToSelect() {
        KeyValue kvMainScaleXStart     = new KeyValue(mainArea.scaleXProperty(), mainArea.getScaleX(), Interpolator.EASE_BOTH);
        KeyValue kvMainScaleXEnd       = new KeyValue(mainArea.scaleXProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kvMainScaleYStart     = new KeyValue(mainArea.scaleYProperty(), mainArea.getScaleY(), Interpolator.EASE_BOTH);
        KeyValue kvMainScaleYEnd       = new KeyValue(mainArea.scaleYProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kvMainOpacityStart    = new KeyValue(mainArea.opacityProperty(), mainArea.getOpacity(), Interpolator.EASE_BOTH);
        KeyValue kvMainOpacityEnd      = new KeyValue(mainArea.opacityProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kvBackgroundFillStart = new KeyValue(backgroundArea.fillProperty(), Color.rgb(229, 229, 229), Interpolator.EASE_BOTH);
        KeyValue kvBackgroundFillEnd   = new KeyValue(backgroundArea.fillProperty(), getSelectedColor(), Interpolator.EASE_BOTH);
        KeyValue kvKnobXStart          = new KeyValue(knob.xProperty(), mainArea.getLayoutBounds().getMinX(), Interpolator.EASE_BOTH);
        KeyValue kvKnobXEnd            = new KeyValue(knob.xProperty(), mainArea.getLayoutBounds().getMaxX() - height * 0.89130435, Interpolator.EASE_BOTH);
        KeyValue kvOneOpacityStart     = new KeyValue(one.opacityProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kvOneOpacityEnd       = new KeyValue(one.opacityProperty(), 1, Interpolator.EASE_BOTH);
        KeyValue kvZeroOpacityStart    = new KeyValue(zero.opacityProperty(), zero.getOpacity(), Interpolator.EASE_BOTH);
        KeyValue kvZeroOpacityEnd      = new KeyValue(zero.opacityProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kvKnobWidthStart      = new KeyValue(knob.widthProperty(), knob.getWidth(), Interpolator.EASE_BOTH);
        KeyValue kvKnobWidthEnd        = new KeyValue(knob.widthProperty(), height * 0.89130435, Interpolator.EASE_BOTH);

        KeyFrame kf0 = new KeyFrame(Duration.ZERO, kvMainScaleXStart, kvMainScaleYStart, kvMainOpacityStart, kvBackgroundFillStart, kvKnobXStart, kvOneOpacityStart, kvZeroOpacityStart, kvKnobWidthStart);
        KeyFrame kf1 = new KeyFrame(Duration.millis(getDuration() * 0.5), kvZeroOpacityEnd);
        KeyFrame kf2 = new KeyFrame(Duration.millis(getDuration()), kvMainScaleXEnd, kvMainScaleYEnd, kvMainOpacityEnd, kvBackgroundFillEnd, kvKnobXEnd, kvOneOpacityEnd, kvKnobWidthEnd);

        timeline.getKeyFrames().setAll(kf0, kf1, kf2);
        timeline.play();
    }
    private void animateToDeselect() {
        KeyValue kvMainScaleXStart     = new KeyValue(mainArea.scaleXProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kvMainScaleXEnd       = new KeyValue(mainArea.scaleXProperty(), 1, Interpolator.EASE_BOTH);
        KeyValue kvMainScaleYStart     = new KeyValue(mainArea.scaleYProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kvMainScaleYEnd       = new KeyValue(mainArea.scaleYProperty(), 1, Interpolator.EASE_BOTH);
        KeyValue kvMainOpacityStart    = new KeyValue(mainArea.opacityProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kvMainOpacityEnd      = new KeyValue(mainArea.opacityProperty(), 1, Interpolator.EASE_BOTH);
        KeyValue kvBackgroundFillStart = new KeyValue(backgroundArea.fillProperty(), getSelectedColor(), Interpolator.EASE_BOTH);
        KeyValue kvBackgroundFillEnd   = new KeyValue(backgroundArea.fillProperty(), Color.rgb(229, 229, 229), Interpolator.EASE_BOTH);
        KeyValue kvKnobXStart          = new KeyValue(knob.xProperty(), mainArea.getLayoutBounds().getMaxX() - knob.getWidth(), Interpolator.EASE_BOTH);
        KeyValue kvKnobXEnd            = new KeyValue(knob.xProperty(), mainArea.getLayoutBounds().getMinX(), Interpolator.EASE_BOTH);
        KeyValue kvOneOpacityStart     = new KeyValue(one.opacityProperty(), one.getOpacity(), Interpolator.EASE_BOTH);
        KeyValue kvOneOpacityEnd       = new KeyValue(one.opacityProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kvZeroOpacityStart    = new KeyValue(zero.opacityProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kvZeroOpacityEnd      = new KeyValue(zero.opacityProperty(), 1, Interpolator.EASE_BOTH);
        KeyValue kvKnobWidthStart      = new KeyValue(knob.widthProperty(), knob.getWidth(), Interpolator.EASE_BOTH);
        KeyValue kvKnobWidthEnd        = new KeyValue(knob.widthProperty(), height * 0.89130435, Interpolator.EASE_BOTH);

        KeyFrame kf0 = new KeyFrame(Duration.ZERO, kvMainScaleXStart, kvMainScaleYStart, kvMainOpacityStart, kvBackgroundFillStart, kvKnobXStart, kvOneOpacityStart, kvZeroOpacityStart, kvKnobWidthStart);
        KeyFrame kf1 = new KeyFrame(Duration.millis(getDuration() * 0.5), kvOneOpacityEnd);
        KeyFrame kf2 = new KeyFrame(Duration.millis(getDuration()), kvMainScaleXEnd, kvMainScaleYEnd, kvMainOpacityEnd, kvBackgroundFillEnd, kvKnobXEnd, kvZeroOpacityEnd, kvKnobWidthEnd);

        timeline.getKeyFrames().setAll(kf0, kf1, kf2);
        timeline.play();
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

        if (width > 0 && height > 0) {
            if (ASPECT_RATIO * width > height) {
                width = 1 / (ASPECT_RATIO / height);
            } else if (1 / (ASPECT_RATIO / height) > width) {
                height = ASPECT_RATIO * width;
            }

            dropShadow.setRadius(height * 0.14);
            dropShadow.setOffsetY(height * 0.065);

            backgroundArea.setWidth(width);
            backgroundArea.setHeight(height);
            backgroundArea.setArcWidth(height);
            backgroundArea.setArcHeight(height);

            one.setWidth(height * 0.0326087);
            one.setHeight(height * 0.32608696);
            one.setX(width * 0.225 - (one.getWidth() * 0.5));
            one.setY((height - one.getHeight()) * 0.5);

            mainArea.setWidth(width * 0.93421053);
            mainArea.setHeight(height * 0.89130435);
            mainArea.setArcWidth(height * 0.89130435);
            mainArea.setArcHeight(height * 0.89130435);
            mainArea.setX(height * 0.05434783);
            mainArea.setY(height * 0.05434783);

            zero.setRadius(height * 0.1413);
            zero.setCenterX(width * 0.765);
            zero.setCenterY(height * 0.5);
            zero.setStrokeWidth(height * 0.04);

            knob.setWidth(height * 0.89130435);
            knob.setHeight(height * 0.89130435);
            knob.setArcWidth(height * 0.89130435);
            knob.setArcHeight(height * 0.89130435);
            if (isSelected()) {
                knob.setX(mainArea.getLayoutBounds().getMaxX() - knob.getWidth());
            } else {
                knob.setX(mainArea.getLayoutBounds().getMinX());
            }
            knob.setY((backgroundArea.getLayoutBounds().getHeight() - knob.getLayoutBounds().getHeight()) * 0.5);

            pane.setMaxSize(width, height);
            pane.setPrefSize(width, height);
            pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);
        }
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() { return IosSwitch.class.getResource("apple.css").toExternalForm(); }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() { return FACTORY.getCssMetaData(); }
    @Override public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() { return FACTORY.getCssMetaData(); }
}
