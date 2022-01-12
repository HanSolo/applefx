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
import eu.hansolo.applefx.tools.Helper.MacOSSystemColor;
import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.EvtType;
import javafx.beans.DefaultProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * User: hansolo
 * Date: 29.05.18
 * Time: 09:03
 */
@DefaultProperty("children")
public class IosMultiButton extends Region {
    public enum Type { CHECKBOX, ADD, DELETE, CHECK_MARK, DOT, SMALL_DOT, INFO, PLUS, FORWARD }

    public static final  Color                                    DEFAULT_SELECTED_COLOR  = MacOSSystemColor.BLUE.getColorAqua();
    private static final double                                   PREFERRED_WIDTH         = 22;
    private static final double                                   PREFERRED_HEIGHT        = 22;
    private static final double                                   MINIMUM_WIDTH           = 11;
    private static final double                                   MINIMUM_HEIGHT          = 11;
    private static final double                                   MAXIMUM_WIDTH           = 1024;
    private static final double                                   MAXIMUM_HEIGHT          = 1024;
    private static final StyleablePropertyFactory<IosMultiButton> FACTORY               = new StyleablePropertyFactory<>(Region.getClassCssMetaData());
    private final        MacEvt                                   selectedEvt           = new MacEvt(IosMultiButton.this, MacEvt.SELECTED);
    private final        MacEvt                                   deselectedEvt         = new MacEvt(IosMultiButton.this, MacEvt.DESELECTED);
    private final        MacEvt                                   pressedEvt            = new MacEvt(IosMultiButton.this, MacEvt.PRESSED);
    private final        MacEvt                                   releasedEvt           = new MacEvt(IosMultiButton.this, MacEvt.RELEASED);
    private static final PseudoClass                              CHECKBOX_PSEUDO_CLASS = PseudoClass.getPseudoClass("checkbox");
    private static final PseudoClass                              ADD_PSEUDO_CLASS        = PseudoClass.getPseudoClass("add");
    private static final PseudoClass                              DELETE_PSEUDO_CLASS     = PseudoClass.getPseudoClass("delete");
    private static final PseudoClass                              CHECK_MARK_PSEUDO_CLASS = PseudoClass.getPseudoClass("checkmark");
    private static final PseudoClass                              DOT_PSEUDO_CLASS        = PseudoClass.getPseudoClass("dot");
    private static final PseudoClass                              SMALL_DOT_PSEUDO_CLASS  = PseudoClass.getPseudoClass("smalldot");
    private static final PseudoClass                              INFO_PSEUDO_CLASS       = PseudoClass.getPseudoClass("info");
    private static final PseudoClass                              PLUS_PSEUDO_CLASS       = PseudoClass.getPseudoClass("plus");
    private static final PseudoClass                              FORWARD_PSEUDO_CLASS    = PseudoClass.getPseudoClass("forward");
    private static final PseudoClass                              SELECTED_PSEUDO_CLASS   = PseudoClass.getPseudoClass("selected");
    private        final StyleableProperty<Color>                 selectedColor;
    private              Map<EvtType, List<EvtObserver<MacEvt>>>  observers;
    private              double                                   size;
    private              double                                   width;
    private              double                                   height;
    private              Circle                                   circle;
    private              Region                                   icon;
    private              Pane                                     pane;
    private              Type                                     _type;
    private              ObjectProperty<Type>                     type;
    private              boolean                                  _selected;
    private              BooleanProperty                          selected;
    private              BooleanBinding                           showing;
    private              ChangeListener<Boolean>                  showingListener;
    private              EventHandler<MouseEvent>                 pressedHandler;
    private              EventHandler<MouseEvent>                 releasedHandler;
    private              HashMap<String, Property>                settings;


