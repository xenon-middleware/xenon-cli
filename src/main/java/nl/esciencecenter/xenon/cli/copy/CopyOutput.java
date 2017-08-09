package nl.esciencecenter.xenon.cli.copy;

/**
 * Results of a copy command
 */
public class CopyOutput {
    private final CopyInput target;
    private final CopyInput source;
    private final long bytesCopied;

    CopyOutput(CopyInput source, CopyInput target, long bytesCopied) {
        this.source = source;
        this.target = target;
        this.bytesCopied = bytesCopied;
    }

    @Override
    public String toString() {
        String sourceLocation = source.getLocation();
        if (source.isLocal() && sourceLocation == null) {
            sourceLocation = "file";
        }
        String targetLocation = target.getLocation();
        if (target.isLocal() && targetLocation == null) {
            targetLocation = "file";
        }
        return String.format("Copied '%s' from location '%s' to '%s' at location '%s', %d bytes copied", source.getPath(), sourceLocation, target.getPath(), targetLocation, bytesCopied);
    }

    public CopyInput getTarget() {
        return target;
    }

    public CopyInput getSource() {
        return source;
    }
}
