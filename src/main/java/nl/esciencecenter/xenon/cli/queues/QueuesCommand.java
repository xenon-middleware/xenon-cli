package nl.esciencecenter.xenon.cli.queues;

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
 * Command to list available queues
 */
public class QueuesCommand extends XenonCommand {
    @Override
    public QueuesOutput run(Namespace res) throws XenonException {
        String adaptor = res.getString("adaptor");
        String location = res.getString("location");
        Credential credential = buildCredential(res);

        Set<String> allowedKeys = getAllowedSchedulerPropertyKeys(adaptor);
        Map<String, String> props = buildXenonProperties(res, allowedKeys);
        Scheduler scheduler = Scheduler.create(adaptor, location, credential, props);
        String[] queues = scheduler.getQueueNames();
        String defaultQueue = scheduler.getDefaultQueueName();

        QueuesOutput output = new QueuesOutput(queues, defaultQueue);
        scheduler.close();
        return output;
    }
}
