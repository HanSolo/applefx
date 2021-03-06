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
import eu.hansolo.applefx.tools.MacosAccentColor;
import eu.hansolo.applefx.tools.MacosSystemColor;
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
public class MacosSwitch extends Region implements MacosControl {
    public static final  double                                  MIN_DURATION           = 10;
    public static final  double                                  MAX_DURATION           = 500;
    public static final  Color                                   DEFAULT_ACCENT_COLOR   = MacosAccentColor.BLUE.getColorAqua();
    private static final double                                  MACOS_WIDTH            = 38;
    private static final double                                  MACOS_HEIGHT           = 22;
    private static final double                                  MACOS_KNOB_RADIUS      = 10;
    private static final double                                  MACOS_KNOB_INSET       = 1;
    private static final double                                  MACOS_KNOB_CENTER_Y    = 11;
    private static final double                                  IOS_WIDTH              = 38;
    private static final double                                  IOS_HEIGHT             = 25.5;
    private static final double                                  IOS_KNOB_RADIUS        = 11;
    private static final double                                  IOS_KNOB_INSET         = 1.5;
    private static final double                                  IOS_KNOB_CENTER_Y      = 12.75;
    private              double                                  width                  = MACOS_WIDTH;
    private              double                                  height                 = MACOS_HEIGHT;
    private              double                                  knobRadius             = MACOS_KNOB_RADIUS;
    private              double                                  knobInset              = MACOS_KNOB_INSET;
    private              double                                  knobCenterY            = MACOS_KNOB_CENTER_Y;
    private static final StyleablePropertyFactory<MacosSwitch>   FACTORY                = new StyleablePropertyFactory<>(Region.getClassCssMetaData());
    private static final PseudoClass                             DARK_PSEUDO_CLASS      = PseudoClass.getPseudoClass("dark");
    private static final PseudoClass                             IOS_PSEUDO_CLASS       = PseudoClass.getPseudoClass("ios");
    private final        MacEvt                                  selectedEvt            = new MacEvt(MacosSwitch.this, MacEvt.SELECTED);
    private final        MacEvt                                  deselectedEvt          = new MacEvt(MacosSwitch.this, MacEvt.DESELECTED);
    private final        StyleableProperty<Color>                accentColor;
    private              Map<EvtType, List<EvtObserver<MacEvt>>> observers;
    private              boolean                                 _dark;
    private              BooleanProperty                         dark;
    private              boolean                                 _ios;
    private              BooleanProperty                         ios;
    private              BooleanProperty                         windowFocusLost;
    private              Rectangle                               backgroundArea;
    private              Circle                                  knob;
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
    private              HashMap<String, Property>               settings;
    private              EventHandler<MouseEvent>                clickedHandler;



    // ******************** Constructors **************************************
    public MacosSwitch() {
        this(new HashMap<>());
    }
    public MacosSwitch(final Map<String, Property> settings) {
        _selected       = false;
        accentColor     = FACTORY.createStyleableColorProperty(MacosSwitch.this, "accentColor", "-accent-color", s -> s.accentColor, DEFAULT_ACCENT_COLOR);
        _dark           = Helper.isDarkMode();
        _ios            = false;
        windowFocusLost = new BooleanPropertyBase() {
            @Override protected void invalidated() {
                if (isSelected()) {
                    if (isDark()) {
                        backgroundArea.setFill(get() ? Color.rgb(106, 105, 104) : getAccentColor());
                    } else {
                        backgroundArea.setFill(get() ? Color.rgb(179, 179, 179) : getAccentColor());
                    }
                }
            }
            @Override public Object getBean() { return MacosSwitch.this; }
            @Override public String getName() { return "windowFocusLost"; }
        };
        _duration       = 250;
        _showOnOffText  = false;
        this.settings   = new HashMap<>(settings);
        timeline        = new Timeline();
        observers       = new ConcurrentHashMap<>();
        clickedHandler  = e -> {
            setSelected(!isSelected());
            if (windowFocusLost.get()) { windowFocusLost.set(false); }
        };

        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void initGraphics() {
        setMinSize(width, height);
        setMaxSize(width, height);
        setPrefSize(width, height);

        getStyleClass().add("macos-switch");

        backgroundArea = new Rectangle(width, height);
        backgroundArea.setArcWidth(height);
        backgroundArea.setArcHeight(height);
        backgroundArea.getStyleClass().addAll("background-area");
        if (isSelected()) {
            backgroundArea.setFill(getAccentColor());
        }

        one = new Rectangle(8, 8, 0.7, 6);
        one.getStyleClass().addAll("one");
        one.setMouseTransparent(true);
        one.setVisible(false);

        zero = new Circle(30, 11, 3);
        zero.getStyleClass().addAll("zero");
        zero.setMouseTransparent(true);
        zero.setVisible(false);

        knob = new Circle(knobRadius);
        knob.getStyleClass().addAll("knob");
        knob.setMouseTransparent(true);
        knob.setCenterX(isSelected() ? (width - knobRadius - knobInset) : (knobRadius + knobInset));
        knob.setCenterY(knobCenterY);

        pane = new Pane(backgroundArea, one, zero, knob);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
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
        accentColorProperty().addListener(o -> {
            if (isSelected()) {
                if (isDark()) {
                    backgroundArea.setFill(isWindowFocusLost() ? Color.rgb(106, 105, 104) : getAccentColor());
                } else {
                    backgroundArea.setFill(isWindowFocusLost() ? Color.rgb(179, 179, 179) : getAccentColor());
                }
            }
        });
    }

    private void setupBinding() {
        showing = Bindings.selectBoolean(sceneProperty(), "window", "showing");
        showing.addListener(o -> {
            if (showing.get()) {
                applySettings();
            }
        });
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
                setAccentColor(((ObjectProperty<Color>) settings.get(key)).get());
            } else if("dark".equals(key)) {
                setDark(((BooleanProperty) settings.get(key)).get());
            } else if ("ios".equals(key)) {
                setIos(((BooleanProperty) settings.get(key)).get());
            } else if ("showOnOffText".equals(key)) {
                setShowOnOffText(((BooleanProperty) settings.get(key)).get());
            } else if ("duration".equals(key)) {
                setDuration(((DoubleProperty) settings.get(key)).get());
            }
        }

