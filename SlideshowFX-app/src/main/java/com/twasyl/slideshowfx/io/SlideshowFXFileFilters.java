package com.twasyl.slideshowfx.io;

import java.io.FileFilter;

import static com.twasyl.slideshowfx.engine.template.TemplateEngine.DEFAULT_DOTTED_ARCHIVE_EXTENSION;

/**
 * @author Thierry Wasylczenko
 * @since SlideshowFX @@NEXT-VERSION@@
 */
public interface SlideshowFXFileFilters {

    FileFilter TEMPLATE_FILE_FILTER = (file) -> file != null && file.getName().endsWith(DEFAULT_DOTTED_ARCHIVE_EXTENSION);
}
