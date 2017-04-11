package nl.esciencecenter.xenon.cli;

import static nl.esciencecenter.xenon.util.Utils.walkFileTree;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.files.FileAttributes;
import nl.esciencecenter.xenon.files.FileSystem;
import nl.esciencecenter.xenon.files.Files;
import nl.esciencecenter.xenon.files.Path;
import nl.esciencecenter.xenon.files.RelativePath;
import nl.esciencecenter.xenon.util.FileVisitResult;
import nl.esciencecenter.xenon.util.FileVisitor;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class ListFilesCommand extends XenonCommand {

    private ListFilesOutput listObjects(Files files, String scheme, String location, String pathIn, Credential credential, Boolean recursive, Boolean hidden) throws XenonException {
        FileSystem fs = files.newFileSystem(scheme, location, credential, null);

        ListFilesOutput listing = new ListFilesOutput();
        RelativePath relPath = new RelativePath(pathIn);
        Path path = files.newPath(fs, relPath);
        FileAttributes att = files.getAttributes(path);
        int depth = 1;
        if (recursive) {
            depth = Integer.MAX_VALUE;
        }
        if (att.isDirectory()) {
            FileVisitor visitor = new FileVisitor() {
                @Override
                public FileVisitResult postVisitDirectory(Path dir, XenonException exception, Files files) throws XenonException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, FileAttributes attributes, Files files) throws XenonException {
                    if (dir.equals(path)) {
                        return FileVisitResult.CONTINUE;
                    }
                    if (!hidden && attributes.isHidden()) {
                        return FileVisitResult.TERMINATE;
                    }
                    String filename = relPath.relativize(dir.getRelativePath()).getRelativePath();
                    listing.addDirectory(filename);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, FileAttributes attributes, Files files) throws XenonException {
                    String filename = relPath.relativize(file.getRelativePath()).getRelativePath();
                    if (!hidden && attributes.isHidden()) {
                        return FileVisitResult.CONTINUE;
                    }
                    if (attributes.isDirectory()) {
                        listing.addDirectory(filename);
                    } else {
                        listing.addFile(filename);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, XenonException exception, Files files) throws XenonException {
                    // TODO now failed files are ignored or should it die
                    return FileVisitResult.CONTINUE;
                }
            };
            walkFileTree(files, path, true, depth, visitor);
        } else {
            String fn = path.getRelativePath().getFileNameAsString();
            listing.addFile(fn);
        }
        files.close(fs);
        return listing;
    }

    public Subparser buildArgumentParser(Subparsers subparsers) {
        Subparser subparser = subparsers.addParser("list")
                .setDefault("command", this)
                .help("List objects at path of location")
                .description("List objects at path of location");
        subparser.addArgument("path").help("Path").required(true);
        subparser.addArgument("--recursive").help("List directories recursively").action(Arguments.storeTrue());
        subparser.addArgument("--hidden").help("Include hidden items").action(Arguments.storeTrue());
        return subparser;
    }

    public ListFilesOutput run(Namespace res, Xenon xenon) throws XenonException {
        String scheme = res.getString("scheme");
        String location = res.getString("location");
        String path = res.getString("path");
        Boolean recursive = res.getBoolean("recursive");
        Boolean hidden = res.getBoolean("hidden");
        Files files = xenon.files();
        Credential credential = buildCredential(res, xenon);
        return listObjects(files, scheme, location, path, credential, recursive, hidden);
    }
}
