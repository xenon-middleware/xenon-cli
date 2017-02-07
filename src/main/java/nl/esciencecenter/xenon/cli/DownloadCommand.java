package nl.esciencecenter.xenon.cli;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.files.Files;

import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class DownloadCommand extends CopyCommand {

    @Override
    public Subparser buildArgumentParser(Subparsers subparsers) {
        Subparser subparser = subparsers.addParser("download")
                .setDefault("command", this)
                .help("Download source file to local file")
                .description("Download source file to local file");
        subparser.addArgument("source").help("Source path").required(true);
        subparser.addArgument("target").help("Target path").required(true);
        return subparser;
    }

    @Override
    public void run(Namespace res, Xenon xenon) throws XenonException {
        String sourceScheme = res.getString("scheme");
        String sourceLocation = res.getString("location");
        String sourcePath = res.getString("source");
        Credential sourceCredential = buildCredential(res, xenon);
        String targetPath = res.getString("target");

        CopyInput source = new CopyInput(sourceScheme, sourceLocation, sourcePath, sourceCredential);
        CopyInput target = new CopyInput("file", null, targetPath, null);

        Files files = xenon.files();
        this.copy(files, source, target);

        DownloadOutput downloadOutput = new DownloadOutput(source, target);
        String format = res.getString("format");
        this.print(downloadOutput, format);
    }
}
