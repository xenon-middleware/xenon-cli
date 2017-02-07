package nl.esciencecenter.xenon.cli;

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

import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class ListCommand extends XenonCommand {

    private ListOutput listObjects(Files files, String scheme, String location, String pathIn, Credential credential) throws XenonException {
        FileSystem fs = files.newFileSystem(scheme, location, credential, null);

        ListOutput listing = new ListOutput();
        Path path = files.newPath(fs, new RelativePath(pathIn));
        FileAttributes att = files.getAttributes(path);
        if (att.isDirectory()) {
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
        return subparser;
    }

    public void run(Namespace res, Xenon xenon) throws XenonException {
        String scheme = res.getString("scheme");
        String location = res.getString("location");
        String path = res.getString("path");
        Files files = xenon.files();
        Credential credential = buildCredential(res, xenon);
        ListOutput listing = listObjects(files, scheme, location, path, credential);

        String format = res.getString("format");
        this.print(listing, format);
    }
}
