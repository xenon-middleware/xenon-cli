package nl.esciencecenter.xenon.cli;

import java.util.List;

public class ListJobsOutput {
    private final String location;
    private final String queue;
    private final List<String> jobs;

    public ListJobsOutput(String location, String queue, List<String> jobIdentifiers) {
        this.location = location;
        this.queue = queue;
        this.jobs = jobIdentifiers;
    }

    @Override
    public String toString() {
        String sep = System.getProperty("line.separator");
        return String.join(sep, jobs);
    }
}
