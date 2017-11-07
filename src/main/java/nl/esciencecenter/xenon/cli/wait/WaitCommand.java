package nl.esciencecenter.xenon.cli.wait;

import static nl.esciencecenter.xenon.cli.Utils.createScheduler;

import net.sourceforge.argparse4j.inf.Namespace;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.schedulers.JobStatus;
import nl.esciencecenter.xenon.schedulers.Scheduler;

public class WaitCommand extends XenonCommand {
    @Override
    public Object run(Namespace res) throws XenonException {
        try (Scheduler scheduler = createScheduler(res)) {
            String identifier = res.getString("identifier");
            long timeout = res.getLong("timeout");

            JobStatus status;
            status = scheduler.waitUntilDone(identifier, timeout);
            if (status.hasException()) {
                Exception e = status.getException();
                if (e != null) {
                    throw (XenonException) e;
                }
            }
        }
        return null;
    }
}
