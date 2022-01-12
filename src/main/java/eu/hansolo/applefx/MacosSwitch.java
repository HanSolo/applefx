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
import eu.hansolo.applefx.tools.Helper.MacOSSystemColor;
import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.EvtType;
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


@DefaultProperty("children")
public class MacosSwitch extends Region {
    public static final  double                                  MIN_DURATION           = 10;
    public static final  double                                  MAX_DURATION           = 500;
    public static final  Color                                   DEFAULT_SELECTED_COLOR = MacOSSystemColor.BLUE.getColorAqua();
    private static final double                                  PREFERRED_WIDTH        = 38;
    private static final double                                  PREFERRED_HEIGHT       = 25;
    private static final double                                  MINIMUM_WIDTH          = 38;
    private static final double                                  MINIMUM_HEIGHT         = 25;
    private static final double                                  MAXIMUM_WIDTH          = 38;
    private static final double                                  MAXIMUM_HEIGHT         = 25;
    private static final double                                  ASPECT_RATIO           = PREFERRED_HEIGHT / PREFERRED_WIDTH;
    private static final StyleablePropertyFactory<MacosSwitch>   FACTORY                = new StyleablePropertyFactory<>(Region.getClassCssMetaData());
    private static final PseudoClass                             DARK_PSEUDO_CLASS      = PseudoClass.getPseudoClass("dark");
    private final        MacEvt                                  selectedEvt            = new MacEvt(MacosSwitch.this, MacEvt.SELECTED);
    private final        MacEvt                                  deselectedEvt          = new MacEvt(MacosSwitch.this, MacEvt.DESELECTED);
    private final        StyleableProperty<Color>                selectedColor;
    private              Map<EvtType, List<EvtObserver<MacEvt>>> observers;
    private              boolean                                 _dark;
    private              BooleanProperty                         dark;
    private              double                                  width;
    private              double                                  height;
    private              Rectangle                               backgroundArea;
    private              Rectangle                               knob;
    private              Circle                                  zero;
    private              Rectangle                               one;
    private              Pane                                    pane;
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



