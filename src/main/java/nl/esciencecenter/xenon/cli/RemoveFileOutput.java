package nl.esciencecenter.xenon.cli;

public class RemoveFileOutput {
    private final String location;
    private final String path;

    public RemoveFileOutput(String location, String path) {
        this.location = location;
        this.path = path;
    }

    @Override
    public String toString() {
        return "Removed '" + path + "' from location '" + location + "'";
    }
}
