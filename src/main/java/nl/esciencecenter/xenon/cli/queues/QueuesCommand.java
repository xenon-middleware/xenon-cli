package nl.esciencecenter.xenon.cli.queues;

import static nl.esciencecenter.xenon.cli.Utils.createScheduler;

import net.sourceforge.argparse4j.inf.Namespace;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.schedulers.Scheduler;

/**
 * Command to list available queues
 */
public class QueuesCommand extends XenonCommand {
    @Override
    public QueuesOutput run(Namespace res) throws XenonException {
        try (Scheduler scheduler = createScheduler(res)) {

            String[] queues = scheduler.getQueueNames();
            String defaultQueue = scheduler.getDefaultQueueName();

            return new QueuesOutput(queues, defaultQueue);
        }
    }
}
