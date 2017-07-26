package nl.esciencecenter.xenon.cli.listjobs;

import static nl.esciencecenter.xenon.cli.Main.buildXenonProperties;
import static nl.esciencecenter.xenon.cli.ParserHelpers.getAllowedSchedulerPropertyKeys;
import static nl.esciencecenter.xenon.cli.Utils.createScheduler;

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
        Scheduler scheduler = createScheduler(res);
        String queue = res.getString("queue");

        String[] jobIdentifiers;
        if (queue == null) {
            jobIdentifiers = scheduler.getJobs();
        } else {
            jobIdentifiers = scheduler.getJobs(queue);
        }

        scheduler.close();

        return new ListJobsOutput(scheduler.getLocation(), queue, Arrays.asList(jobIdentifiers));
    }
}
