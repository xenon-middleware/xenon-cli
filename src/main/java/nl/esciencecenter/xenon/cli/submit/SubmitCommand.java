package nl.esciencecenter.xenon.cli.submit;

import static nl.esciencecenter.xenon.cli.Main.buildXenonProperties;
import static nl.esciencecenter.xenon.cli.ParserHelpers.getAllowedSchedulerPropertyKeys;
import static nl.esciencecenter.xenon.cli.Utils.createScheduler;
import static nl.esciencecenter.xenon.cli.Utils.getJobDescription;

import java.util.Map;
import java.util.Set;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.schedulers.JobDescription;
import nl.esciencecenter.xenon.schedulers.Scheduler;

import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Command to submit job to scheduler
 */
public class SubmitCommand extends XenonCommand {
    @Override
    public SubmitOutput run(Namespace res) throws XenonException {
        JobDescription description = getJobDescription(res);
        Scheduler scheduler = createScheduler(res);

        String jobIdentifier = scheduler.submitBatchJob(description);

        SubmitOutput output = new SubmitOutput(scheduler.getLocation(), description, jobIdentifier);
        scheduler.close();
        return output;
    }
}
