package nl.esciencecenter.xenon.cli;

import static nl.esciencecenter.xenon.util.Utils.walkFileTree;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.files.DirectoryStream;
import nl.esciencecenter.xenon.files.FileAttributes;
import nl.esciencecenter.xenon.files.FileSystem;
import nl.esciencecenter.xenon.files.Files;
import nl.esciencecenter.xenon.files.Path;
import nl.esciencecenter.xenon.files.PathAttributesPair;
import nl.esciencecenter.xenon.files.RelativePath;
import nl.esciencecenter.xenon.util.FileVisitResult;
import nl.esciencecenter.xenon.util.FileVisitor;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class ListFilesCommand extends XenonCommand {

    private ListFilesOutput listObjects(Files files, String scheme, String location, String pathIn, Credential credential, Boolean recursive) throws XenonException {
        FileSystem fs = files.newFileSystem(scheme, location, credential, null);

        ListFilesOutput listing = new ListFilesOutput();
        Path path = files.newPath(fs, new RelativePath(pathIn));
        FileAttributes att = files.getAttributes(path);
        if (att.isDirectory()) {
            if (recursive) {
                FileVisitor visitor = new FileVisitor() {
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, XenonException exception, Files files) throws XenonException {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, FileAttributes attributes, Files files) throws XenonException {
                        String filename = dir.getRelativePath().getFileNameAsString();
                        listing.objects.add(filename);
                        listing.directories.add(filename);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, FileAttributes attributes, Files files) throws XenonException {
                        String filename = file.getRelativePath().getFileNameAsString();
                        listing.objects.add(filename);
                        listing.files.add(filename);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, XenonException exception, Files files) throws XenonException {
                        // TODO now failed files are ignored or should it die
                        return FileVisitResult.CONTINUE;
                    }
                };
                walkFileTree(files, path, visitor);
            } else {
                DirectoryStream<PathAttributesPair> stream = files.newAttributesDirectoryStream(path);
                for (PathAttributesPair p : stream) {
                    String filename = p.path().getRelativePath().getFileNameAsString();
                    listing.objects.add(filename);
                    if (p.attributes().isDirectory()) {
                        listing.directories.add(filename);
                    } else {
                        listing.files.add(filename);
                    }
                }
            }
        } else {
            String fn = path.getRelativePath().getFileNameAsString();
            listing.objects.add(fn);
            listing.files.add(fn);
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
        return subparser;
    }

    public ListFilesOutput run(Namespace res, Xenon xenon) throws XenonException {
        String scheme = res.getString("scheme");
        String location = res.getString("location");
        String path = res.getString("path");
        Boolean recursive = res.getBoolean("recursive");
        Files files = xenon.files();
        Credential credential = buildCredential(res, xenon);
        ListFilesOutput listing = listObjects(files, scheme, location, path, credential, recursive);

        return listing;
    }
}
