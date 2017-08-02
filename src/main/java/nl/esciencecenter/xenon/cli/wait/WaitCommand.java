package nl.esciencecenter.xenon.cli.wait;

import static nl.esciencecenter.xenon.cli.Utils.createScheduler;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.schedulers.JobStatus;
import nl.esciencecenter.xenon.schedulers.Scheduler;

import net.sourceforge.argparse4j.inf.Namespace;

public class WaitCommand extends XenonCommand {
    @Override
    public Object run(Namespace res) throws XenonException {
        Scheduler scheduler = createScheduler(res);
        String identifier = res.getString("identifier");
        long timeout = res.getLong("timeout");

        JobStatus status;
        try {
            status = scheduler.waitUntilDone(identifier, timeout);
            if (status.hasException()) {
                Exception e = status.getException();
                if (e instanceof XenonException) {
                    throw (XenonException) e;
                } else {
                    throw new XenonException(scheduler.getAdaptorName(), e.getMessage(), e);
                }
            }
        } finally {
            scheduler.close();
        }

        return null;
    }
}
