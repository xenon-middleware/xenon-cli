package nl.esciencecenter.xenon.cli;

import nl.esciencecenter.xenon.jobs.JobDescription;

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
