package nl.esciencecenter.xenon.cli.queues;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.jobs.Jobs;
import nl.esciencecenter.xenon.jobs.Scheduler;

import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Command to list available queues
 */
public class QueuesCommand extends XenonCommand {
    @Override
    public QueuesOutput run(Namespace res, Xenon xenon) throws XenonException {
        String scheme = res.getString("scheme");
        String location = res.getString("location");
        Credential credential = buildCredential(res, xenon);

        Jobs jobs = xenon.jobs();
        Scheduler scheduler = jobs.newScheduler(scheme, location, credential, null);
        String[] queues = scheduler.getQueueNames();
        String defaultQueue = jobs.getDefaultQueueName(scheduler);

        QueuesOutput output = new QueuesOutput(queues, defaultQueue);
        jobs.close(scheduler);
        return output;
    }
}
