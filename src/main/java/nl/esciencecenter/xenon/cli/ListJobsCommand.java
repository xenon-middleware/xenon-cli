package nl.esciencecenter.xenon.cli;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.jobs.Job;
import nl.esciencecenter.xenon.jobs.Jobs;
import nl.esciencecenter.xenon.jobs.Scheduler;

import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class ListJobsCommand extends XenonCommand {
    @Override
    public Subparser buildArgumentParser(Subparsers subparsers) {
        Subparser subparser = subparsers.addParser("list")
            .setDefault("command", this)
            .help("List jobs of scheduler")
            .description("List jobs of scheduler");
        subparser.addArgument("--queue").help("Only list jobs belonging to this queue");
        return subparser;
    }

    @Override
    public ListJobsOutput run(Namespace res, Xenon xenon) throws XenonException {
        String scheme = res.getString("scheme");
        String location = res.getString("location");
        String queue = res.getString("queue");
        Credential credential = buildCredential(res, xenon);

        Jobs jobs = xenon.jobs();
        Scheduler scheduler = jobs.newScheduler(scheme, location, credential, null);

        Job[] scheduledJobs;
        if (queue == null) {
            scheduledJobs = jobs.getJobs(scheduler);
        } else {
            scheduledJobs = jobs.getJobs(scheduler, queue);
        }
        // TODO Slurm adaptor returns job with id="" when there are no jobs, adaptor should be fixed instead of ignoring empty strings here
        List<String> jobIdentifiers = Arrays.stream(scheduledJobs).filter(job -> !job.getIdentifier().isEmpty()).map(Job::getIdentifier).collect(Collectors.toList());
        jobs.close(scheduler);

        return new ListJobsOutput(location, queue, jobIdentifiers);
    }
}
