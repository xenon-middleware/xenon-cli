package nl.esciencecenter.xenon.cli.listfiles;

import static org.junit.Assert.assertEquals;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.XenonFactory;
import nl.esciencecenter.xenon.cli.MockedFileAttributes;
import nl.esciencecenter.xenon.files.FileAttributes;
import nl.esciencecenter.xenon.files.FileSystem;
import nl.esciencecenter.xenon.files.Files;
import nl.esciencecenter.xenon.files.Path;
import nl.esciencecenter.xenon.files.RelativePath;
import nl.esciencecenter.xenon.util.FileVisitResult;

import org.junit.Before;
import org.junit.Test;

public class ListFilesVisitorTest {
    private Xenon xenon;
    private FileSystem fs;
    private Files files;
    private Path startPath;
    private ListFilesVisitor visitor;

    @Before
    public void setUp() throws XenonException {
        xenon = XenonFactory.newXenon(null);
        files = xenon.files();
        fs = files.newFileSystem("file", null, null, null);
        startPath = files.newPath(fs, new RelativePath("/start"));
        visitor = new ListFilesVisitor(startPath, false);
    }

    @Test
    public void postVisitDirectory() throws XenonException {
        FileVisitResult result = visitor.postVisitDirectory(startPath, null, files);

        assertEquals(FileVisitResult.CONTINUE, result);
    }

    @Test
    public void preVisitDirectory_samePath() throws XenonException {
        FileAttributes attr = new MockedFileAttributes(false, true);

        FileVisitResult result = visitor.preVisitDirectory(startPath, attr, files);

        assertEquals(FileVisitResult.CONTINUE, result);
        ListFilesOutput expectedListing = new ListFilesOutput();
        assertEquals(expectedListing, visitor.getListing());
    }


    @Test
    public void preVisitDirectory_somePath_inListing() throws XenonException {
        FileAttributes attr = new MockedFileAttributes(false, true);
        Path somePath = files.newPath(fs, new RelativePath("/start/foo"));

        FileVisitResult result = visitor.preVisitDirectory(somePath, attr, files);

        assertEquals(FileVisitResult.CONTINUE, result);
        ListFilesOutput expectedListing = new ListFilesOutput();
        expectedListing.addDirectory("foo");
        assertEquals(expectedListing, visitor.getListing());
    }

    @Test
    public void preVisitDirectory_hiddenPath_terminate() throws XenonException {
        FileAttributes attr = new MockedFileAttributes(true, true);
        Path somePath = files.newPath(fs, new RelativePath("/start/foo"));

        FileVisitResult result = visitor.preVisitDirectory(somePath, attr, files);

        assertEquals(FileVisitResult.TERMINATE, result);
        ListFilesOutput expectedListing = new ListFilesOutput();
        assertEquals(expectedListing, visitor.getListing());
    }

    @Test
    public void visitFileFailed() throws XenonException {
        FileVisitResult result = visitor.visitFileFailed(startPath, null, files);

        assertEquals(FileVisitResult.CONTINUE, result);
    }
}