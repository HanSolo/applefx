package eu.hansolo.applefx;

public enum MacosButtonSize {
    LARGE(16),
    NORMAL(12),
    SMALL(9);

    public final double px;

    MacosButtonSize(final double px) {
        this.px = px;
    }
}
