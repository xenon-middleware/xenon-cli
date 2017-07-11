package nl.esciencecenter.xenon.cli.removejob;

import static nl.esciencecenter.xenon.cli.Main.buildXenonProperties;
import static nl.esciencecenter.xenon.cli.ParserHelpers.getAllowedXenonPropertyKeys;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.XenonPropertyDescription;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.jobs.Job;
import nl.esciencecenter.xenon.jobs.Jobs;
import nl.esciencecenter.xenon.jobs.NoSuchJobException;
import nl.esciencecenter.xenon.jobs.Scheduler;

import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Command to remove job from scheduler
 */
public class RemoveJobCommand extends XenonCommand {
    @Override
    public RemoveJobOutput run(Namespace res, Xenon xenon) throws XenonException {
        String scheme = res.getString("scheme");
        String location = res.getString("location");
        String jobId = res.getString("job_identifier");
        Credential credential = buildCredential(res, xenon);

        Jobs jobs = xenon.jobs();
        Set<String> allowedKeys = getAllowedXenonPropertyKeys(xenon, scheme, XenonPropertyDescription.Component.SCHEDULER);
        Map<String, String> props = buildXenonProperties(res, allowedKeys);
        Scheduler scheduler = jobs.newScheduler(scheme, location, credential, props);
        Job[] scheduledJobs = jobs.getJobs(scheduler);
        Optional<Job> job = Arrays.stream(scheduledJobs).filter(j -> j.getIdentifier().equals(jobId)).findFirst();
        if (job.isPresent()) {
            jobs.cancelJob(job.get());
        } else {
           throw new NoSuchJobException(scheme, "Job with identifier '" + jobId + "' not found");
        }
        jobs.close(scheduler);

        return new RemoveJobOutput(location, jobId);
    }
}
