package nl.esciencecenter.xenon.cli.copy;

/**
 * Results of a copy command
 */
public class CopyOutput {
    private final CopyInput target;
    private final CopyInput source;

    public CopyOutput(CopyInput source, CopyInput target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public String toString() {
        return "Copied '" + source.getPath() + "' from location '" + source.getLocation() + "' to  '" + target.getPath() + "' to location '" + target.getLocation() + "'";
    }
}
