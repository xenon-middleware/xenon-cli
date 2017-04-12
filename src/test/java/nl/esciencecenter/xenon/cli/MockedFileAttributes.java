package nl.esciencecenter.xenon.cli;

import java.util.Set;

import nl.esciencecenter.xenon.files.AttributeNotSupportedException;
import nl.esciencecenter.xenon.files.FileAttributes;
import nl.esciencecenter.xenon.files.PosixFilePermission;

public class MockedFileAttributes implements FileAttributes {
    private final boolean hidden;
    private final boolean directory;

    public MockedFileAttributes(boolean hidden, boolean directory) {
        this.hidden = hidden;
        this.directory = directory;
    }

    @Override
    public boolean isDirectory() {
        return directory;
    }

    @Override
    public boolean isOther() {
        return false;
    }

    @Override
    public boolean isRegularFile() {
        return false;
    }

    @Override
    public boolean isSymbolicLink() {
        return false;
    }

    @Override
    public long creationTime() {
        return 0;
    }

    @Override
    public long lastAccessTime() {
        return 0;
    }

    @Override
    public long lastModifiedTime() {
        return 0;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public boolean isExecutable() {
        return false;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public boolean isReadable() {
        return false;
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public String group() throws AttributeNotSupportedException {
        return null;
    }

    @Override
    public String owner() throws AttributeNotSupportedException {
        return null;
    }

    @Override
    public Set<PosixFilePermission> permissions() throws AttributeNotSupportedException {
        return null;
    }
}
