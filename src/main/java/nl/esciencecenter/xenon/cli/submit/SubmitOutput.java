package nl.esciencecenter.xenon.cli.submit;

import nl.esciencecenter.xenon.schedulers.JobDescription;

/**
 * Result of a job submission
 */
public class SubmitOutput {
    public final String location;
    public final JobDescription description;
    public final String jobId;

    public SubmitOutput(String location, JobDescription description, String jobId) {
        this.location = location;
        this.description = description;
        this.jobId = jobId;
    }

    @Override
    public String toString() {
        return "Submitted to location " +
            "'" + location + '\'' +
            ", description '" + description +
            "', scheduled with job identifier '" + jobId + '\'';
    }
}
