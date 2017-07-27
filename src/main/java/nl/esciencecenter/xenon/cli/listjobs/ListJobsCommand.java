package nl.esciencecenter.xenon.cli.listjobs;

import static nl.esciencecenter.xenon.cli.Utils.createScheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.schedulers.JobStatus;
import nl.esciencecenter.xenon.schedulers.Scheduler;

import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Command to list jobs of scheduler
 */
public class ListJobsCommand extends XenonCommand {
    @Override
    public ListJobsOutput run(Namespace res) throws XenonException {
        Scheduler scheduler = createScheduler(res);
        List<String> queues = res.getList("queue");
        List<String> identifiers = res.getList("identifier");

        if (identifiers == null) {
            if (queues == null) {
                queues = new ArrayList<>();
            }
            // TODO getJobs sometimes returns empty strings, they are filtered out, but should be fixed in Xenon
            identifiers = Arrays.stream(scheduler.getJobs(queues.toArray(new String[0]))).filter(c -> !"".equals(c)).collect(Collectors.toList());
        }

        List<JobStatus> statuses = Arrays.asList(scheduler.getJobStatuses(identifiers.toArray(new String[0])));

        scheduler.close();

        return new ListJobsOutput(statuses);
    }
}
