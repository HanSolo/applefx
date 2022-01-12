package eu.hansolo.applefx;

import eu.hansolo.toolbox.Helper;
import javafx.animation.Transition;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import javafx.util.StringConverter;


public class MacosSliderSkin extends SkinBase<Slider> {
    private Slider                  slider;
    private NumberAxis              tickLine       = null;
    private double                  trackToTickGap = 2;

    private boolean                 showTickMarks;
    private double                  thumbWidth;
    private double                  thumbHeight;

    private double                  trackStart;
    private double                  trackLength;
    private double                  thumbTop;
    private double                  thumbLeft;
    private double                  preDragThumbPos;
    private Point2D                 dragStart; // in skin coordinates

    private StackPane               thumb;
    private StackPane               track;
    private StackPane               trackProgress;
    private Line                    centerLine;
    private boolean                 trackClicked = false;
    private StringConverter<Number> stringConverterWrapper;


    public MacosSliderSkin(final Slider slider) {
        super(slider);
        this.slider = slider;

        init();
        slider.requestLayout();
        registerChangeListener(slider.minProperty(), e -> {
            if (showTickMarks && tickLine != null) { tickLine.setLowerBound(slider.getMin()); }
            slider.requestLayout();
        });
        registerChangeListener(slider.maxProperty(), e -> {
            if (showTickMarks && tickLine != null) { tickLine.setUpperBound(slider.getMax()); }
            slider.requestLayout();
        });
        registerChangeListener(slider.valueProperty(), e -> positionThumb(trackClicked));
        registerChangeListener(slider.orientationProperty(), e -> {
            if (showTickMarks && tickLine != null) {
                boolean isVertical = slider.getOrientation() == Orientation.VERTICAL;
                tickLine.setSide(isVertical ? Side.RIGHT : (slider.getOrientation() == null) ? Side.RIGHT : Side.BOTTOM);
            }
            slider.requestLayout();
        });
        registerChangeListener(slider.showTickMarksProperty(), e -> setShowTickMarks(slider.isShowTickMarks(), slider.isShowTickLabels()));
        registerChangeListener(slider.showTickLabelsProperty(), e -> setShowTickMarks(slider.isShowTickMarks(), slider.isShowTickLabels()));
        registerChangeListener(slider.majorTickUnitProperty(), e -> {
            if (tickLine != null) {
                tickLine.setTickUnit(slider.getMajorTickUnit());
                slider.requestLayout();
            }
        });
        registerChangeListener(slider.minorTickCountProperty(), e -> {
            if (tickLine != null) {
                tickLine.setMinorTickCount(Math.max(slider.getMinorTickCount(),0) + 1);
                slider.requestLayout();
            }
        });
        registerChangeListener(slider.labelFormatterProperty(), e -> {
            if (tickLine != null) {
                if (slider.getLabelFormatter() == null) {
                    tickLine.setTickLabelFormatter(null);
                } else {
                    tickLine.setTickLabelFormatter(stringConverterWrapper);
                    tickLine.requestAxisLayout();
                }
            }
        });
        registerChangeListener(slider.snapToTicksProperty(), e -> slider.adjustValue(slider.getValue()));


        if (slider instanceof IosSlider) {
            IosSlider iosSlider = (IosSlider) slider;
            registerChangeListener(iosSlider.balanceProperty(), e -> {
                IosSlider sldr = (IosSlider) slider;
                boolean isBalance = sldr.getBalance();
                trackProgress.setVisible(!isBalance);
                centerLine.setVisible(isBalance);
                if (isBalance) { sldr.setValue(sldr.getRange() * 0.5); }
                sldr = null;
            });
            iosSlider = null;
        }

        stringConverterWrapper = new StringConverter<>() {
            @Override public String toString(Number object) {
                return(object != null) ? slider.getLabelFormatter().toString(object.doubleValue()) : "";
            }
            @Override public Number fromString(String string) {
                return slider.getLabelFormatter().fromString(string);
            }
        };
    }


    private void init() {
        thumb = new StackPane() {
            @Override public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
                switch (attribute) {
                    case VALUE -> { return slider.getValue(); }
                    default    -> { return super.queryAccessibleAttribute(attribute, parameters); }
                }
            }
        };
        thumb.getStyleClass().setAll("thumb");
        thumb.setAccessibleRole(AccessibleRole.THUMB);

        centerLine = new Line();
        centerLine.getStyleClass().setAll("center-line");

