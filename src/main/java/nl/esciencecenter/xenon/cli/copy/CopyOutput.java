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
        String sourceLocation = source.getLocation();
        if (source.isLocal() && sourceLocation == null) {
            sourceLocation = "local";
        }
        String targetLocation = target.getLocation();
        if (target.isLocal() && targetLocation == null) {
            targetLocation = "local";
        }
        return "Copied '" + source.getPath() + "' from location '" + sourceLocation + "' to  '" + target.getPath() + "' to location '" + targetLocation + "'";
    }
}
