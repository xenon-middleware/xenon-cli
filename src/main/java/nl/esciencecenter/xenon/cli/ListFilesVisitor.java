package nl.esciencecenter.xenon.cli;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.files.FileAttributes;
import nl.esciencecenter.xenon.files.Files;
import nl.esciencecenter.xenon.files.Path;
import nl.esciencecenter.xenon.files.RelativePath;
import nl.esciencecenter.xenon.util.FileVisitResult;
import nl.esciencecenter.xenon.util.FileVisitor;

public class ListFilesVisitor implements FileVisitor {
    private final RelativePath relPath;
    private final Boolean hidden;
    private ListFilesOutput listing;
    private Path path;

    public ListFilesVisitor(Path path, Boolean hidden) {
        this.listing = new ListFilesOutput();
        this.path = path;
        this.relPath = path.getRelativePath();
        this.hidden = hidden;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, XenonException exception, Files files) throws XenonException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, FileAttributes attributes, Files files) throws XenonException {
        if (dir.equals(path)) {
            return FileVisitResult.CONTINUE;
        }
        if (!hidden && attributes.isHidden()) {
            return FileVisitResult.TERMINATE;
        }
        String filename = relPath.relativize(dir.getRelativePath()).getRelativePath();
        listing.addDirectory(filename);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, FileAttributes attributes, Files files) throws XenonException {
        String filename = relPath.relativize(file.getRelativePath()).getRelativePath();
        if (!hidden && attributes.isHidden()) {
            return FileVisitResult.CONTINUE;
        }
        if (attributes.isDirectory()) {
            listing.addDirectory(filename);
        } else {
            listing.addFile(filename);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, XenonException exception, Files files) throws XenonException {
        // TODO now failed files are ignored or should it die
        return FileVisitResult.CONTINUE;
    }

    public ListFilesOutput getListing() {
        return listing;
    }
}
