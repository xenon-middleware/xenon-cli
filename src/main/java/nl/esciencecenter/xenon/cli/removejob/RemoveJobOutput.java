package nl.esciencecenter.xenon.cli.removejob;

/**
 * Result of job removal
 */
public class RemoveJobOutput {
    private final String location;
    private final String identifier;

    public RemoveJobOutput(String location, String jobId) {
        this.location = location;
        this.identifier = jobId;
    }

    @Override
    public String toString() {
        return "Removed job with identifier '" + identifier + "' from location '" + location + "'";
    }
}