        track = new StackPane();
        track.getStyleClass().setAll("track");

        trackProgress = new StackPane();
        trackProgress.getStyleClass().setAll("track-progress");
        if (getSkinnable() instanceof IosSlider) {
            IosSlider iosSlider = (IosSlider) getSkinnable();
            boolean isBalance = iosSlider.getBalance();
            trackProgress.setVisible(!isBalance);
            if (isBalance) { iosSlider.setValue(iosSlider.getRange() * 0.5); }
            iosSlider = null;
        }

        getChildren().setAll(track, centerLine, trackProgress, thumb);
        setShowTickMarks(slider.isShowTickMarks(), slider.isShowTickLabels());


        track.setOnMousePressed(me -> {
            if (!thumb.isPressed()) {
                trackClicked = true;
                if (slider.getOrientation() == Orientation.HORIZONTAL) {
                    trackPress(me, (me.getX() / trackLength));
                } else {
                    trackPress(me, (me.getY() / trackLength));
                }
                trackClicked = false;
            }
        });
        track.setOnMouseDragged(me -> {
            if (!thumb.isPressed()) {
                if (slider.getOrientation() == Orientation.HORIZONTAL) {
                    trackPress(me, (me.getX() / trackLength));
                } else {
                    trackPress(me, (me.getY() / trackLength));
                }
            }
        });

