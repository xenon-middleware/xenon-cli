package nl.esciencecenter.xenon.cli.removejob;

import static nl.esciencecenter.xenon.cli.Main.buildXenonProperties;
import static nl.esciencecenter.xenon.cli.ParserHelpers.getAllowedSchedulerPropertyKeys;

import java.util.Map;
import java.util.Set;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.schedulers.Scheduler;

import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Command to remove job from scheduler
 */
public class RemoveJobCommand extends XenonCommand {
    @Override
    public RemoveJobOutput run(Namespace res) throws XenonException {
        String adaptor = res.getString("adaptor");
        String location = res.getString("location");
        String jobId = res.getString("job_identifier");
        Credential credential = buildCredential(res);

        Set<String> allowedKeys = getAllowedSchedulerPropertyKeys(adaptor);
        Map<String, String> props = buildXenonProperties(res, allowedKeys);
        Scheduler scheduler = Scheduler.create(adaptor, location, credential, props);

        scheduler.cancelJob(jobId);

        scheduler.close();

        return new RemoveJobOutput(location, jobId);
    }
}
