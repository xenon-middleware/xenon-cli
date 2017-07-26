package nl.esciencecenter.xenon.cli.submit;

import static nl.esciencecenter.xenon.cli.Main.buildXenonProperties;
import static nl.esciencecenter.xenon.cli.ParserHelpers.getAllowedSchedulerPropertyKeys;
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
        String adaptor = res.getString("adaptor");
        String location = res.getString("location");
        Credential credential = buildCredential(res);
        JobDescription description = getJobDescription(res);
        String stdin = res.getString("stdin");
        if (stdin != null) {
            description.setStdin(stdin);
        }
        String stdout = res.getString("stdout");
        if (stdout != null) {
            description.setStdout(stdout);
        }
        String stderr = res.getString("stderr");
        if (stderr != null) {
            description.setStderr(stderr);
        }

        Set<String> allowedKeys = getAllowedSchedulerPropertyKeys(adaptor);
        Map<String, String> props = buildXenonProperties(res, allowedKeys);
        Scheduler scheduler = Scheduler.create(adaptor, location, credential, props);
        String jobIdentifier = scheduler.submitBatchJob(description);
        SubmitOutput output = new SubmitOutput(location, description, jobIdentifier);
        scheduler.close();
        return output;
    }
}
