package com.twasyl.slideshowfx.icons;

/**
 * @author Thierry Wasylczenko
 * @since SlideshowFX 2.0
 */
public enum Icon {
    ARCHIVE("\uf187"),
    ARROW_RIGHT("\uf061"),
    CHECK_CIRCLE("\uf058"),
    CHEVRON_LEFT("\uf053"),
    CHEVRON_RIGHT("\uf054"),
    CODE("\uf121"),
    COG("\uf013"),
    COMMENTS_O("\uf0E6"),
    DESKTOP("\uf108"),
    EXCLAMATION_CIRCLE("\uf06a"),
    EXCLAMATION_TRIANGLE("\uf071"),
    FILE("\uf15b"),
    FILE_TEXT_ALT("\uf0f6"),
    FILES("\uf0c5"),
    FLOPPY("\uf0c7"),
    FOLDER("\uf07b"),
    FOLDER_OPEN("\uf07c"),
    LINK("\uf0c1"),
    REFRESH("\uf021"),
    PICTURE_ALT("\uf03e"),
    PLAY("\uf04b"),
    PLUS("\uf067"),
    PLUS_SQUARE("\uf0fe"),
    POWER_OFF("\uf011"),
    PRINT("\uf02f"),
    QRCODE("\uf029"),
    QUESTION("\uf128"),
    QUOTE_LEFT("\uf10d"),
    SHARE_ALT_SQUARE("\uf1e1"),
    SPINNER("\uf110"),
    STAR_HALF_ALT("\uf123"),
    TERMINAL("\uf120"),
    TIMES("\uf00d"),
    TIMES_CIRCLE("\uf057"),
    TRASH_ALT("\uf014"),
    TWITTER("\uf099");

    private final String unicode;

    Icon(final String unicode) {
        this.unicode = unicode;
    }

    public String getUnicode() {
        return unicode;
    }
}
