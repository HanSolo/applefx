package eu.hansolo.applefx;

import eu.hansolo.applefx.tools.MacosAccentColor;
import eu.hansolo.applefx.tools.MacosSystemColor;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


public class MacosCheckBoxSwitchSkin extends SkinBase<MacosCheckBoxSwitch> {
    private static final double                           WIDTH          = 38;
    private static final double                           HEIGHT         = 25.5;
    private static final double                           THUMB_RADIUS   = 11;
    private static final double                           THUMB_INSET    = 1.5;
    private static final double                           THUMB_CENTER_Y = 12.75;
    private static final Duration                         DURATION       = Duration.millis(250);
    private static final Duration                         HALF_DURATION  = Duration.millis(125);
    private final        MacosCheckBoxSwitch              control;
    private final        Timeline                         timeline;
    private              Circle                           thumb;
    private              Circle                           zero;
    private              Rectangle                        one;
    private              Pane                             pane;
    private              EventHandler<MouseEvent>         mouseHandler;
    private              ChangeListener<Boolean>          selectionListener;
    private              ChangeListener<Color>            bkgColorListener;
    private              ChangeListener<MacosAccentColor> accentColorListener;
    private              ChangeListener<Boolean>          showDescriptionListener;


    // ******************** Constructors **************************************
    public MacosCheckBoxSwitchSkin(final MacosCheckBoxSwitch control) {
        super(control);
        this.control                 = control;
        this.timeline                = new Timeline();
        this.mouseHandler            = e -> control.setSelected(!control.isSelected());
        this.selectionListener       = (o, ov, nv) -> {
            if (nv) {
                animateToSelected();
            } else {
                animateToDeselected();
            }
        };
        this.bkgColorListener        = (o, ov, nv) -> pane.setStyle("-bkg-color: " + nv.toString().replace("0x", "#"));
        this.accentColorListener     = (o, ov, nv) -> { if (control.isSelected()) { control.setBkgColor(control.isDark() ? nv.getColorDark() : nv.getColorAqua()); } };
        this.showDescriptionListener = (o, ov, nv) -> {
            one.setVisible(nv);
            zero.setVisible(nv);
        };
        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void initGraphics() {
        control.setPrefSize(WIDTH, HEIGHT);

        thumb = new Circle(control.isSelected() ? WIDTH - THUMB_RADIUS - THUMB_INSET : THUMB_RADIUS + THUMB_INSET, THUMB_CENTER_Y, THUMB_RADIUS);
        thumb.getStyleClass().setAll("switch-thumb");
        thumb.toFront();
        thumb.setMouseTransparent(true);

        one = new Rectangle(7, 9, 0.7, 7.5);
        one.getStyleClass().addAll("switch-one");
        one.setMouseTransparent(true);
        one.setVisible(control.getShowDescriptions());

        zero = new Circle(31, 12.75, 3.75);
        zero.getStyleClass().addAll("switch-zero");
        zero.setMouseTransparent(true);
        zero.setVisible(control.getShowDescriptions());

        pane = new Pane();
        pane.setMinSize(WIDTH, HEIGHT);
        pane.setMaxSize(WIDTH, HEIGHT);
        pane.setPrefSize(WIDTH, HEIGHT);
        pane.getStyleClass().setAll("switch-background");

        pane.getChildren().addAll(one, zero, thumb);
        getChildren().add(pane);
    }

    private void registerListeners() {
        pane.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseHandler);
        control.selectedProperty().addListener(selectionListener);
        control.bkgColorProperty().addListener(bkgColorListener);
        control.accentColorProperty().addListener(accentColorListener);
        control.showDescriptionsProperty().addListener(showDescriptionListener);
    }


