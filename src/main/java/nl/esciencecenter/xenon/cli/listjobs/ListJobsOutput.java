package nl.esciencecenter.xenon.cli.listjobs;

import java.util.List;
import java.util.Objects;

/**
 * Listing of jobs
 */
public class ListJobsOutput {
    private final String location;
    private final String queue;
    public final List<String> jobs;

    ListJobsOutput(String location, String queue, List<String> jobIdentifiers) {
        this.location = location;
        this.queue = queue;
        this.jobs = jobIdentifiers;
    }

    @Override
    public String toString() {
        String sep = System.getProperty("line.separator");
        return String.join(sep, jobs) + sep;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }    
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ListJobsOutput that = (ListJobsOutput) o;
        return Objects.equals(location, that.location) &&
                Objects.equals(queue, that.queue) &&
                Objects.equals(jobs, that.jobs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, queue, jobs);
    }
}
