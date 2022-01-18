package eu.hansolo.applefx;

public enum WindowButtonSize {
    LARGE(16),
    NORMAL(12),
    SMALL(9);

    public final double px;

    WindowButtonSize(final double px) {
        this.px = px;
    }
}
