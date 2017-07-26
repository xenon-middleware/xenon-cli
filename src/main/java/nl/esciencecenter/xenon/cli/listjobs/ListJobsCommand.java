package nl.esciencecenter.xenon.cli.listjobs;

import static nl.esciencecenter.xenon.cli.Main.buildXenonProperties;
import static nl.esciencecenter.xenon.cli.ParserHelpers.getAllowedSchedulerPropertyKeys;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.schedulers.Scheduler;

import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Command to list jobs of scheduler
 */
public class ListJobsCommand extends XenonCommand {
    @Override
    public ListJobsOutput run(Namespace res) throws XenonException {
        String adaptor = res.getString("adaptor");
        String location = res.getString("location");
        String queue = res.getString("queue");
        Credential credential = buildCredential(res);

        Set<String> allowedKeys = getAllowedSchedulerPropertyKeys(adaptor);
        Map<String, String> props = buildXenonProperties(res, allowedKeys);
        Scheduler scheduler = Scheduler.create(adaptor, location, credential, props);

        String[] jobIdentifiers;
        if (queue == null) {
            jobIdentifiers = scheduler.getJobs();
        } else {
            jobIdentifiers = scheduler.getJobs(queue);
        }
        // TODO Slurm adaptor returns job with id="" when there are no jobs, adaptor should be fixed instead of ignoring empty strings here

        scheduler.close();

        return new ListJobsOutput(location, queue, Arrays.asList(jobIdentifiers));
    }
}
