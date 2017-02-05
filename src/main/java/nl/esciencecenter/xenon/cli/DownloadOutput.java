package nl.esciencecenter.xenon.cli;

public class DownloadOutput {

    private final String location;
    private final String path;
    private final String target;

    public DownloadOutput(CopyInput source, CopyInput target) {
        this.location = source.location;
        this.path = source.path;
        this.target = target.path;
    }

    @Override
    public String toString() {
        return "Downloaded '" + path + "' from location '" + location + "' to  '" + target + "'";
    }
}
