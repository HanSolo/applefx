package eu.hansolo.applefx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.Node;


public class MacosSelectableLabel extends MacosLabel {
    private static final PseudoClass     SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    private              boolean         _selected;
    private              BooleanProperty selected;


    // ******************** Constructors **************************************
    public MacosSelectableLabel() {
        super();
        init();
    }
    public MacosSelectableLabel(final String text) {
        super(text);
        init();
    }
    public MacosSelectableLabel(final String text, final Node graphic) {
        super(text, graphic);
        init();
    }


    // ******************** Initialization ************************************
    private void init() {
        getStyleClass().add("macos-label");
        _selected = false;
    }



    // ******************** Methods *******************************************
    public final boolean isSelected() {
        return null == selected ? _selected : selected.get();
    }
    public final void setSelected(final boolean selected) {
        if (null == this.selected) {
            _selected = selected;
            pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, selected);
        } else {
            this.selected.set(selected);
        }
    }
    public final BooleanProperty selectedProperty() {
        if (null == selected) {
            selected = new BooleanPropertyBase() {
                @Override protected void invalidated() {
                    pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, get());
                }
                @Override public Object getBean() { return MacosSelectableLabel.this; }
                @Override public String getName() { return "selected"; }
            };
        }
        return selected;
    }
}
