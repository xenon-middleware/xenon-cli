package nl.esciencecenter.xenon.cli.createdir;

import nl.esciencecenter.xenon.filesystems.Path;

public class CreateDirectoryOutput {
    private final String location;
    private final Path dir;

    CreateDirectoryOutput(String location, Path dir) {
        this.location = location;
        this.dir = dir;
    }
    @Override
    public String toString() {
        return "Created directory '" + dir + "' in location '" + location + "'";
    }
}
