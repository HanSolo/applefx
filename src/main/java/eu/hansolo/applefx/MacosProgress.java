package eu.hansolo.applefx;

import eu.hansolo.applefx.tools.Helper;
import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;


public class MacosProgress extends Region implements MacosControl {
    private static final double                                  PREFERRED_WIDTH        = 16;
    private static final double                                  PREFERRED_HEIGHT       = 16;
    private static final double                                  MINIMUM_WIDTH          = 5;
    private static final double                                  MINIMUM_HEIGHT         = 5;
    private static final double                                  MAXIMUM_WIDTH          = 1024;
    private static final double                                  MAXIMUM_HEIGHT         = 1024;
    private static final StyleablePropertyFactory<MacosProgress> FACTORY                = new StyleablePropertyFactory<>(Region.getClassCssMetaData());
    private static final PseudoClass                             DARK_PSEUDO_CLASS      = PseudoClass.getPseudoClass("dark");
    private static final CssMetaData<MacosProgress, Color>       PROGRESS_STROKE        = FACTORY.createColorCssMetaData("-progress-stroke", s -> s.progressStroke, Color.rgb(119, 119, 119), false);
    private static final CssMetaData<MacosProgress, Color>       PROGRESS_FILL          = FACTORY.createColorCssMetaData("-progress-fill", s -> s.progressFill, Color.rgb(119, 119, 119), false);
    private static final Color[]                                 PILL_COLORS            = { Color.rgb(0, 0, 0), Color.rgb(21, 21, 21), Color.rgb(42, 42, 42), Color.rgb(63, 63, 63),
                                                                                            Color.rgb(84, 84, 84), Color.rgb(105, 105, 105), Color.rgb(126, 126, 126), Color.rgb(147, 147, 147),
                                                                                            Color.rgb(168, 168, 168), Color.rgb(189, 189, 189), Color.rgb(210, 210, 210), Color.rgb(231, 231, 231), Color.rgb(252, 252, 252) };
    private static final Color[]                                 PILL_COLORS_DARK       = { Color.rgb(252, 252, 252), Color.rgb(231, 231, 231), Color.rgb(210, 210, 210), Color.rgb(189, 189, 189), Color.rgb(168, 168, 168),
                                                                                            Color.rgb(147, 147, 147), Color.rgb(126, 126, 126), Color.rgb(105, 105, 105), Color.rgb(84, 84, 84),
                                                                                            Color.rgb(63, 63, 63), Color.rgb(42, 42, 42), Color.rgb(21, 21, 21), Color.rgb(0, 0, 0) };
    private              boolean                                 _dark;
    private              BooleanProperty                         dark;
    private        final StyleableProperty<Color>                progressStroke;
    private        final StyleableProperty<Color>                progressFill;
    private              boolean                                 _indeterminate;
    private              BooleanProperty                         indeterminate;
    private              String                                  userAgentStyleSheet;
    private              double                                  size;
    private              double                                  width;
    private              double                                  height;
    private              Canvas                                  canvas;
    private              GraphicsContext                         ctx;
    private              Pane                                    pane;
    private              double                                  lineWidth;
    private              double                                  halfLineWidth;
    private              double                                  _progress;
    private              DoubleProperty                          progress;
    private              long                                    lastTimerCall;
    private              AnimationTimer                          timer;
    private              int                                     pillFillOffset;


