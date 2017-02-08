package nl.esciencecenter.xenon.cli;

import static nl.esciencecenter.xenon.cli.ParserHelpers.addCopyModeArguments;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.files.CopyOption;
import nl.esciencecenter.xenon.files.Files;

import net.sourceforge.argparse4j.impl.Arguments;
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
        subparser.addArgument("source").help("Local source path").required(true);
        subparser.addArgument("target")
            .type(Arguments.fileType().acceptSystemIn().verifyCanWriteParent())
            .help("Target path, use '-' for stdout")
            .required(true);
        subparser.addArgument("--recursive").help("Copy directories recursively").action(Arguments.storeTrue());
        addCopyModeArguments(subparser);

        return subparser;
    }

    @Override
    public void run(Namespace res, Xenon xenon) throws XenonException {
        String sourceScheme = res.getString("scheme");
        String sourceLocation = res.getString("location");
        String sourcePath = res.getString("source");
        Credential sourceCredential = buildCredential(res, xenon);
        String targetPath = res.getString("target");
        CopyOption copymode = res.get("copymode");
        Boolean recursive = res.getBoolean("recursive");

        CopyInput source = new CopyInput(sourceScheme, sourceLocation, sourcePath, sourceCredential);
        CopyInput target = new CopyInput("file", null, targetPath, null);

        Files files = xenon.files();
        this.copy(files, source, target, recursive, copymode);

        if (!target.stream) {
            DownloadOutput downloadOutput = new DownloadOutput(source, target);
            String format = res.getString("format");
            this.print(downloadOutput, format);
        }
    }
}
