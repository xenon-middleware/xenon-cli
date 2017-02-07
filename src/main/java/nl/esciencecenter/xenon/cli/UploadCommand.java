package nl.esciencecenter.xenon.cli;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.files.Files;

import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class UploadCommand extends CopyCommand {

    @Override
    public Subparser buildArgumentParser(Subparsers subparsers) {
        Subparser subparser = subparsers.addParser("upload")
                .setDefault("command", this)
                .help("Upload local file to target")
                .description("Upload local file to target");
        subparser.addArgument("source").help("Source path").required(true);
        subparser.addArgument("target").help("Target path").required(true);
        return subparser;
    }

    @Override
    public void run(Namespace res, Xenon xenon) throws XenonException {
        String sourcePath = res.getString("source");
        String targetScheme = res.getString("scheme");
        String targetLocation = res.getString("location");
        String targetPath = res.getString("target");
        Credential targetCredential = buildCredential(res, xenon);

        CopyInput source = new CopyInput("file", null, sourcePath, null);
        CopyInput target = new CopyInput(targetScheme, targetLocation, targetPath, targetCredential);

        Files files = xenon.files();
        this.copy(files, source, target);

        UploadOutput uploadOutput = new UploadOutput(source, target);
        String format = res.getString("format");
        this.print(uploadOutput, format);
    }
}
