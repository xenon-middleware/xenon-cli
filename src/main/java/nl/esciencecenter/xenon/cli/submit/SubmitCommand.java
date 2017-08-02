package nl.esciencecenter.xenon.cli.submit;

import static nl.esciencecenter.xenon.cli.Utils.createScheduler;
import static nl.esciencecenter.xenon.cli.Utils.getJobDescription;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.schedulers.JobDescription;
import nl.esciencecenter.xenon.schedulers.Scheduler;

import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Command to submit job to scheduler
 */
public class SubmitCommand extends XenonCommand {
    @Override
    public Object run(Namespace res) throws XenonException {
        JobDescription description = getJobDescription(res);
        Scheduler scheduler = createScheduler(res);

        String jobIdentifier = scheduler.submitBatchJob(description);
        scheduler.close();

        Boolean longFormat = res.getBoolean("long");
        if (longFormat) {
            return new SubmitOutput(scheduler.getLocation(), description, jobIdentifier);
        } else {
            return jobIdentifier;
        }
    }
}
