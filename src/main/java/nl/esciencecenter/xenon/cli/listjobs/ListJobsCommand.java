package nl.esciencecenter.xenon.cli.listjobs;

import static nl.esciencecenter.xenon.cli.Main.buildXenonProperties;
import static nl.esciencecenter.xenon.cli.ParserHelpers.getAllowedXenonPropertyKeys;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.XenonPropertyDescription;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.jobs.Job;
import nl.esciencecenter.xenon.jobs.Jobs;
import nl.esciencecenter.xenon.jobs.Scheduler;

import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Command to list jobs of scheduler
 */
public class ListJobsCommand extends XenonCommand {
    @Override
    public ListJobsOutput run(Namespace res, Xenon xenon) throws XenonException {
        String scheme = res.getString("scheme");
        String location = res.getString("location");
        String queue = res.getString("queue");
        Credential credential = buildCredential(res, xenon);

        Jobs jobs = xenon.jobs();
        Set<String> allowedKeys = getAllowedXenonPropertyKeys(xenon, scheme, XenonPropertyDescription.Component.SCHEDULER);
        Map<String, String> props = buildXenonProperties(res, allowedKeys);
        Scheduler scheduler = jobs.newScheduler(scheme, location, credential, props);

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
