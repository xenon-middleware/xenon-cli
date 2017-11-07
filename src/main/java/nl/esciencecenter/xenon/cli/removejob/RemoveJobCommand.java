package nl.esciencecenter.xenon.cli.removejob;

import static nl.esciencecenter.xenon.cli.Utils.createScheduler;

import net.sourceforge.argparse4j.inf.Namespace;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.schedulers.Scheduler;

/**
 * Command to remove job from scheduler
 */
public class RemoveJobCommand extends XenonCommand {
    @Override
    public RemoveJobOutput run(Namespace res) throws XenonException {
        String jobId = res.getString("job_identifier");

        try (Scheduler scheduler = createScheduler(res)) {
            scheduler.cancelJob(jobId);
            return new RemoveJobOutput(scheduler.getLocation(), jobId);
        }
    }
}
