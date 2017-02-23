package nl.esciencecenter.xenon.cli;

import static nl.esciencecenter.xenon.cli.ParserHelpers.getJobDescription;

import java.io.IOException;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.jobs.Job;
import nl.esciencecenter.xenon.jobs.JobDescription;
import nl.esciencecenter.xenon.jobs.Jobs;
import nl.esciencecenter.xenon.jobs.Scheduler;
import nl.esciencecenter.xenon.jobs.Streams;
import nl.esciencecenter.xenon.util.Utils;
import nl.esciencecenter.xenon.util.StreamForwarder;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class ExecCommand extends XenonCommand {
    @Override
    public Subparser buildArgumentParser(Subparsers subparsers) {
        //   exec <executable> <args> <environment> <job options> <max time> <queue> <working directory> <std* attached to local streams>

        Subparser subparser = subparsers.addParser("exec")
            .setDefault("command", this)
            .help("Execute job at location")
            .description("Execute job at location");
        subparser.addArgument("executable").help("Executable to execute").required(true);
        subparser.addArgument("args")
            .help("Arguments for executable, prepend ' -- ' when arguments start with '-'")
            .nargs("*");

        subparser.addArgument("--queue").help("Execute job in this queue");
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

        subparser.addArgument("--wait-timeout")
            .type(Long.class)
            .help("Time to wait for job completion")
            .setDefault(Long.MAX_VALUE);
        return subparser;
    }

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
        StreamForwarder stderrForwarder = new StreamForwarder(streams.getStderr(), System.err);
        jobs.waitUntilDone(job, waitTimeout);
        try {
            // Using copy instead of StreamForwarder to pipe stdout in main thread,
            // so close is called after all stdout has been produced
            Utils.copy(streams.getStdout(), System.out, -1);
            streams.getStdout().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stdinForwarder.terminate(1000);
        stderrForwarder.terminate(1000);
        jobs.close(scheduler);
        // run has no output, because all output has already been sent to stdout and stderr.
        return null;
    }
}
