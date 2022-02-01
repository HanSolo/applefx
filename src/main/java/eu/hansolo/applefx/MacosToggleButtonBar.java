package eu.hansolo.applefx;

import eu.hansolo.applefx.tools.Helper;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class MacosToggleButtonBar extends HBox implements MacosControl {
    private static final PseudoClass          DARK_PSEUDO_CLASS = PseudoClass.getPseudoClass("dark");
    private              boolean              _dark;
    private              BooleanProperty      dark;
    private              InvalidationListener selectionListener;


    // ******************** Constructors **************************************
    public MacosToggleButtonBar() {
        this(1, new Node[]{});
    }
    public MacosToggleButtonBar(final double spacing) {
        this(1, new Node[]{});
    }
    public MacosToggleButtonBar(final Node... toggleButtons) {
        this(1, toggleButtons);
    }
    public MacosToggleButtonBar(final double spacing, final Node... toggleButtons) {
        super(spacing, toggleButtons);
        init();
    }


    // ******************** Initialization ************************************
    private void init() {
        setSpacing(1);
        getStyleClass().add("macos-toggle-button-bar");
        _dark             = Helper.isDarkMode();
        selectionListener = e -> setSeparatorVisibilities();
        registerListeners();
        setSeparatorVisibilities();
    }

    private void registerListeners() {
        getChildren().forEach(n -> {
            if (n instanceof MacosToggleButton) {
                MacosToggleButton macosToggleButton = (MacosToggleButton) n;
                macosToggleButton.selectedProperty().addListener(selectionListener);
            }
        });

        getChildren().addListener((ListChangeListener<Node>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(n -> {
                        if (n instanceof MacosToggleButton) {
                            MacosToggleButton macosToggleButton = (MacosToggleButton) n;
                            macosToggleButton.selectedProperty().addListener(selectionListener);
                        }
                    });
                } else if (c.wasRemoved()) {
                    c.getRemoved().forEach(n -> {
                        if (n instanceof MacosToggleButton) {
                            MacosToggleButton macosToggleButton = (MacosToggleButton) n;
                            macosToggleButton.selectedProperty().removeListener(selectionListener);
                        }
                    });
                }
            }
        });
    }

    private void setSeparatorVisibilities() {
        final int size = getChildren().size();
        boolean wasSelected = false;
        for (int i = 0 ; i < getChildren().size() ; i++) {
            Node node = getChildren().get(i);
            boolean isMacosToggleButton = node instanceof MacosToggleButton;
            if (isMacosToggleButton && size > 0) {
                MacosToggleButton toggleButton = (MacosToggleButton) node;
                boolean           isSelected   = toggleButton.isSelected();
                if (0 == i) {
                    Node nextNode = getChildren().get(i + 1);
                    if (nextNode instanceof MacosToggleButtonBarSeparator) {
                        nextNode.setVisible(!isSelected);
                    }
                } else if (i < size - 1) {
                    Node lastNode = getChildren().get(i - 1);
                    if (lastNode instanceof MacosToggleButtonBarSeparator) {
                        if (isSelected) {
                            lastNode.setVisible(false);
                        } else {
                            lastNode.setVisible(!wasSelected);
                        }
                    }
                    Node nextNode = getChildren().get(i + 1);
                    if (nextNode instanceof MacosToggleButtonBarSeparator) {
                        nextNode.setVisible(!isSelected);
                    }
                } else if (size - 1 == i) {
                    Node lastNode = getChildren().get(i - 1);
                    if (lastNode instanceof MacosToggleButtonBarSeparator) {
                        if (isSelected) {
                            lastNode.setVisible(false);
                        } else {
                            lastNode.setVisible(!wasSelected);
                        }
                    }
                }
                wasSelected = isSelected;
            }
        }
    }


    // ******************** Methods *******************************************
    @Override public final boolean isDark() {
        return null == dark ? _dark : dark.get();
    }
    @Override public final void setDark(final boolean dark) {
        if (null == this.dark) {
            _dark = dark;
            pseudoClassStateChanged(DARK_PSEUDO_CLASS, dark);
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
                @Override public Object getBean() { return MacosToggleButtonBar.this; }
                @Override public String getName() { return "dark"; }
            };
        }
        return dark;
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() { return MacosToggleButtonBar.class.getResource("apple.css").toExternalForm(); }
}
