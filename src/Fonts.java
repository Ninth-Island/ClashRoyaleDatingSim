import java.awt.*;
import java.io.IOException;

public class Fonts {

    public static final Font ClashFont;
    public static final Font ClashFontLarge;
    public static final Font ClashFontMedium;
    public static final Font ClashFontSmall;
    public static final Font ClashFontTitle;

    static {
        try {
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT, Fonts.class.getResourceAsStream("/resources/Clash_Regular.otf"));
            ClashFont = baseFont.deriveFont(24f);
            ClashFontLarge = baseFont.deriveFont(32f);
            ClashFontMedium = baseFont.deriveFont(20f);
            ClashFontSmall = baseFont.deriveFont(16f);
            ClashFontTitle = baseFont.deriveFont(Font.BOLD, 36f);
        } catch (FontFormatException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