        thumb.setOnMousePressed(me -> {
            thumbPressed(me, 0.0f);
            dragStart = thumb.localToParent(me.getX(), me.getY());
            preDragThumbPos = (slider.getValue() - slider.getMin()) /
                              (slider.getMax() - slider.getMin());
        });
        thumb.setOnMouseReleased(me -> thumbReleased(me));
        thumb.setOnMouseDragged(me -> {
            Point2D cur = thumb.localToParent(me.getX(), me.getY());
            double dragPos = (slider.getOrientation() == Orientation.HORIZONTAL)?
                             cur.getX() - dragStart.getX() : -(cur.getY() - dragStart.getY());
            thumbDragged(me, preDragThumbPos + dragPos / trackLength);
        });
    }

    @Override protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (slider.getOrientation() == Orientation.HORIZONTAL) {
            return (leftInset + minTrackLength() + thumb.minWidth(-1) + rightInset);
        } else {
            return (leftInset + thumb.prefWidth(-1) + rightInset);
        }
    }
    @Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (slider.getOrientation() == Orientation.HORIZONTAL) {
            double axisHeight = showTickMarks ? (tickLine.prefHeight(-1) + trackToTickGap) : 0;
            return (topInset + thumb.prefHeight(-1) + axisHeight + bottomInset);
        } else {
            return (topInset + minTrackLength() + thumb.prefHeight(-1) + bottomInset);
        }
    }
    @Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (slider.getOrientation() == Orientation.HORIZONTAL) {
            if(showTickMarks) {
                return Math.max(140, tickLine.prefWidth(-1));
            } else {
                return 140;
            }
        } else {
            double axisWidth = showTickMarks ? (tickLine.prefWidth(-1) + trackToTickGap) : 0;
            return leftInset + Math.max(thumb.prefWidth(-1), track.prefWidth(-1)) + axisWidth + rightInset;
        }
    }
    @Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (slider.getOrientation() == Orientation.HORIZONTAL) {
            return topInset + Math.max(thumb.prefHeight(-1), track.prefHeight(-1)) +
                   ((showTickMarks) ? (trackToTickGap+tickLine.prefHeight(-1)) : 0)  + bottomInset;
        } else {
            if(showTickMarks) {
                return Math.max(140, tickLine.prefHeight(-1));
            } else {
                return 140;
            }
        }
    }
    @Override protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (slider.getOrientation() == Orientation.HORIZONTAL) {
            return Double.MAX_VALUE;
        } else {
            return slider.prefWidth(-1);
        }
    }
    @Override protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (slider.getOrientation() == Orientation.HORIZONTAL) {
            return slider.prefHeight(width);
        } else {
            return Double.MAX_VALUE;
        }
    }


    private void setShowTickMarks(final boolean ticksVisible, final boolean labelsVisible) {
        showTickMarks = (ticksVisible || labelsVisible);
        if (showTickMarks) {
            if (tickLine == null) {
                tickLine = new NumberAxis();
                tickLine.setAutoRanging(false);
                tickLine.setSide(slider.getOrientation() == Orientation.VERTICAL ? Side.RIGHT : (slider.getOrientation() == null) ? Side.RIGHT : Side.BOTTOM);
                tickLine.setUpperBound(slider.getMax());
                tickLine.setLowerBound(slider.getMin());
                tickLine.setTickUnit(slider.getMajorTickUnit());
                tickLine.setTickMarkVisible(ticksVisible);
                tickLine.setTickLabelsVisible(labelsVisible);
                tickLine.setMinorTickVisible(ticksVisible);
                // add 1 to the slider minor tick count since the axis draws one
                // less minor ticks than the number given.
                tickLine.setMinorTickCount(Math.max(slider.getMinorTickCount(),0) + 1);
                if (slider.getLabelFormatter() != null) {
                    tickLine.setTickLabelFormatter(stringConverterWrapper);
                }
                getChildren().setAll(tickLine, track, centerLine, trackProgress, thumb);
            } else {
                tickLine.setTickLabelsVisible(labelsVisible);
                tickLine.setTickMarkVisible(ticksVisible);
                tickLine.setMinorTickVisible(ticksVisible);
            }
        }
        else  {
            getChildren().setAll(track, centerLine, trackProgress, thumb);
            //            tickLine = null;
        }

        slider.requestLayout();
    }

    private void positionThumb(final boolean animate) {
        if (slider.getValue() > slider.getMax()) return;// this can happen if we are bound to something
        final boolean HORIZONTAL = slider.getOrientation() == Orientation.HORIZONTAL;
        final double  END_X      = (HORIZONTAL) ? trackStart + (((trackLength * ((slider.getValue() - slider.getMin()) / (slider.getMax() - slider.getMin()))) - thumbWidth/2)) : thumbLeft;
        final double  END_Y      = (HORIZONTAL) ? thumbTop : snappedTopInset() + trackLength - (trackLength * ((slider.getValue() - slider.getMin()) / (slider.getMax() - slider.getMin()))); //  - thumbHeight/2

        if (animate) {
            // lets animate the thumb transition
            final double START_X  = thumb.getLayoutX();
            final double START_Y  = thumb.getLayoutY();
            Transition transition = new Transition() {
                {
                    setCycleDuration(Duration.millis(200));
                }

                @Override protected void interpolate(double frac) {
                    if (!Double.isNaN(START_X)) {
                        thumb.setLayoutX(START_X + frac * (END_X - START_X));
                    }
                    if (!Double.isNaN(START_Y)) {
                        thumb.setLayoutY(START_Y + frac * (END_Y - START_Y));
                    }
                }
            };
            transition.play();
        } else {
            thumb.setLayoutX(END_X);
            thumb.setLayoutY(END_Y);
        }
    }

    private double minTrackLength() { return 2 * thumb.prefWidth(-1); }


    public void trackPress(MouseEvent e, double position) {
        // determine the percentage of the way between min and max
        // represented by this mouse event
        // If not already focused, request focus
        if (!slider.isFocused()) slider.requestFocus();
        if (slider.getOrientation().equals(Orientation.HORIZONTAL)) {
            slider.adjustValue(position * (slider.getMax() - slider.getMin()) + slider.getMin());
        } else {
            slider.adjustValue((1-position) * (slider.getMax() - slider.getMin()) + slider.getMin());
        }
    }

    /**
     * @param position The mouse position on track with 0.0 being beginning of
     *       track and 1.0 being the end
     */
    public void thumbPressed(MouseEvent e, double position) {
        // If not already focused, request focus
        if (!slider.isFocused())  slider.requestFocus();
        slider.setValueChanging(true);
    }

    /**
     * @param position The mouse position on track with 0.0 being beginning of
     *        track and 1.0 being the end
     */
    public void thumbDragged(MouseEvent e, double position) {
        slider.setValue(Helper.clamp(slider.getMin(), (position * (slider.getMax() - slider.getMin())) + slider.getMin(), slider.getMax()));
    }

    /**
     * When thumb is released valueChanging should be set to false.
     */
    public void thumbReleased(MouseEvent e) {
        slider.setValueChanging(false);
        // RT-15207 When snapToTicks is true, slider value calculated in drag
        // is then snapped to the nearest tick on mouse release.
        slider.adjustValue(slider.getValue());
    }


    @Override protected void layoutChildren(final double X, final double Y, final double W, final double H) {
        double value       = slider.getValue();
        double range       = Math.abs(slider.getMax() - slider.getMin());
        double trackRadius = track.getBackground() == null ? 0 : track.getBackground().getFills().size() > 0 ?
                                                                 track.getBackground().getFills().get(0).getRadii().getTopLeftHorizontalRadius() : 0;
        trackProgress.setVisible(!slider.isShowTickMarks());

        if (slider.getOrientation() == Orientation.HORIZONTAL) {
            thumbWidth               = 8;
            thumbHeight              = 20;
            thumb.resize(thumbWidth, thumbHeight);

            double tickLineHeight    =  (showTickMarks) ? tickLine.prefHeight(-1) : 0;
            double trackHeight       = snapSizeY(track.prefHeight(-1));
            double trackAreaHeight   = Math.max(trackHeight,thumbHeight);
            double totalHeightNeeded = trackAreaHeight  + ((showTickMarks) ? trackToTickGap+tickLineHeight : 0);
            double startY            = Y + ((H - totalHeightNeeded)/2);
            double trackTop          = (int)(startY + ((trackAreaHeight - trackHeight) / 2));

            trackLength              = snapSizeX(W - thumbWidth);
            trackStart               = snapPositionX(X + (thumbWidth / 2));
            thumbTop                 = (int)(startY + ((trackAreaHeight - thumbHeight) / 2));

            positionThumb(false);
            // layout track
            track.resizeRelocate((int)(trackStart - trackRadius), trackTop ,
                                 (int)(trackLength + trackRadius + trackRadius), trackHeight);

            // layout center line
            centerLine.setStartX(W * 0.5);
            centerLine.setStartY(H * 0.5 - 3);
            centerLine.setEndX(W * 0.5);
            centerLine.setEndY(H * 0.5 + 3);

            // layout trackProgress
            trackProgress.resizeRelocate((int)(trackStart - trackRadius), trackTop ,
                                         (int)((trackLength * (value / range)) + trackRadius + trackRadius), trackHeight);

            // layout tick line
            if (showTickMarks) {
                tickLine.setLayoutX(trackStart);
                tickLine.setLayoutY(snapPositionY(trackTop + trackHeight * 0.5 - tickLineHeight * 0.5 + 1));
                tickLine.resize(trackLength, tickLineHeight);
                tickLine.requestAxisLayout();
            } else {
                if (tickLine != null) {
                    tickLine.resize(0,0);
                    tickLine.requestAxisLayout();
                }
                tickLine = null;
            }
        } else {
            thumbWidth               = 20;
            thumbHeight              = 8;
            thumb.resize(thumbWidth, thumbHeight);

            double tickLineWidth    = (showTickMarks) ? tickLine.prefWidth(-1) : 0;
            double trackWidth       = snapSizeX(track.prefWidth(-1));
            double trackAreaWidth   = Math.max(trackWidth,thumbWidth);
            double totalWidthNeeded = trackAreaWidth  + ((showTickMarks) ? trackToTickGap+tickLineWidth : 0) ;
            double startX           = X + ((W - totalWidthNeeded)/2);
            double trackLeft        = (int)(startX + ((trackAreaWidth-trackWidth)/2));
            trackLength             = snapSizeY(H - thumbHeight);
            trackStart              = snapPositionY(Y + (thumbHeight/2));
            thumbLeft               = (int)(startX + ((trackAreaWidth-thumbWidth)/2));

            positionThumb(false);
            // layout track
            track.resizeRelocate(trackLeft,
                                 (int)(trackStart - trackRadius),
                                 trackWidth,
                                 (int)(trackLength + trackRadius + trackRadius));

            // layout center line
            // layout center line
            centerLine.setStartX(W * 0.5 - 3);
            centerLine.setStartY(H * 0.5);
            centerLine.setEndX(W * 0.5 + 3);
            centerLine.setEndY(H * 0.5);

            // layout trackProgress
            trackProgress.resizeRelocate(trackLeft,
                                         (int)(trackStart - trackRadius),
                                         trackWidth,
                                         (int)((trackLength * (value / range)) + trackRadius + trackRadius));

            // layout tick line
            if (showTickMarks) {
                tickLine.setLayoutX(trackLeft+trackWidth+trackToTickGap);
                tickLine.setLayoutY(trackStart);
                tickLine.resize(tickLineWidth, trackLength);
                tickLine.requestAxisLayout();
            } else {
                if (tickLine != null) {
                    tickLine.resize(0,0);
                    tickLine.requestAxisLayout();
                }
                tickLine = null;
            }
        }
    }
}
