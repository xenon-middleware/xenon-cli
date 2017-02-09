package nl.esciencecenter.xenon.cli;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.jobs.Jobs;
import nl.esciencecenter.xenon.jobs.Scheduler;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class QueuesCommand extends XenonCommand {
    @Override
    public Subparser buildArgumentParser(Subparsers subparsers) {
        Subparser subparser = subparsers.addParser("queues")
            .setDefault("command", this)
            .help("List of available queues")
            .description("List of available queues");
        subparser.addArgument("--default").help("Filter on default queue").action(Arguments.storeTrue());
        return subparser;
    }

    @Override
    public QueuesOutput run(Namespace res, Xenon xenon) throws XenonException {
        String scheme = res.getString("scheme");
        String location = res.getString("location");
        Credential credential = buildCredential(res, xenon);

        Jobs jobs = xenon.jobs();
        Scheduler scheduler = jobs.newScheduler(scheme, location, credential, null);
        String[] queues = scheduler.getQueueNames();
        String defaultQueue = jobs.getDefaultQueueName(scheduler);
        jobs.close(scheduler);

        QueuesOutput output = new QueuesOutput(queues, defaultQueue);
        return output;
    }
}
