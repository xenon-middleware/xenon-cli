package nl.esciencecenter.xenon.cli;

import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.files.Files;

public class DownloadCommand extends CopyCommand {

    @Override
    public Subparser buildArgumentParser(Subparsers subparsers) {
        Subparser subparser = subparsers.addParser("download")
                .setDefault("subcommand", this)
                .help("Download source file to local file")
                .description("Download source file to local file");
        subparser.addArgument("sourceLocation")
                .help("Source location, " + getSupportedLocationHelp())
                .setDefault("/");
        subparser.addArgument("sourcePath").help("Source path").required(true);
        subparser.addArgument("targetPath").help("Target path").required(true);
        return subparser;
    }

    @Override
    public void run(Namespace res, Xenon xenon) throws XenonException {
        String sourceScheme = res.getString("scheme");
        String sourceLocation = res.getString("sourceLocation");
        String sourcePath = res.getString("sourcePath");
        Credential sourceCredential = buildCredential(res, xenon);
        String targetPath = res.getString("targetPath");

        CopyInput source = new CopyInput(sourceScheme, sourceLocation, sourcePath, sourceCredential);
        CopyInput target = new CopyInput("local", null, targetPath, null);

        Files files = xenon.files();
        this.copy(files, source, target);

        Boolean json = res.getBoolean("json");
        DownloadOutput downloadOutput = new DownloadOutput(source, target);
        this.print(downloadOutput, json);
    }
}
