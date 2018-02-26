package nl.esciencecenter.xenon.cli.listjobs;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.adaptors.schedulers.JobStatusImplementation;
import nl.esciencecenter.xenon.schedulers.JobStatus;
import nl.esciencecenter.xenon.schedulers.NoSuchJobException;

public class ListJobsOutputTest {
    @Test
    public void test_equal_emptyList() {
        List<JobStatus> jobs1 = Collections.emptyList();
        ListJobsOutput list1 = new ListJobsOutput(jobs1);
        List<JobStatus> jobs2 = Collections.emptyList();
        ListJobsOutput list2 = new ListJobsOutput(jobs2);
        assertEquals(list1, list2);
    }

    @Test
    public void test_toString_emptyList() {
        List<JobStatus> jobs1 = Collections.emptyList();
        ListJobsOutput list1 = new ListJobsOutput(jobs1);

        String sep = System.getProperty("line.separator");
        String expected = "Job identifier\tName\tState\tRunning\tDone\tError\tExit code\tInformation" + sep;
        assertEquals(expected, list1.toString());
    }

    @Test
    public void test_toString_pendingStatus() {
        JobStatus status = new JobStatusImplementation("1234", null, "PENDING", null, null, false, false, null);
        List<JobStatus> statuses = Collections.singletonList(status);
        ListJobsOutput jobsOutput = new ListJobsOutput(statuses);

        String sep = System.getProperty("line.separator");
        String expected = "Job identifier\tName\tState\tRunning\tDone\tError\tExit code\tInformation" + sep + "1234\t\tPENDING\tfalse\tfalse\t\t\t" + sep;
        assertEquals(expected, jobsOutput.toString());
    }

    @Test
    public void test_toString_doneStatus() {
        JobStatus status = new JobStatusImplementation("1234", null,"DONE", 0, null, false, true, null);
        List<JobStatus> statuses = Collections.singletonList(status);
        ListJobsOutput jobsOutput = new ListJobsOutput(statuses);

        String sep = System.getProperty("line.separator");
        String expected = "Job identifier\tName\tState\tRunning\tDone\tError\tExit code\tInformation" + sep + "1234\t\tDONE\tfalse\ttrue\t\t0\t" + sep;
        assertEquals(expected, jobsOutput.toString());
    }

    @Test
    public void test_toString_unknownStatus() {
        JobStatus status = new JobStatusImplementation("1234", null,"UNKNOWN", null, new NoSuchJobException("slurm", "Job identifier not found"), false, false, null);
        List<JobStatus> statuses = Collections.singletonList(status);
        ListJobsOutput jobsOutput = new ListJobsOutput(statuses);

        String sep = System.getProperty("line.separator");
        String expected = "Job identifier\tName\tState\tRunning\tDone\tError\tExit code\tInformation" + sep + "1234\t\tUNKNOWN\tfalse\tfalse\tslurm adaptor: Job identifier not found\t\t" + sep;
        assertEquals(expected, jobsOutput.toString());
    }

    @Test
    public void test_toString_errorStatus() {
        JobStatus status = new JobStatusImplementation("1234", null,"ERROR", null, new XenonException("slurm", "sacct returned jibberish"), false, false, null);
        List<JobStatus> statuses = Collections.singletonList(status);
        ListJobsOutput jobsOutput = new ListJobsOutput(statuses);

        String sep = System.getProperty("line.separator");
        String expected = "Job identifier\tName\tState\tRunning\tDone\tError\tExit code\tInformation" + sep + "1234\t\tERROR\tfalse\tfalse\tslurm adaptor: sacct returned jibberish\t\t" + sep;
        assertEquals(expected, jobsOutput.toString());
    }

    @Test
    public void test_toString_pendingStatusWithName() {
        JobStatus status = new JobStatusImplementation("1234", "myjobname", "PENDING", null, null, false, false, null);
        List<JobStatus> statuses = Collections.singletonList(status);
        ListJobsOutput jobsOutput = new ListJobsOutput(statuses);

        String sep = System.getProperty("line.separator");
        String expected = "Job identifier\tName\tState\tRunning\tDone\tError\tExit code\tInformation" + sep + "1234\tmyjobname\tPENDING\tfalse\tfalse\t\t\t" + sep;
        assertEquals(expected, jobsOutput.toString());
    }
}
