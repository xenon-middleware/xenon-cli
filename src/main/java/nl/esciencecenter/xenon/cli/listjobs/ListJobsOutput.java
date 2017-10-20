package nl.esciencecenter.xenon.cli.listjobs;

import java.util.List;
import java.util.Objects;

import nl.esciencecenter.xenon.schedulers.JobStatus;

/**
 * Listing of jobs
 */
public class ListJobsOutput {
    public final List<JobStatus> statuses;


    public ListJobsOutput(List<JobStatus> statuses) {
        this.statuses = statuses;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String sep = "\t";
        String lsep = System.getProperty("line.separator");
        sb.append(String.join(
            sep,
            "Job identifier",
            "State", "Running",
            "Done", "Error",
            "Exit code",
            "Information"
        )).append(lsep);
        for (JobStatus status: statuses) {
            sb.append(String.join(
                sep,
                status.getJobIdentifier(),
                status.getState(),
                String.valueOf(status.isRunning()),
                String.valueOf(status.isDone()),
                status.hasException() ? status.getException().getMessage() : "",
                status.getExitCode() != null ? status.getExitCode().toString() : "",
                status.getSchedulerSpecificInformation() != null ? status.getSchedulerSpecificInformation().toString() : ""
            )).append(lsep);
        }
        return sb.toString();
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
        return Objects.hash(statuses);
    }
}