    // ******************** Constructors **************************************
    public MacosProgress() {
        this(0);
    }
    public MacosProgress(final double progress) {
        this._dark          = false;
        this.progressStroke = new StyleableObjectProperty<>(PROGRESS_STROKE.getInitialValue(MacosProgress.this)) {
            @Override protected void invalidated() { redraw(); }
            @Override public Object getBean() { return MacosProgress.this; }
            @Override public String getName() { return "progressStroke"; }
            @Override public CssMetaData<? extends Styleable, Color> getCssMetaData() { return PROGRESS_STROKE; }
        };
        this.progressFill   = new StyleableObjectProperty<>(PROGRESS_FILL.getInitialValue(MacosProgress.this)) {
            @Override protected void invalidated() { redraw(); }
            @Override public Object getBean() { return MacosProgress.this; }
            @Override public String getName() { return "progressFill"; }
            @Override public CssMetaData<? extends Styleable, Color> getCssMetaData() { return PROGRESS_FILL; }
        };
        this._indeterminate = false;
        this._progress      = Helper.clamp(0.0, 1.0, progress);
        this.lastTimerCall  = System.nanoTime();
        this.timer          = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + 66_660_000l) {
                    redraw();
                    lastTimerCall = now;
                }
            }
        };
        this.pillFillOffset = 0;
        this.lineWidth      = 1;
        this.halfLineWidth  = 0.5;
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

        getStyleClass().add("macos-progress");

        canvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        ctx    = canvas.getGraphicsContext2D();

        pane = new Pane(canvas);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double height) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double width) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double height) { return super.computePrefWidth(height); }
    @Override protected double computePrefHeight(final double width) { return super.computePrefHeight(width); }
    @Override protected double computeMaxWidth(final double height) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double width) { return MAXIMUM_HEIGHT; }

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
                @Override public Object getBean() { return MacosProgress.this; }
                @Override public String getName() { return "dark"; }
            };
        }
        return dark;
    }

    public Color getProgressStroke() { return progressStroke.getValue(); }
    public void setProgressStroke(final Color color) { progressStroke.setValue(color); }
    public ObjectProperty<Color> progressStrokeProperty() { return (ObjectProperty<Color>) progressStroke; }

    public Color getProgressFill() { return progressFill.getValue(); }
    public void setProgressFill(final Color color) { progressFill.setValue(color); }
    public ObjectProperty<Color> progressFillProperty() { return (ObjectProperty<Color>) progressFill; }

    public double getProgress() { return null == progress ? _progress : progress.get(); }
    public void setProgress(final double progress) {
        if (null == this.progress) {
            _progress = Helper.clamp(0.0, 1.0, progress);
            redraw();
        } else {
            this.progress.set(Helper.clamp(0.0, 1.0, progress));
        }
    }
    public DoubleProperty progressProperty() {
        if (null == progress) {
            progress = new DoublePropertyBase(_progress) {
                @Override protected void invalidated() {
                    if (isBound()) {
                        if (get() > 1 || get() < 0) { throw new IllegalArgumentException("Value cannot be smaller than 0 or larger than 1"); }
                    } else {
                        set(Helper.clamp(0.0, 1.0, get()));
                    }
                    redraw();
                }
                @Override public Object getBean() { return MacosProgress.this; }
                @Override public String getName() { return "progress"; }
            };
        }
        return progress;
    }

    public boolean isIndeterminate() { return null == indeterminate ? _indeterminate : indeterminate.get(); }
    public void setIndeterminate(final boolean indeterminate) {
        if (indeterminate) {
            timer.start();
        } else {
            timer.stop();
        }
        if (null == this.indeterminate) {
            _indeterminate = indeterminate;
            redraw();
        } else {
            this.indeterminate.set(indeterminate);
        }
    }
    public BooleanProperty indeterminateProperty() {
        if (null == indeterminate) {
            indeterminate = new BooleanPropertyBase(_indeterminate) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return MacosProgress.this; }
                @Override public String getName() { return "indeterminate"; }
            };
        }
        return indeterminate;
    }


    // ******************** Layout *******************************************
    @Override public String getUserAgentStylesheet() {
        if (null == userAgentStyleSheet) { userAgentStyleSheet = MacosProgress.class.getResource("apple.css").toExternalForm(); }
        return userAgentStyleSheet;
    }

    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;


        if (width > 0 && height > 0) {
            pane.setMaxSize(size, size);
            pane.setPrefSize(size, size);
            pane.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

            canvas.setWidth(width);
            canvas.setHeight(height);

            lineWidth     = Helper.clamp(1, 10, size * 0.0625);
            halfLineWidth = lineWidth * 0.5;

            if (!isIndeterminate()) { redraw(); }
        }
    }

    private void redraw() {
        ctx.clearRect(0, 0, width, height);
        if (isIndeterminate()) {
            double pillWidth  = size * 0.125;
            double pillHeight = size * 0.28125;
            ctx.save();
            for (int i = 0 ; i < 12 ; i++) {
                int pillFillIndex = pillFillOffset + i;
                if (pillFillIndex > 11) { pillFillIndex -= 11; }
                ctx.setFill(isDark() ? PILL_COLORS_DARK[pillFillIndex] : PILL_COLORS[pillFillIndex]);
                ctx.fillRoundRect((size - pillWidth) * 0.5, 0, pillWidth, pillHeight,pillWidth, pillWidth);
                ctx.translate(size * 0.5, size * 0.5);
                ctx.rotate(-30);
                ctx.translate(-size * 0.5, -size * 0.5);
            }
            pillFillOffset++;
            if (pillFillOffset > 11) { pillFillOffset = 0; }
            ctx.restore();
        } else {
            ctx.setFill(getProgressFill());
            ctx.setStroke(getProgressStroke());
            ctx.setLineWidth(lineWidth);
            ctx.strokeOval(halfLineWidth, halfLineWidth, size - lineWidth, size - lineWidth);
            ctx.fillArc(halfLineWidth, halfLineWidth, size - lineWidth, size - lineWidth, 90, -360.0 * getProgress(), ArcType.ROUND);
        }
    }
}

