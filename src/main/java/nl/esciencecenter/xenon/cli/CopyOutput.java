package nl.esciencecenter.xenon.cli;

public class CopyOutput {
    private final CopyInput target;
    private final CopyInput source;

    public CopyOutput(CopyInput source, CopyInput target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public String toString() {
        return "Copied '" + source.path + "' from location '" + source.location + "' to  '" + target.path + "' to location '" + target.location + "'";
    }
}
