package nl.esciencecenter.xenon.cli.exec;

import static nl.esciencecenter.xenon.cli.Main.buildXenonProperties;
import static nl.esciencecenter.xenon.cli.ParserHelpers.getAllowedSchedulerPropertyKeys;
import static nl.esciencecenter.xenon.cli.Utils.getJobDescription;
import static nl.esciencecenter.xenon.cli.Utils.pipe;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.utils.StreamForwarder;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.schedulers.JobDescription;
import nl.esciencecenter.xenon.schedulers.Scheduler;
import nl.esciencecenter.xenon.schedulers.Streams;

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
        String scheme = res.getString("scheme");
        String location = res.getString("location");
        Credential credential = buildCredential(res);
        JobDescription description = getJobDescription(res);
        long waitTimeout = res.getLong("wait_timeout");

        Set<String> allowedKeys = getAllowedSchedulerPropertyKeys(scheme);
        Map<String, String> props = buildXenonProperties(res, allowedKeys);
        Scheduler scheduler = Scheduler.create(scheme, location, credential, props);

        Streams streams = scheduler.submitInteractiveJob(description);
        StreamForwarder stdinForwarder = new StreamForwarder(System.in, streams.getStdin());
        StreamForwarder stderrForwarder = new StreamForwarder(streams.getStderr(), System.err);
        scheduler.waitUntilDone(streams.getJobIdentifier(), waitTimeout);
        try {
            // Using copy instead of StreamForwarder to pipe stdout in main thread,
            // so close is called after all stdout has been produced
            pipe(streams.getStdout(), System.out);
            streams.getStdout().close();
        } catch (IOException e) {
            logger.info("Copy stdout failed", e);
        }
        stdinForwarder.terminate(1000);
        stderrForwarder.terminate(1000);
        scheduler.close();
        // run has no output, because all output has already been sent to stdout and stderr.
        return null;
    }


}
