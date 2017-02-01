package nl.esciencecenter.xenon.cli;

import java.util.ArrayList;
import java.util.List;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.files.DirectoryStream;
import nl.esciencecenter.xenon.files.FileAttributes;
import nl.esciencecenter.xenon.files.FileSystem;
import nl.esciencecenter.xenon.files.Files;
import nl.esciencecenter.xenon.files.Path;
import nl.esciencecenter.xenon.files.RelativePath;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Parameters(commandDescription="List of objects")
public class ListCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    @Parameter(names="--location", description="List information about location")
    public String location = null;

    @Parameter(description="List information about path", required=true)
    public List<String> paths;

    public void run(Files files, String adaptor, boolean json) throws XenonException {
        ArrayList<String> objects = listObjects(files, adaptor);
        printObjects(json, objects);
    }

    private void printObjects(boolean json, ArrayList<String> objects) {
        if (json) {
            ListOutput listOutput = new ListOutput();
            listOutput.objects = objects;
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.print(gson.toJson(listOutput));
        } else {
            objects.forEach(System.out::println);
        }
    }

    private ArrayList<String> listObjects(Files files, String adaptor) throws XenonException {
        FileSystem fs = files.newFileSystem(adaptor, location, null, null);
        ArrayList<String> objects = new ArrayList<>();
        for (String pathIn : paths) {
            Path path = files.newPath(fs, new RelativePath(pathIn));
            FileAttributes att = files.getAttributes(path);
            if (att.isDirectory()) {
                DirectoryStream<Path> stream = files.newDirectoryStream(path);
                for (Path p : stream) {
                    objects.add(p.getRelativePath().getFileNameAsString());
                }
            } else if (att.isRegularFile()) {
                LOGGER.error(pathIn + " is a file.");
            } else {
                LOGGER.error("Directory " + pathIn + " does not exist or is not a directory.");
            }
        }
        files.close(fs);
        return objects;
    }
}
