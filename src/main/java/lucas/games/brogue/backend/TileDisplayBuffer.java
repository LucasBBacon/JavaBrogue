package lucas.games.brogue.backend;

public final class TileDisplayBuffer {
    private DisplayGlyph glyph;
    private BrogueColor foregroundColor;
    private BrogueColor backgroundColor;
    private float opacity;

    public TileDisplayBuffer(DisplayGlyph glyph,
                             BrogueColor foregroundColor,
                             BrogueColor backgroundColor,
                             float opacity) {
        this.glyph = glyph;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.opacity = opacity;
    }

    public DisplayGlyph getGlyph() { return glyph; }
    public BrogueColor getForegroundColor() { return foregroundColor; }
    public BrogueColor getBackgroundColor() { return backgroundColor; }
    public float getOpacity() { return opacity; }

    public void setGlyph(DisplayGlyph glyph) { this.glyph = glyph; }
    public void setForegroundColor(BrogueColor foregroundColor) { this.foregroundColor = foregroundColor; }
    public void setBackgroundColor(BrogueColor backgroundColor) { this.backgroundColor = backgroundColor; }
    public void setOpacity(float opacity) { this.opacity = opacity; }
}
