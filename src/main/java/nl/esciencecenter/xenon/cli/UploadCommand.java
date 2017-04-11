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

public class UploadCommand extends CopyCommand {

    @Override
    public Subparser buildArgumentParser(Subparsers subparsers) {
        Subparser subparser = subparsers.addParser("upload")
                .setDefault("command", this)
                .help("Upload local file to target")
                .description("Upload local file to target");
        subparser.addArgument("source")
            .type(Arguments.fileType().acceptSystemIn().verifyCanRead())
            .help("Local source path, use '-' for stdin")
            .required(true);
        subparser.addArgument("target").help("Target path").required(true);
        subparser.addArgument("--recursive").help("Upload directories recursively").action(Arguments.storeTrue());
        addCopyModeArguments(subparser);
        return subparser;
    }

    @Override
    public CopyOutput run(Namespace res, Xenon xenon) throws XenonException {
        String sourcePath = res.getString("source");
        String targetScheme = res.getString("scheme");
        String targetLocation = res.getString("location");
        String targetPath = res.getString("target");
        Credential targetCredential = buildCredential(res, xenon);
        CopyOption copymode = res.get("copymode");
        Boolean recursive = res.getBoolean("recursive");

        CopyInput source = new CopyInput("file", null, sourcePath, null);
        CopyInput target = new CopyInput(targetScheme, targetLocation, targetPath, targetCredential);

        Files files = xenon.files();
        this.copy(files, source, target, recursive, copymode);

        return new CopyOutput(source, target);
    }
}
