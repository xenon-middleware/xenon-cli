package nl.esciencecenter.xenon.cli;

import java.util.Arrays;
import java.util.Optional;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.jobs.Job;
import nl.esciencecenter.xenon.jobs.Jobs;
import nl.esciencecenter.xenon.jobs.NoSuchJobException;
import nl.esciencecenter.xenon.jobs.Scheduler;

import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class RemoveJobCommand extends XenonCommand {
    @Override
    public Subparser buildArgumentParser(Subparsers subparsers) {
        Subparser subparser = subparsers.addParser("remove")
            .setDefault("command", this)
            .help("Remove job from scheduler")
            .description("Remove job from scheduler");
        subparser.addArgument("job-identifier").help("Job identifier").required(true);
        return subparser;
    }

    @Override
    public void run(Namespace res, Xenon xenon) throws XenonException {
        String scheme = res.getString("scheme");
        String location = res.getString("location");
        String jobId = res.getString("job_identifier");
        Credential credential = buildCredential(res, xenon);

        Jobs jobs = xenon.jobs();
        Scheduler scheduler = jobs.newScheduler(scheme, location, credential, null);
        Job[] scheduledJobs = jobs.getJobs(scheduler);
        Optional<Job> job = Arrays.stream(scheduledJobs).filter((j) -> j.getIdentifier().equals(jobId)).findFirst();
        if (job.isPresent()) {
            jobs.cancelJob(job.get());
        } else {
           throw new NoSuchJobException(scheme, "Job with identifier '" + jobId + "' not found");
        }
        jobs.close(scheduler);

        RemoveJobOutput output = new RemoveJobOutput(location, jobId);
        String format = res.getString("format");
        this.print(output, format);
    }
}
