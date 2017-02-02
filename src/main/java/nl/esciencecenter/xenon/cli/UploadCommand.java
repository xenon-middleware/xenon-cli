package nl.esciencecenter.xenon.cli;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.files.CopyOption;
import nl.esciencecenter.xenon.files.FileSystem;
import nl.esciencecenter.xenon.files.Files;
import nl.esciencecenter.xenon.files.Path;
import nl.esciencecenter.xenon.files.RelativePath;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Parameters(commandDescription="Upload file")
public class UploadCommand {
    // TODO change to File type and use converter=FileConverter.class
    @Parameter(names="--source", description="Local file", required=true)
    public String source = null;

    @Parameter(names="--location", description="Target location")
    public String location = null;

    @Parameter(names="--path", description="Target path", required=true)
    public String path = null;

    public void run(Files files, String scheme, boolean json) throws XenonException {
        FileSystem sourceFS = files.newFileSystem("local", null, null, null);
        FileSystem targetFS = files.newFileSystem(scheme, location,null, null);

        Path sourcePath = files.newPath(sourceFS, new RelativePath(source));
        Path targetPath = files.newPath(targetFS, new RelativePath(path));

        files.copy(sourcePath, targetPath, CopyOption.CREATE);

        files.close(sourceFS);
        files.close(targetFS);

        if (json) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            UploadOutput uploadOutput = new UploadOutput();
            uploadOutput.source = source;
            uploadOutput.location = location;
            uploadOutput.path = path;
            System.out.print(gson.toJson(uploadOutput));
        } else {
            System.out.println("Uploaded '" + source + "' to path '" + path + "' at location '" + location + "'");
        }
    }
}
