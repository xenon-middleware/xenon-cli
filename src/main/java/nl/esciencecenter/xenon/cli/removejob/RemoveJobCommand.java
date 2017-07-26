package nl.esciencecenter.xenon.cli.removejob;

import net.sourceforge.argparse4j.inf.Namespace;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.schedulers.Scheduler;

import static nl.esciencecenter.xenon.cli.Utils.createScheduler;

/**
 * Command to remove job from scheduler
 */
public class RemoveJobCommand extends XenonCommand {
    @Override
    public RemoveJobOutput run(Namespace res) throws XenonException {
        String jobId = res.getString("job_identifier");

        Scheduler scheduler = createScheduler(res);
        scheduler.cancelJob(jobId);
        scheduler.close();

        return new RemoveJobOutput(scheduler.getLocation(), jobId);
    }
}
