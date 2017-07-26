package nl.esciencecenter.xenon.cli.exec;

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
import nl.esciencecenter.xenon.schedulers.Streams;
import nl.esciencecenter.xenon.utils.StreamForwarder;

import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command to execute job in the foreground
 */
public class ExecCommand extends XenonCommand {
    final Logger logger = LoggerFactory.getLogger(ExecCommand.class);

    @Override
    public Object run(Namespace res) throws XenonException {
        String adaptor = res.getString("adaptor");
        String location = res.getString("location");
        Credential credential = buildCredential(res);
        JobDescription description = getJobDescription(res);
        long waitTimeout = res.getLong("wait_timeout");

        Set<String> allowedKeys = getAllowedSchedulerPropertyKeys(adaptor);
        Map<String, String> props = buildXenonProperties(res, allowedKeys);
        Scheduler scheduler = Scheduler.create(adaptor, location, credential, props);

        Streams streams = scheduler.submitInteractiveJob(description);
        StreamForwarder stdinForwarder = new StreamForwarder(System.in, streams.getStdin());
        StreamForwarder stderrForwarder = new StreamForwarder(streams.getStderr(), System.err);
        StreamForwarder stdoutForwarder = new StreamForwarder(streams.getStdout(), System.out);

        scheduler.waitUntilDone(streams.getJobIdentifier(), waitTimeout);

        stdinForwarder.terminate(waitTimeout);
        stderrForwarder.terminate(waitTimeout);
        stdoutForwarder.terminate(waitTimeout);

        scheduler.close();

        // run has no output, because all output has already been sent to stdout and stderr.
        return null;
    }
}
