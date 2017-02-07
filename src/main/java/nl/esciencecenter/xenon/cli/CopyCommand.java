package nl.esciencecenter.xenon.cli;

import static nl.esciencecenter.xenon.cli.ParserHelpers.addCopyModeArguments;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.files.*;
import nl.esciencecenter.xenon.util.Utils;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopyCommand extends XenonCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyCommand.class);

    protected void copy(Files files, CopyInput source, CopyInput target, Boolean recursive, CopyOption copymode) throws XenonException {
        if (recursive && source.stream) {
            throw new NoSuchCopyException(source.scheme, "Unable to do recursive copy from stdin");
        }
        if (recursive && target.stream) {
            throw new NoSuchCopyException(target.scheme, "Unable to do recursive copy to stdout");
        }

        FileSystem sourceFS = files.newFileSystem(source.scheme, source.location, source.credential, source.properties);
        FileSystem targetFS = files.newFileSystem(target.scheme, target.location, target.credential, target.properties);

        Path sourcePath = files.newPath(sourceFS, new RelativePath(source.path));
        if ("local".equals(source.scheme) || "file".equals(source.scheme)) {
            if (!source.path.startsWith("/") && !source.path.equals("-")) {
                // Path is relative to working directory, make it absolute
                RelativePath workingDirectory = new RelativePath(System.getProperty("user.dir"));
                sourcePath = files.newPath(sourceFS, workingDirectory.resolve(source.path));
            }
        }
        Path targetPath = files.newPath(targetFS, new RelativePath(target.path));
        if ("local".equals(target.scheme) || "file".equals(target.scheme)) {
            if (!target.path.startsWith("/") && !target.path.equals("-")) {
                // Path is relative to working directory, make it absolute
                RelativePath workingDirectory = new RelativePath(System.getProperty("user.dir"));
                sourcePath = files.newPath(sourceFS, workingDirectory.resolve(target.path));
            }
        }

        if (source.stream && !target.stream) {
            // copy from stdin
            if (copymode.occursIn(CopyOption.REPLACE)) {
                Utils.copy(files, System.in, targetPath, true);
            } else {
                Utils.copy(files, System.in, targetPath, false);
            }
        } else if (!source.stream && target.stream) {
            // copy to stdout
            Utils.copy(files, sourcePath, System.out);
        } else if (recursive) {
            Utils.recursiveCopy(files, sourcePath, targetPath, copymode);
        } else {
            files.copy(sourcePath, targetPath, copymode);
        }

        files.close(sourceFS);
        files.close(targetFS);
    }

    public Subparser buildArgumentParser(Subparsers subparsers, String supportedLocationHelp, Boolean isLocal) {
        Subparser subparser = subparsers.addParser("copy")
            .setDefault("command", this)
            .help("Copy path from location to target location")
            .description("Copy path from location to target location");
        Argument sourcePath = subparser.addArgument("source-path").required(true);
        if (isLocal) {
            sourcePath
                .help("Source path, use '-' for stdin")
                .type(Arguments.fileType().acceptSystemIn().verifyCanRead());
        } else {
            sourcePath.help("Source path");
        }
        Argument targetLocation = subparser.addArgument("target-location").help("Target location, " + supportedLocationHelp);
        if (!isLocal) {
            targetLocation.required(true);
        }
        Argument targetPath = subparser.addArgument("target-path").required(true);
        if (isLocal) {
            targetPath.help("Source path, use '-' for stdout")
                .type(Arguments.fileType().acceptSystemIn().verifyCanWrite());
        } else {
            targetPath.help("Target path");
        }
        if (!isLocal) {
            ParserHelpers.addCredentialArguments(subparser, "target-");
        }
        subparser.addArgument("--recursive").help("Copy directories recursively").action(Arguments.storeTrue());
        addCopyModeArguments(subparser);
        return subparser;
    }

    @Override
    public Subparser buildArgumentParser(Subparsers subparsers) {
        return buildArgumentParser(subparsers, "", false);
    }

    @Override
    public void run(Namespace res, Xenon xenon) throws XenonException {
        String scheme = res.getString("scheme");
        String sourceLocation = res.getString("location");
        String sourcePath = res.getString("source_path");
        Credential sourceCredential = buildCredential(res, xenon);
        String targetLocation = res.getString("target_location");
        String targetPath = res.getString("target_path");
        Credential targetCredential = buildCredential(res, xenon, "target_");
        CopyOption copymode = res.get("copymode");
        Boolean recursive = res.getBoolean("recursive");

        CopyInput source = new CopyInput(scheme, sourceLocation, sourcePath, sourceCredential);
        CopyInput target = new CopyInput(scheme, targetLocation, targetPath, targetCredential);

        Files files = xenon.files();
        this.copy(files, source, target, recursive, copymode);

        CopyOutPut copyOutput = new CopyOutPut(source, target);
        String format = res.getString("format");
        this.print(copyOutput, format);
    }
}
