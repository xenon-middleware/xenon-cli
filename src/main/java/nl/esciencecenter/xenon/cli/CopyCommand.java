package nl.esciencecenter.xenon.cli;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.files.*;

import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopyCommand extends XenonCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyCommand.class);

    protected void copy(Files files, CopyInput source, CopyInput target) throws XenonException {
        FileSystem sourceFS = files.newFileSystem(source.scheme, source.location, source.credential, source.properties);
        FileSystem targetFS = files.newFileSystem(target.scheme, target.location, target.credential, target.properties);

        Path sourcePath = files.newPath(sourceFS, new RelativePath(source.path));
        if ("local".equals(source.scheme) || "file".equals(source.scheme)) {
            if (!source.path.startsWith("/")) {
                // Path is relative to working directory, make it absolute
                RelativePath workingDirectory = new RelativePath(System.getProperty("user.dir"));
                sourcePath = files.newPath(sourceFS, workingDirectory.resolve(source.path));
            }
        }
        Path targetPath = files.newPath(targetFS, new RelativePath(target.path));
        if ("local".equals(target.scheme) || "file".equals(target.scheme)) {
            if (!target.path.startsWith("/")) {
                // Path is relative to working directory, make it absolute
                RelativePath workingDirectory = new RelativePath(System.getProperty("user.dir"));
                sourcePath = files.newPath(sourceFS, workingDirectory.resolve(target.path));
            }
        }

        files.copy(sourcePath, targetPath, CopyOption.CREATE);

        files.close(sourceFS);
        files.close(targetFS);
    }

    @Override
    public Subparser buildArgumentParser(Subparsers subparsers) {
        Subparser subparser = subparsers.addParser("copy")
            .setDefault("command", this)
            .help("Copy path from location to target location")
            .description("Copy path from location to target location");
        subparser.addArgument("source-path").help("Source path").required(true);
        // TODO For file scheme target-location should be optional
        subparser.addArgument("target-location").help("Target location").required(true);
        subparser.addArgument("target-path").help("Target path").required(true);
        // TODO For file scheme requires no credentials
        ParserHelpers.addCredentialArguments(subparser, "target-");
        return subparser;
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

        CopyInput source = new CopyInput(scheme, sourceLocation, sourcePath, sourceCredential);
        CopyInput target = new CopyInput(scheme, targetLocation, targetPath, targetCredential);

        Files files = xenon.files();
        this.copy(files, source, target);

        CopyOutPut copyOutput = new CopyOutPut(source, target);
        String format = res.getString("format");
        this.print(copyOutput, format);
    }
}