        if (settings.containsKey("selected")) {
            setSelected(((BooleanProperty) settings.get("selected")).get());
        }

        settings.clear();
        if (null == showing) { return; }
    }

    public void dispose() {
        backgroundArea.removeEventHandler(MouseEvent.MOUSE_CLICKED, clickedHandler);
    }


    // ******************** Methods *******************************************
    @Override public void layoutChildren() {
        super.layoutChildren();
    }

    @Override protected double computeMinWidth(final double height) { return width; }
    @Override protected double computeMinHeight(final double width) { return height; }
    @Override protected double computePrefWidth(final double height) { return width; }
    @Override protected double computePrefHeight(final double width) { return height; }
    @Override protected double computeMaxWidth(final double height) { return width; }
    @Override protected double computeMaxHeight(final double width) { return height; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public boolean isSelected() { return null == selected ? _selected : selected.get(); }
    public void setSelected(final boolean selected) {
        if (null == this.selected) {
            _selected = selected;
            if (_selected) {
                fireMacEvt(selected ? selectedEvt : deselectedEvt);
                animateToSelected();
            } else {
                animateToDeselected();
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
                        animateToSelected();
                    } else {
                        animateToDeselected();
                    }
                }
                @Override public Object getBean() { return MacosSwitch.this; }
                @Override public String getName() { return "selected"; }
            };
        }
        return selected;
    }

    public Color getAccentColor() { return accentColor.getValue(); }
    public void setAccentColor(final Color color) { this.accentColor.setValue(color); }
    public void setAccentColor(final MacosAccentColor accentColor) { this.accentColor.setValue(isDark() ? accentColor.getColorDark() : accentColor.getColorAqua()); }
    public ObjectProperty<Color> accentColorProperty() { return (ObjectProperty<Color>) accentColor; }

    @Override public final boolean isDark() {
        return null == dark ? _dark : dark.get();
    }
    @Override public final void setDark(final boolean dark) {
        if (null == this.dark) {
            _dark = dark;
            pseudoClassStateChanged(DARK_PSEUDO_CLASS, dark);
            backgroundArea.setFill(dark ? MacosSystemColor.CTRL_BACKGROUND.dark() : MacosSystemColor.CTRL_BACKGROUND.aqua());
        } else {
            this.dark.set(dark);
        }
    }
    @Override public final BooleanProperty darkProperty() {
        if (null == dark) {
            dark = new BooleanPropertyBase() {
                @Override protected void invalidated() {
                    pseudoClassStateChanged(DARK_PSEUDO_CLASS, get());
                    backgroundArea.setFill(get() ? MacosSystemColor.CTRL_BACKGROUND.dark() : MacosSystemColor.CTRL_BACKGROUND.aqua());
                }
                @Override public Object getBean() { return MacosSwitch.this; }
                @Override public String getName() { return "dark"; }
            };
        }
        return dark;
    }

    public final boolean isIos() {
        return null == ios ? _ios : ios.get();
    }
    public final void setIos(final boolean ios) {
        if (null == this.ios) {
            _ios = ios;
            pseudoClassStateChanged(IOS_PSEUDO_CLASS, ios);
            if (ios) {
                width       = IOS_WIDTH;
                height      = IOS_HEIGHT;
                knobRadius  = IOS_KNOB_RADIUS;
                knobInset   = IOS_KNOB_INSET;
                knobCenterY = IOS_KNOB_CENTER_Y;
                one.setX(7);
                one.setY(9);
                one.setWidth(0.7);
                one.setHeight(7.5);
                zero.setRadius(3.75);
                zero.setCenterX(31);
                zero.setCenterY(12.75);
            } else {
                width       = MACOS_WIDTH;
                height      = MACOS_HEIGHT;
                knobRadius  = MACOS_KNOB_RADIUS;
                knobInset   = MACOS_KNOB_INSET;
                knobCenterY = MACOS_KNOB_CENTER_Y;
                one.setX(8);
                one.setY(8);
                one.setWidth(0.7);
                one.setHeight(6);
                zero.setRadius(3);
                zero.setCenterX(30);
                zero.setCenterY(11);
            }
            setMinSize(width, height);
            setMaxSize(width, height);
            setPrefSize(width, height);
            backgroundArea.setHeight(height);
            backgroundArea.setArcWidth(height);
            backgroundArea.setArcHeight(height);
            knob.setRadius(knobRadius);
            knob.setCenterX(isSelected() ? (width - knobRadius - knobInset) : (knobRadius + knobInset));
            knob.setCenterY(knobCenterY);
        } else {
            this.dark.set(ios);
        }
    }
    public final BooleanProperty iosProperty() {
        if (null == ios) {
            ios = new BooleanPropertyBase() {
                @Override protected void invalidated() {
                    pseudoClassStateChanged(IOS_PSEUDO_CLASS, get());
                    if (get()) {
                        width       = IOS_WIDTH;
                        height      = IOS_HEIGHT;
                        knobRadius  = IOS_KNOB_RADIUS;
                        knobInset   = IOS_KNOB_INSET;
                        knobCenterY = IOS_KNOB_CENTER_Y;
                        one.setX(7);
                        one.setY(9);
                        one.setWidth(0.7);
                        one.setHeight(7.5);
                        zero.setRadius(3.75);
                        zero.setCenterX(31);
                        zero.setCenterY(12.75);
                    } else {
                        width       = MACOS_WIDTH;
                        height      = MACOS_HEIGHT;
                        knobRadius  = MACOS_KNOB_RADIUS;
                        knobInset   = MACOS_KNOB_INSET;
                        knobCenterY = MACOS_KNOB_CENTER_Y;
                        one.setX(8);
                        one.setY(8);
                        one.setWidth(0.7);
                        one.setHeight(6);
                        zero.setRadius(3);
                        zero.setCenterX(30);
                        zero.setCenterY(11);
                    }
                    setMinSize(width, height);
                    setMaxSize(width, height);
                    setPrefSize(width, height);
                    backgroundArea.setHeight(height);
                    backgroundArea.setArcWidth(height);
                    backgroundArea.setArcHeight(height);
                    knob.setRadius(knobRadius);
                    knob.setCenterX(isSelected() ? (width - knobRadius - knobInset) : (knobRadius + knobInset));
                    knob.setCenterY(knobCenterY);
                }
                @Override public Object getBean() { return MacosSwitch.this; }
                @Override public String getName() { return "ios"; }
            };
        }
        return ios;
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

    public boolean isWindowFocusLost() { return windowFocusLost.get(); }
    public void setWindowFocusLost(final boolean windowFocusLost) { this.windowFocusLost.set(windowFocusLost); }
    public BooleanProperty windowFocusLostProperty() { return windowFocusLost; }

    protected HashMap<String, Property> getSettings() { return settings; }

    private void animateToSelected() {
        KeyValue kvBackgroundFillStart = new KeyValue(backgroundArea.fillProperty(), isDark() ? MacosSystemColor.CTRL_BACKGROUND.dark() : MacosSystemColor.CTRL_BACKGROUND.aqua(), Interpolator.EASE_BOTH);
        KeyValue kvBackgroundFillEnd   = new KeyValue(backgroundArea.fillProperty(), getAccentColor(), Interpolator.EASE_BOTH);
        KeyValue kvKnobXStart          = new KeyValue(knob.centerXProperty(), knobRadius + knobInset, Interpolator.EASE_BOTH);
        KeyValue kvKnobXEnd            = new KeyValue(knob.centerXProperty(), width - knobRadius - knobInset, Interpolator.EASE_BOTH);
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
    private void animateToDeselected() {
        KeyValue kvBackgroundFillStart = new KeyValue(backgroundArea.fillProperty(), getAccentColor(), Interpolator.EASE_BOTH);
        KeyValue kvBackgroundFillEnd   = new KeyValue(backgroundArea.fillProperty(), isDark() ? MacosSystemColor.CTRL_BACKGROUND.dark() : MacosSystemColor.CTRL_BACKGROUND.aqua(), Interpolator.EASE_BOTH);
        KeyValue kvKnobXStart          = new KeyValue(knob.centerXProperty(), width - knobRadius - knobInset, Interpolator.EASE_BOTH);
        KeyValue kvKnobXEnd            = new KeyValue(knob.centerXProperty(), knobRadius + knobInset, Interpolator.EASE_BOTH);
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


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() { return MacosSwitch.class.getResource("apple.css").toExternalForm(); }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() { return FACTORY.getCssMetaData(); }
    @Override public List<CssMetaData<? extends Styleable, ?>> getCssMetaData()   { return FACTORY.getCssMetaData(); }
}
