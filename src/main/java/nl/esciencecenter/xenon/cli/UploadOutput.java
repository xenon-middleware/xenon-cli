package nl.esciencecenter.xenon.cli;

public class UploadOutput {
    public String source;
    public String location;
    public String path;

    public UploadOutput(CopyInput source, CopyInput target) {
        this.source = source.path;
        location = target.location;
        path = target.path;
    }

    @Override
    public String toString() {
        return "Uploaded '" + source + "' to path '" + path + "' at location '" + location + "'";
    }
}
