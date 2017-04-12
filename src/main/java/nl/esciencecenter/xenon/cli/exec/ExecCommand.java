package nl.esciencecenter.xenon.cli.exec;

import static nl.esciencecenter.xenon.cli.JobsUtils.getJobDescription;

import java.io.IOException;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.jobs.Job;
import nl.esciencecenter.xenon.jobs.JobDescription;
import nl.esciencecenter.xenon.jobs.Jobs;
import nl.esciencecenter.xenon.jobs.Scheduler;
import nl.esciencecenter.xenon.jobs.Streams;
import nl.esciencecenter.xenon.util.StreamForwarder;
import nl.esciencecenter.xenon.util.Utils;

import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        StreamForwarder stdinForwarder = new StreamForwarder(System.in, streams.getStdin());
        StreamForwarder stderrForwarder = new StreamForwarder(streams.getStderr(), System.err); //NOSONAR
        jobs.waitUntilDone(job, waitTimeout);
        try {
            // Using copy instead of StreamForwarder to pipe stdout in main thread,
            // so close is called after all stdout has been produced
            Utils.copy(streams.getStdout(), System.out, -1); //NOSONAR
            streams.getStdout().close();
        } catch (IOException e) {
            logger.info("Copy stdout failed", e);
        }
        stdinForwarder.terminate(1000);
        stderrForwarder.terminate(1000);
        jobs.close(scheduler);
        // run has no output, because all output has already been sent to stdout and stderr.
        return null;
    }
}
