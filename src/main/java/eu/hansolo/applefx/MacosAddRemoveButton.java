package eu.hansolo.applefx;

import eu.hansolo.applefx.event.MacEvt;
import eu.hansolo.applefx.tools.Helper;
import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.EvtType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class MacosAddRemoveButton extends HBox implements MacosControl {
    private static final PseudoClass                             DARK_PSEUDO_CLASS = PseudoClass.getPseudoClass("dark");
    private        final MacEvt                                  addEvt            = new MacEvt(MacosAddRemoveButton.this, MacEvt.ADD);
    private        final MacEvt                                  removeEvt         = new MacEvt(MacosAddRemoveButton.this, MacEvt.REMOVE);
    private              Map<EvtType, List<EvtObserver<MacEvt>>> observers;
    private              boolean                                 _dark;
    private              BooleanProperty                         dark;
    private              Region                                  plusIcon;
    private              Region                                  minusIcon;
    private              MacosButton                             addButton;
    private              MacosButton        removeButton;


    // ******************** Constructors **************************************
    public MacosAddRemoveButton() {
        super();
        init();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void init() {
        getStyleClass().add("macos-add-remove-button");
        observers = new ConcurrentHashMap<>();
        _dark     = Helper.isDarkMode();
        plusIcon  = new Region();
        plusIcon.getStyleClass().setAll("macos-add-remove-button", "macos-plus-icon");

        addButton = new MacosButton();
        addButton.setGraphic(plusIcon);
        addButton.getStyleClass().addAll("macos-add-remove-button", "macos-add-button");

        MacosToggleButtonBarSeparator separator = new MacosToggleButtonBarSeparator();

        minusIcon = new Region();
        minusIcon.getStyleClass().setAll("macos-add-remove-button", "macos-minus-icon");

        removeButton = new MacosButton();
        removeButton.setGraphic(minusIcon);
        removeButton.getStyleClass().addAll("macos-add-remove-button", "macos-remove-button");

        setSpacing(0);
        getChildren().setAll(addButton, separator, removeButton);
    }

    private void registerListeners() {
        addButton.setOnAction(e -> fireMacEvt(addEvt));
        removeButton.setOnAction(e -> fireMacEvt(removeEvt));
    }


    // ******************** Methods *******************************************
    @Override public final boolean isDark() {
        return null == dark ? _dark : dark.get();
    }
    @Override public final void setDark(final boolean dark) {
        if (null == this.dark) {
            _dark = dark;
            pseudoClassStateChanged(DARK_PSEUDO_CLASS, dark);
            plusIcon.pseudoClassStateChanged(DARK_PSEUDO_CLASS, dark);
            addButton.pseudoClassStateChanged(DARK_PSEUDO_CLASS, dark);
            minusIcon.pseudoClassStateChanged(DARK_PSEUDO_CLASS, dark);
            removeButton.pseudoClassStateChanged(DARK_PSEUDO_CLASS, dark);
            /*
            String style;
            if (isDark()) {
                style = new StringBuilder().append("-accent-color-dark: ").append(get().getDarkStyleClass()).append(";").append("-arrow-button-color: ").append(get().getDarkStyleClass()).toString();
            } else {
                style = new StringBuilder().append("-accent-color: ").append(get().getAquaStyleClass()).append(";").append("-arrow-button-color: ").append(get().getAquaStyleClass()).toString();
            }
            setStyle(style);
            */
        } else {
            this.dark.set(dark);
        }
    }
    @Override public final BooleanProperty darkProperty() {
        if (null == dark) {
            dark = new BooleanPropertyBase() {
                @Override protected void invalidated() {
                    pseudoClassStateChanged(DARK_PSEUDO_CLASS, get());
                }
                @Override public Object getBean() { return MacosAddRemoveButton.this; }
                @Override public String getName() { return "dark"; }
            };
        }
        return dark;
    }

    public boolean isAddDisable() { return addButton.isDisable(); }
    public void setAddDisable(final boolean disable) { addButton.setDisable(disable); }
    public BooleanProperty addDisableProperty() { return addButton.disableProperty(); }

    public boolean isRemoveDisable() { return removeButton.isDisable(); }
    public void setRemoveDisable(final boolean disable) { removeButton.setDisable(disable); }
    public BooleanProperty removeDisableProperty() { return removeButton.disableProperty(); }


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
}
