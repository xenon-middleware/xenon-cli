package nl.esciencecenter.xenon.cli;

import static nl.esciencecenter.xenon.cli.ParserHelpers.getJobDescription;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.jobs.Job;
import nl.esciencecenter.xenon.jobs.JobDescription;
import nl.esciencecenter.xenon.jobs.Jobs;
import nl.esciencecenter.xenon.jobs.Scheduler;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class SubmitCommand extends XenonCommand {

    @Override
    public Subparser buildArgumentParser(Subparsers subparsers) {
        //   exec <executable> <args> <environment> <job options> <max time> <queue> <working directory> <std* attached to local streams>

        Subparser subparser = subparsers.addParser("submit")
            .setDefault("command", this)
            .help("Submit job at location")
            .description("Submit job at location");
        subparser.addArgument("executable").help("Executable to schedule for execution").required(true);
        subparser.addArgument("args")
            .help("Arguments for executable, prepend ' -- ' when arguments start with '-'")
            .nargs("*");

        subparser.addArgument("--queue").help("Schedule job in this queue");
        subparser.addArgument("--env")
            .help("Environment variable of the executable")
            .metavar("KEY=VAL")
            .action(Arguments.append())
            .dest("envs");
        subparser.addArgument("--option")
            .help("Option for job")
            .metavar("KEY=VAL")
            .action(Arguments.append())
            .dest("options");
        subparser.addArgument("--max-time").help("Maximum job time (in minutes)").type(Integer.class).setDefault(JobDescription.DEFAULT_MAX_RUN_TIME);
        subparser.addArgument("--node-count").type(Integer.class).help("Number of nodes to reserve").setDefault(1);
        subparser.addArgument("--procs-per-node").type(Integer.class).help("Number of processes started on each node").setDefault(1);
        subparser.addArgument("--working-directory")
            .help("Path at location where executable should be executed. If not given will local working directory or when remove will use home directory");

        subparser.addArgument("--stdin").help("Path to file which is used as stdin for executable");
        subparser.addArgument("--stdout").help("Path to file which is used as stdout for executable");
        subparser.addArgument("--stderr").help("Path to file which is used as stderr for executable");

        return subparser;
    }

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