    // ******************** Methods *******************************************
    private void animateToSelected() {
        KeyValue kvBackgroundFillStart = new KeyValue(control.bkgColorProperty(), control.isDark() ? MacosSystemColor.CTRL_BACKGROUND.dark() : MacosSystemColor.CTRL_BACKGROUND.aqua(), Interpolator.EASE_BOTH);
        KeyValue kvBackgroundFillEnd   = new KeyValue(control.bkgColorProperty(), control.isDark() ? control.getAccentColor().getColorDark() : control.getAccentColor().getColorAqua(), Interpolator.EASE_BOTH);
        KeyValue kvThumbXStart         = new KeyValue(thumb.centerXProperty(), THUMB_RADIUS + THUMB_INSET, Interpolator.EASE_BOTH);
        KeyValue kvThumbXEnd           = new KeyValue(thumb.centerXProperty(), WIDTH - THUMB_RADIUS - THUMB_INSET, Interpolator.EASE_BOTH);
        KeyValue kvOneOpacityStart     = new KeyValue(one.opacityProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kvOneOpacityEnd       = new KeyValue(one.opacityProperty(), 1, Interpolator.EASE_BOTH);
        KeyValue kvZeroOpacityStart    = new KeyValue(zero.opacityProperty(), zero.getOpacity(), Interpolator.EASE_BOTH);
        KeyValue kvZeroOpacityEnd      = new KeyValue(zero.opacityProperty(), 0, Interpolator.EASE_BOTH);

        KeyFrame kf0 = new KeyFrame(Duration.ZERO, kvBackgroundFillStart, kvThumbXStart, kvOneOpacityStart, kvZeroOpacityStart);
        KeyFrame kf1 = new KeyFrame(HALF_DURATION, kvZeroOpacityEnd);
        KeyFrame kf2 = new KeyFrame(DURATION, kvBackgroundFillEnd, kvThumbXEnd, kvOneOpacityEnd);

        timeline.getKeyFrames().setAll(kf0, kf1, kf2);
        timeline.play();
    }
    private void animateToDeselected() {
        KeyValue kvBackgroundFillStart = new KeyValue(control.bkgColorProperty(), control.isDark() ? control.getAccentColor().getColorDark() : control.getAccentColor().getColorAqua(), Interpolator.EASE_BOTH);
        KeyValue kvBackgroundFillEnd   = new KeyValue(control.bkgColorProperty(), control.isDark() ? MacosSystemColor.CTRL_BACKGROUND.dark() : MacosSystemColor.CTRL_BACKGROUND.aqua(), Interpolator.EASE_BOTH);
        KeyValue kvThumbXStart         = new KeyValue(thumb.centerXProperty(), WIDTH - THUMB_RADIUS - THUMB_INSET, Interpolator.EASE_BOTH);
        KeyValue kvThumbXEnd           = new KeyValue(thumb.centerXProperty(), THUMB_RADIUS + THUMB_INSET, Interpolator.EASE_BOTH);
        KeyValue kvOneOpacityStart     = new KeyValue(one.opacityProperty(), one.getOpacity(), Interpolator.EASE_BOTH);
        KeyValue kvOneOpacityEnd       = new KeyValue(one.opacityProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kvZeroOpacityStart    = new KeyValue(zero.opacityProperty(), 0, Interpolator.EASE_BOTH);
        KeyValue kvZeroOpacityEnd      = new KeyValue(zero.opacityProperty(), 1, Interpolator.EASE_BOTH);

        KeyFrame kf0 = new KeyFrame(Duration.ZERO, kvBackgroundFillStart, kvThumbXStart, kvOneOpacityStart, kvZeroOpacityStart);
        KeyFrame kf1 = new KeyFrame(HALF_DURATION, kvOneOpacityEnd);
        KeyFrame kf2 = new KeyFrame(DURATION, kvBackgroundFillEnd, kvThumbXEnd, kvZeroOpacityEnd);

        timeline.getKeyFrames().setAll(kf0, kf1, kf2);
        timeline.play();
    }

    @Override public void dispose() {
        super.dispose();
        pane.removeEventFilter(MouseEvent.MOUSE_PRESSED, mouseHandler);
        control.selectedProperty().removeListener(selectionListener);
        control.bkgColorProperty().removeListener(bkgColorListener);
        control.accentColorProperty().removeListener(accentColorListener);
        control.showDescriptionsProperty().removeListener(showDescriptionListener);
    }
}