    // ******************** Constructors **************************************
    public IosMultiButton() {
        this(new HashMap<>());
    }
    public IosMultiButton(final Map<String, Property> SETTINGS) {
        _type           = Type.CHECKBOX;
        _selected       = false;
        selectedColor   = FACTORY.createStyleableColorProperty(IosMultiButton.this, "selectedColor", "-selected-color", s -> s.selectedColor, DEFAULT_SELECTED_COLOR);
        observers       = new ConcurrentHashMap<>();
        showingListener =  (o, ov, nv) -> { if (nv) { applySettings(); } };
        pressedHandler  = e -> {
            fireMacEvt(pressedEvt);
            if (Type.CHECKBOX == getType() && !isDisabled()) { setSelected(!isSelected()); }
        };
        releasedHandler = e -> fireMacEvt(releasedEvt);
        settings        = new HashMap<>(SETTINGS);

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

        getStyleClass().addAll("apple", "ios-multi-button");

        circle = new Circle(PREFERRED_HEIGHT * 0.5);
        circle.getStyleClass().add("circle");

        icon = new Region();
        icon.getStyleClass().setAll("icon");
        icon.setMouseTransparent(true);

        pane = new Pane(circle, icon);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        addEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
        addEventHandler(MouseEvent.MOUSE_RELEASED, releasedHandler);
        selectedColorProperty().addListener(o -> icon.setStyle(String.join("", "-selected-color: ", (getSelectedColor()).toString().replace("0x", "#"))));
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
            } else if("type".equals(key)) {
                setType(((ObjectProperty<Type>) settings.get(key)).get());
            }
        }

        if (settings.containsKey("selected")) { setSelected(((BooleanProperty) settings.get("selected")).get()); }

        settings.clear();
        if (null == showing) { return; }
        showing.removeListener(showingListener);
    }

    public void dispose() {
        removeEventHandler(MouseEvent.MOUSE_RELEASED, releasedHandler);
        removeEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
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
            fireMacEvt(selected ? selectedEvt : deselectedEvt);
            _selected = selected;
            pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, selected);
        } else {
            this.selected.set(selected);
        }
    }
    public BooleanProperty selectedProperty() {
        if (null == selected) {
            selected = new BooleanPropertyBase(_selected) {
                @Override protected void invalidated() {
                    fireMacEvt(get() ? selectedEvt : deselectedEvt);
                    pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, get());
                }
                @Override public Object getBean() { return IosMultiButton.this; }
                @Override public String getName() { return "selected"; }
            };
        }
        return selected;
    }

    public Color getSelectedColor() { return selectedColor.getValue(); }
    public void setSelectedColor(final Color color) { selectedColor.setValue(color); }
    public ObjectProperty<Color> selectedColorProperty() { return (ObjectProperty<Color>) selectedColor; }

    public Type getType() { return null == type ? _type : type.get(); }
    public void setType(final Type TYPE) {
        if (null == type) {
            _type = TYPE;
            adjustStyle();
        } else {
            type.set(TYPE);
        }
    }
    public ObjectProperty<Type> typeProperty() {
        if (null == type) {
            type = new ObjectPropertyBase<Type>(_type) {
                @Override protected void invalidated() { adjustStyle(); }
                @Override public Object getBean() { return IosMultiButton.this; }
                @Override public String getName() { return "type"; }
            };
            _type = null;
        }
        return type;
    }

    protected HashMap<String, Property> getSettings() { return settings; }

    private void adjustStyle() {
        switch(getType()) {
            case ADD        -> pseudoClassStateChanged(ADD_PSEUDO_CLASS, true);
            case DELETE     -> pseudoClassStateChanged(DELETE_PSEUDO_CLASS, true);
            case CHECK_MARK -> pseudoClassStateChanged(CHECK_MARK_PSEUDO_CLASS, true);
            case DOT        -> pseudoClassStateChanged(DOT_PSEUDO_CLASS, true);
            case SMALL_DOT  -> pseudoClassStateChanged(SMALL_DOT_PSEUDO_CLASS, true);
            case INFO       -> pseudoClassStateChanged(INFO_PSEUDO_CLASS, true);
            case PLUS       -> pseudoClassStateChanged(PLUS_PSEUDO_CLASS, true);
            case FORWARD    -> pseudoClassStateChanged(FORWARD_PSEUDO_CLASS, true);
            default -> {
                pseudoClassStateChanged(CHECKBOX_PSEUDO_CLASS, true);
                pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, isSelected());
            }
        }
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
            pane.setMaxSize(size, size);
            pane.setPrefSize(size, size);
            pane.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

            circle.setCenterX(size * 0.5);
            circle.setCenterY(size * 0.5);

            icon.setPrefSize(size, size);
        }
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() {
        return IosMultiButton.class.getResource("apple.css").toExternalForm();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() { return FACTORY.getCssMetaData(); }
    @Override public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() { return FACTORY.getCssMetaData(); }
}
