package nl.esciencecenter.xenon.cli.exec;

import net.sourceforge.argparse4j.inf.Namespace;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.schedulers.JobDescription;
import nl.esciencecenter.xenon.schedulers.JobStatus;
import nl.esciencecenter.xenon.schedulers.Scheduler;
import nl.esciencecenter.xenon.schedulers.Streams;
import nl.esciencecenter.xenon.utils.StreamForwarder;

import static nl.esciencecenter.xenon.cli.Utils.createScheduler;
import static nl.esciencecenter.xenon.cli.Utils.getJobDescription;

/**
 * Command to execute job in the foreground
 */
public class ExecCommand extends XenonCommand {
    @Override
    public Object run(Namespace res) throws XenonException {
        JobDescription description = getJobDescription(res);
        long waitTimeout = res.getLong("wait_timeout");
        try (Scheduler scheduler = createScheduler(res)) {
            Streams streams = scheduler.submitInteractiveJob(description);
            StreamForwarder stdinForwarder = new StreamForwarder(System.in, streams.getStdin());
            StreamForwarder stdoutForwarder = new StreamForwarder(streams.getStdout(), System.out);
            StreamForwarder stderrForwarder = new StreamForwarder(streams.getStderr(), System.err);
            JobStatus status = scheduler.waitUntilDone(streams.getJobIdentifier(), waitTimeout);
            stdinForwarder.terminate(1000);
            stderrForwarder.terminate(1000);
            stdoutForwarder.terminate(1000);
            System.exit(status.getExitCode());
        }
        // run has no output, because all output has already been sent to stdout and stderr.
        return null;
    }
}