    // ******************** Constructors **************************************
    public MacosSwitch() {
        this(new HashMap<>());
    }
    public MacosSwitch(final Map<String, Property> settings) {
        _selected       = false;
        selectedColor   = FACTORY.createStyleableColorProperty(MacosSwitch.this, "selectedColor", "-selected-color", s -> s.selectedColor, DEFAULT_SELECTED_COLOR);
        _dark           = false;
        _duration       = 250;
        _showOnOffText  = false;
        showingListener =  (o, ov, nv) -> { if (nv) { applySettings(); } };
        this.settings   = new HashMap<>(settings);
        timeline        = new Timeline();
        observers       = new ConcurrentHashMap<>();
        clickedHandler  = e -> setSelected(!isSelected());

        initGraphics();
        registerListeners();
        applySettings();
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

        getStyleClass().addAll("apple", "macos-switch");

        backgroundArea = new Rectangle();
        backgroundArea.getStyleClass().addAll("background-area");
        if (isSelected()) {
            backgroundArea.setFill(getSelectedColor());
        }

        one = new Rectangle();
        one.getStyleClass().addAll("one");
        one.setMouseTransparent(true);
        one.setVisible(false);

        zero = new Circle();
        zero.getStyleClass().addAll("zero");
        zero.setMouseTransparent(true);
        zero.setVisible(false);

        knob = new Rectangle();
        knob.getStyleClass().addAll("knob");
        knob.setMouseTransparent(true);

        pane = new Pane(backgroundArea, one, zero, knob);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        disabledProperty().addListener(o -> setOpacity(isDisabled() ? 0.5 : 1.0));
        backgroundArea.addEventHandler(MouseEvent.MOUSE_CLICKED, clickedHandler);
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
                @Override public Object getBean() { return MacosSwitch.this; }
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
                @Override public Object getBean() { return MacosSwitch.this; }
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
                @Override public Object getBean() { return MacosSwitch.this; }
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
                @Override public Object getBean() { return MacosSwitch.this; }
                @Override public String getName() { return "showOnOffText"; }
            };
        }
        return showOnOffText;
    }

    protected HashMap<String, Property> getSettings() { return settings; }


    private void animateToSelect() {
        KeyValue kvBackgroundFillStart = new KeyValue(backgroundArea.fillProperty(), isDark() ? MacOSSystemColor.CTR_BACKGROUND.getColorDark() : MacOSSystemColor.CTR_BACKGROUND.getColorAqua(), Interpolator.EASE_BOTH);
        KeyValue kvBackgroundFillEnd   = new KeyValue(backgroundArea.fillProperty(), getSelectedColor(), Interpolator.EASE_BOTH);
        KeyValue kvKnobXStart          = new KeyValue(knob.xProperty(), backgroundArea.getLayoutBounds().getMinX() + height * 0.1, Interpolator.EASE_BOTH);
        KeyValue kvKnobXEnd            = new KeyValue(knob.xProperty(), backgroundArea.getLayoutBounds().getMaxX() - height * 0.9, Interpolator.EASE_BOTH);
        KeyValue kvOneOpacityStart     = new KeyValue(one.opacityProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kvOneOpacityEnd       = new KeyValue(one.opacityProperty(), 1, Interpolator.EASE_BOTH);
        KeyValue kvZeroOpacityStart    = new KeyValue(zero.opacityProperty(), zero.getOpacity(), Interpolator.EASE_BOTH);
        KeyValue kvZeroOpacityEnd      = new KeyValue(zero.opacityProperty(), 0, Interpolator.EASE_BOTH);

        KeyFrame kf0 = new KeyFrame(Duration.ZERO, kvBackgroundFillStart, kvKnobXStart, kvOneOpacityStart, kvZeroOpacityStart);
        KeyFrame kf1 = new KeyFrame(Duration.millis(getDuration() * 0.5), kvZeroOpacityEnd);
        KeyFrame kf2 = new KeyFrame(Duration.millis(getDuration()), kvBackgroundFillEnd, kvKnobXEnd, kvOneOpacityEnd);

        timeline.getKeyFrames().setAll(kf0, kf1, kf2);
        timeline.play();
    }
    private void animateToDeselect() {
        KeyValue kvBackgroundFillStart = new KeyValue(backgroundArea.fillProperty(), getSelectedColor(), Interpolator.EASE_BOTH);
        KeyValue kvBackgroundFillEnd   = new KeyValue(backgroundArea.fillProperty(), isDark() ? MacOSSystemColor.CTR_BACKGROUND.getColorDark() : MacOSSystemColor.CTR_BACKGROUND.getColorAqua(), Interpolator.EASE_BOTH);
        KeyValue kvKnobXStart          = new KeyValue(knob.xProperty(), backgroundArea.getLayoutBounds().getMaxX() - height * 0.9, Interpolator.EASE_BOTH);
        KeyValue kvKnobXEnd            = new KeyValue(knob.xProperty(), backgroundArea.getLayoutBounds().getMinX() + height * 0.1, Interpolator.EASE_BOTH);
        KeyValue kvOneOpacityStart     = new KeyValue(one.opacityProperty(), one.getOpacity(), Interpolator.EASE_BOTH);
        KeyValue kvOneOpacityEnd       = new KeyValue(one.opacityProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kvZeroOpacityStart    = new KeyValue(zero.opacityProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kvZeroOpacityEnd      = new KeyValue(zero.opacityProperty(), 1, Interpolator.EASE_BOTH);

        KeyFrame kf0 = new KeyFrame(Duration.ZERO, kvBackgroundFillStart, kvKnobXStart, kvOneOpacityStart, kvZeroOpacityStart);
        KeyFrame kf1 = new KeyFrame(Duration.millis(getDuration() * 0.5), kvOneOpacityEnd);
        KeyFrame kf2 = new KeyFrame(Duration.millis(getDuration()), kvBackgroundFillEnd, kvKnobXEnd, kvZeroOpacityEnd);

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

            backgroundArea.setWidth(width);
            backgroundArea.setHeight(height);
            backgroundArea.setArcWidth(height);
            backgroundArea.setArcHeight(height);

            one.setWidth(height * 0.0326087);
            one.setHeight(height * 0.32608696);
            one.setX(width * 0.225 - (one.getWidth() * 0.5));
            one.setY((height - one.getHeight()) * 0.5);

            zero.setRadius(height * 0.1413);
            zero.setCenterX(width * 0.765);
            zero.setCenterY(height * 0.5);
            zero.setStrokeWidth(height * 0.04);

            knob.setWidth(height * 0.84);
            knob.setHeight(height * 0.84);
            knob.setArcWidth(height * 0.84);
            knob.setArcHeight(height * 0.84);
            if (isSelected()) {
                knob.setX(backgroundArea.getLayoutBounds().getMaxX() - height * 0.9);
            } else {
                knob.setX(backgroundArea.getLayoutBounds().getMinX() + height * 0.1);
            }
            knob.setY((backgroundArea.getLayoutBounds().getHeight() - knob.getLayoutBounds().getHeight()) * 0.5);

            pane.setMaxSize(width, height);
            pane.setPrefSize(width, height);
            pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);
        }
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() { return MacosSwitch.class.getResource("apple.css").toExternalForm(); }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() { return FACTORY.getCssMetaData(); }
    @Override public List<CssMetaData<? extends Styleable, ?>> getCssMetaData()   { return FACTORY.getCssMetaData(); }
}
