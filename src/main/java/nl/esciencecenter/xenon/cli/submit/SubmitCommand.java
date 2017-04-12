package nl.esciencecenter.xenon.cli.submit;

import static nl.esciencecenter.xenon.cli.JobsUtils.getJobDescription;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.cli.XenonCommand;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.jobs.Job;
import nl.esciencecenter.xenon.jobs.JobDescription;
import nl.esciencecenter.xenon.jobs.Jobs;
import nl.esciencecenter.xenon.jobs.Scheduler;

import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Command to submit job to scheduler
 */
public class SubmitCommand extends XenonCommand {
    @Override
    public SubmitOutput run(Namespace res, Xenon xenon) throws XenonException {
        String scheme = res.getString("scheme");
        String location = res.getString("location");
        Credential credential = buildCredential(res, xenon);
        JobDescription description = getJobDescription(res);
        String stdin = res.getString("stdin");
        if (stdin != null) {
            description.setStdin(stdin);
        }
        String stdout = res.getString("stdout");
        if (stdout != null) {
            description.setStdout(stdout);
        }
        String stderr = res.getString("stderr");
        if (stderr != null) {
            description.setStderr(stderr);
        }

        Jobs jobs = xenon.jobs();
        Scheduler scheduler = jobs.newScheduler(scheme, location, credential, null);
        Job job = jobs.submitJob(scheduler, description);
        String jobId = job.getIdentifier();
        SubmitOutput output = new SubmitOutput(location, description, jobId);
        jobs.close(scheduler);
        return output;
    }
}
