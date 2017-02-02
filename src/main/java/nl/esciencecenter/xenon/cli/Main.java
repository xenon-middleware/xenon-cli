package nl.esciencecenter.xenon.cli;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.XenonFactory;
import nl.esciencecenter.xenon.files.Files;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    public static final String PROGRAM_NAME = "xenon";
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    @Parameter(names = "--help", help = true)
    private boolean help;

    @Parameter(names="--scheme", description="Xenon scheme to use")
    private String scheme;

    @Parameter(names="--json", description="Output in json format")
    private boolean json = false;

    private ListCommand listCommand = new ListCommand();
    private UploadCommand uploadCommand = new UploadCommand();
    private SchemesCommand schemesCommand = new SchemesCommand();

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        JCommander commander = main.buildCommander();
        try {
            commander.parse(args);
            main.run(commander);
        } catch (ParameterException ex) {
            System.err.println(ex.getMessage());
            commander.usage();
            System.exit(1);
        }
    }

    private JCommander buildCommander() {
        JCommander commander = new JCommander(this);
        commander.setProgramName(PROGRAM_NAME);
        commander.addCommand("schemes", schemesCommand);
        commander.addCommand("list", listCommand);
        commander.addCommand("upload", uploadCommand);
        return commander;
    }

    public void run(JCommander commander) throws XenonException {
        String parsedCommand = commander.getParsedCommand();
        if (help || parsedCommand == null) {
            commander.usage();
            return;
        }
        runCommand(parsedCommand);
    }

    private void runCommand(String command) throws XenonException {
        Xenon xenon = XenonFactory.newXenon(null);
        Files files = xenon.files();
        switch (command) {
            case "list":
                listCommand.run(files, scheme, json);
                break;
            case "upload":
                uploadCommand.run(files, scheme, json);
            case "schemes":
                schemesCommand.run(xenon);
        }
        XenonFactory.endXenon(xenon);
    }
}
