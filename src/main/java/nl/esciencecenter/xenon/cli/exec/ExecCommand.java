package nl.esciencecenter.xenon.cli.exec;

import net.sourceforge.argparse4j.inf.Namespace;
import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.jobs.*;
import nl.esciencecenter.xenon.util.StreamForwarder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static nl.esciencecenter.xenon.cli.JobsUtils.getJobDescription;

/**
 * Command to execute job in the foreground
 */
public class ExecCommand extends XenonCommand {
    final Logger logger = LoggerFactory.getLogger(ExecCommand.class);

    @Override
    public Object run(Namespace res, Xenon xenon) throws XenonException {
        String scheme = res.getString("scheme");
        String location = res.getString("location");
        Credential credential = buildCredential(res, xenon);
        JobDescription description = getJobDescription(res);
        description.setInteractive(true);
        long waitTimeout = res.getLong("wait_timeout");

        Jobs jobs = xenon.jobs();
        Scheduler scheduler = jobs.newScheduler(scheme, location, credential, null);

        Job job = jobs.submitJob(scheduler, description);

        Streams streams = jobs.getStreams(job);
        StreamForwarder stderrForwarder = new StreamForwarder(streams.getStderr(), System.err);
        UnbufferedStreamForwarder stdoutForwarder = new UnbufferedStreamForwarder(streams.getStdout(), System.out);
        UnbufferedStreamForwarder stdinForwarder = new UnbufferedStreamForwarder(System.in, streams.getStdin());
        jobs.waitUntilDone(job, waitTimeout);
        stdinForwarder.terminate(1000);
        stderrForwarder.terminate(1000);
        stdoutForwarder.terminate(1000);
        jobs.close(scheduler);
        // run has no output, because all output has already been sent to stdout and stderr.
        return null;
    }
}
