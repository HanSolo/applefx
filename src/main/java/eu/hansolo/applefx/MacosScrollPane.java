package eu.hansolo.applefx;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.util.Duration;


public class MacosScrollPane extends ScrollPane {
    private static final PseudoClass     DARK_PSEUDO_CLASS = PseudoClass.getPseudoClass("dark");
    private              boolean         _dark;
    private              BooleanProperty dark;
    private              SequentialTransition fadeInSequence;
    private              SequentialTransition fadeOutSequence;


    public MacosScrollPane() {
        super();
        init();
    }
    public MacosScrollPane(final Node node) {
        super(node);
        init();
    }


    private void init() {
        getStyleClass().addAll("apple", "macos-scroll-pane");
        _dark           = false;
        fadeInSequence  = new SequentialTransition();
        fadeOutSequence = new SequentialTransition();
        registerListeners();
    }

    private void registerListeners() {
        setOnScrollStarted(e -> fadeInScrollBars());
        setOnScrollFinished(e -> fadeOutScrollBars());
        getChildren().addListener((ListChangeListener<Node>) c -> {
            for (Node node : lookupAll(".scroll-bar")) {
                if (node instanceof ScrollBar) {
                    ScrollBar scrollBar = (ScrollBar) node;
                    scrollBar.setOpacity(0);
                }
            }
        });
    }

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
                @Override public Object getBean() { return MacosScrollPane.this; }
                @Override public String getName() { return "dark"; }
            };
        }
        return dark;
    }

    private void fadeInScrollBars() {
        fadeInSequence.stop();
        for (Node node : lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar) {
                ScrollBar scrollBar = (ScrollBar) node;
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300));
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.setNode(scrollBar);
                fadeInSequence.setNode(scrollBar);
                fadeInSequence.getChildren().setAll(fadeIn);
                fadeInSequence.play();
            }
        }
    }
    private void fadeOutScrollBars() {
        fadeOutSequence.stop();
        for (Node node : lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar) {
                ScrollBar scrollBar = (ScrollBar) node;
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300));
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setNode(scrollBar);
                fadeOutSequence.setNode(scrollBar);
                fadeOutSequence.getChildren().setAll(new PauseTransition(Duration.millis(1000)), fadeOut);
                fadeOutSequence.play();
            }
        }
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() { return MacosScrollPane.class.getResource("apple.css").toExternalForm(); }
}
