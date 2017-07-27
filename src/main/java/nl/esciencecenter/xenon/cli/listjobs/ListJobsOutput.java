package nl.esciencecenter.xenon.cli.listjobs;

import nl.esciencecenter.xenon.schedulers.JobStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Listing of jobs
 */
public class ListJobsOutput {
    public final JobStatus[] statuses;


    public ListJobsOutput(JobStatus[] statuses) {
        this.statuses = statuses;
    }

    @Override
    public String toString() {
        String sep = System.getProperty("line.separator");
        return String.join(sep, Arrays.stream(statuses).toString()) + sep;
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
        return Objects.equals(statuses, that.statuses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.asList(statuses));
    }
}
