package nl.esciencecenter.xenon.cli.rename;

import nl.esciencecenter.xenon.filesystems.Path;

public class RenameOutput {
    private final Path target;
    private final Path source;
    private final String location;

    RenameOutput(String location, Path source, Path target) {
        this.location = location;
        this.source = source;
        this.target = target;
    }

    @Override
    public String toString() {
        return "Renamed '" + source + "' to '" + target + "' on location '" + location + "'";
    }
}
