package com.twasyl.slideshowfx.utils.io;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

import static org.junit.Assert.*;

/**
 * Tests the class {@link ListFilesFileVisitor}
 *
 * @author Thierry Wasylczenko
 * @version 1.1
 * @since SlideshowFX 1.0
 */
public class ListFilesFileVisitorTest {

    private static File baseLocation;
    private ListFilesFileVisitor visitor;

    @BeforeClass
    public static void setUp() throws URISyntaxException {
        // Hack to get the resources folder
        final File file = new File(ListFilesFileVisitorTest.class.getResource("/com/twasyl/slideshowfx/utils/io/file.txt").toURI());
        baseLocation = file.getParentFile();

        // Create an empty directory as an empty dir can not be added to the VCS.
        new File(baseLocation, "emptyDir").mkdir();
        new File(baseLocation, "dir/dir2").mkdirs();
    }

    @Before
    public void beforeTest() {
        this.visitor = new ListFilesFileVisitor();
    }

    @Test
    public void testWalkOnExistingFile() throws IOException {
        final File file = new File(baseLocation, "file.txt");
        Files.walkFileTree(file.toPath(), visitor);

        assertFalse(visitor.getPaths().isEmpty());
        assertEquals(file.toPath(), visitor.getPaths().get(0));
    }

    @Test(expected = NoSuchFileException.class)
    public void testWalkOnInvalidFile() throws IOException {
        final File file = new File(baseLocation, "this_file_dont_exist.txt");
        Files.walkFileTree(file.toPath(), visitor);
    }

    @Test
    public void testOnEmptyDir() throws IOException {
        final File emptyDir = new File(baseLocation, "emptyDir");
        Files.walkFileTree(emptyDir.toPath(), visitor);

        assertFalse(visitor.getPaths().isEmpty());
        assertEquals(emptyDir.toPath(), visitor.getPaths().get(0));
    }

    @Test
    public void testOnFileTree() throws IOException {
        final File emptyDir = baseLocation;
        Files.walkFileTree(emptyDir.toPath(), visitor);

        assertEquals(5, visitor.getPaths().size());
        assertTrue(visitor.getPaths().contains(new File(baseLocation, "dir/dir2").toPath()));
        assertTrue(visitor.getPaths().contains(new File(baseLocation, "dir/dir3/file2.txt").toPath()));
        assertTrue(visitor.getPaths().contains(new File(baseLocation, "dir/file1.txt").toPath()));
        assertTrue(visitor.getPaths().contains(new File(baseLocation, "emptyDir").toPath()));
        assertTrue(visitor.getPaths().contains(new File(baseLocation, "file.txt").toPath()));
    }
}
