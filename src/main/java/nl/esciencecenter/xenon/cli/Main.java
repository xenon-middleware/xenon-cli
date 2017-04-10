package nl.esciencecenter.xenon.cli;

import static nl.esciencecenter.xenon.cli.ParserHelpers.getSupportedLocationHelp;
import static nl.esciencecenter.xenon.cli.ParserHelpers.parseArgumentListAsMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import nl.esciencecenter.xenon.AdaptorStatus;
import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.XenonFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final String PROGRAM_NAME = "xenon";
    private static final String PROGRAM_VERSION = "1.0.0";
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private final ArgumentParser parser;
    private Namespace res;

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        try {
            Object output = main.run(args);
            main.print(output);
        } catch (ArgumentParserException e) {
            main.getParser().handleError(e);
        }
    }

    public static Map<String,String> buildXenonProperties(Namespace res) {
        return parseArgumentListAsMap(res.getList("props"));
    }

    public ArgumentParser getParser() {
        return parser;
    }

    public Namespace getRes() {
        return res;
    }

    public Main() throws XenonException {
        parser = buildArgumentParser();
    }

    public Object run(String[] args) throws XenonException, ArgumentParserException {
        res = parser.parseArgs(args);
        ICommand subcommand = res.get("command");
        Xenon xenon = XenonFactory.newXenon(buildXenonProperties(res));
        Object output = subcommand.run(res, xenon);
        XenonFactory.endXenon(xenon);
        return output;
    }

    public void print(Object output) {
        String format = res.getString("format");
        if ("cwljson".equals(format)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.print(gson.toJson(output));
        } else if (output != null) {
            System.out.println(output);
        }
    }

    public ArgumentParser buildArgumentParser() throws XenonException {
        ArgumentParser parser = ArgumentParsers.newArgumentParser(PROGRAM_NAME, true, "-", "@")
                .defaultHelp(true)
                .description("Files and Jobs operations with Xenon")
                .version(PROGRAM_VERSION);
        parser.addArgument("--version").action(Arguments.version());
        parser.addArgument("--format").choices("cwljson").help("Output in JSON format");
        addSchemeSubParsers(parser);
        ParserHelpers.addCredentialArguments(parser);
        return parser;
    }

    private void addSchemeSubParsers(ArgumentParser parser) {
        Subparsers subparsers = parser.addSubparsers().title("scheme");
        AdaptorStatus[] adaptors = getAdaptorStatuses();

        // TODO make filesSchemes+jobsSchemes dynamic after https://github.com/NLeSC/Xenon/issues/400 is fixed
        List<String> filesSchemes = Arrays.asList("file", "sftp", "ftp");
        List<String> jobsSchemes = Arrays.asList("local", "ssh", "ge", "sge", "slurm", "torque");
        List<String> onlineSchemes = Arrays.asList("file", "local", "ssh", "sftp");
        List<String> localSchemes = Arrays.asList("file", "local");
        List<String> sshSchemes = Arrays.asList("ssh", "sftp");

        for (AdaptorStatus adaptor: adaptors) {
            for (String scheme: adaptor.getSupportedSchemes()) {
                Subparser schemeParser = subparsers.addParser(scheme)
                    .help(adaptor.getDescription())
                    .description(adaptor.getDescription())
                    .setDefault("scheme", scheme);
                // --location
                String supportedLocationHelp = getSupportedLocationHelp(adaptor);
                Argument locationArgument = schemeParser.addArgument("--location").help("Location, " + supportedLocationHelp);
                if (sshSchemes.contains(scheme)) {
                    locationArgument.required(true);
                }
                // --prop
                schemeParser.addArgument("--prop")
                    .action(Arguments.append())
                    .metavar("KEY=VALUE")
                    .help("Xenon adaptor properties, " + getSupportedPropertiesHelp(adaptor))
                    .dest("props");
                Subparsers commandsParser = schemeParser.addSubparsers().title("commands");
                if (filesSchemes.contains(scheme)) {
                    // copy
                    new CopyCommand().buildArgumentParser(commandsParser, supportedLocationHelp, localSchemes.contains(scheme));
                    if (!localSchemes.contains(scheme)) {
                        // upload
                        new UploadCommand().buildArgumentParser(commandsParser);
                        // download
                        new DownloadCommand().buildArgumentParser(commandsParser);
                    }
                    // list
                    new ListFilesCommand().buildArgumentParser(commandsParser);
                    // remove
                    new RemoveFileCommand().buildArgumentParser(commandsParser);
                } else if (jobsSchemes.contains(scheme)) {
                    // exec
                    new ExecCommand().buildArgumentParser(commandsParser);
                    if (!onlineSchemes.contains(scheme)) {
                        // submit
                        new SubmitCommand().buildArgumentParser(commandsParser);
                        // list
                        new ListJobsCommand().buildArgumentParser(commandsParser);
                        // remove
                        new RemoveJobCommand().buildArgumentParser(commandsParser);
                        // queues
                        new QueuesCommand().buildArgumentParser(commandsParser);
                    }
                }
            }
        }
    }

    private String getSupportedPropertiesHelp(AdaptorStatus adaptor) {
        String sep = System.getProperty("line.separator");
        List<String> helps = Arrays.stream(adaptor.getSupportedProperties()).map(
                (property) -> "- " + property.getName() + "=" + property.getDefaultValue() + " ("+ property.getDescription() + ", type:" + property.getType() + ") "
            ).collect(Collectors.toList());

        helps.add(0, "Supported properties:");
        return String.join(sep, helps);
    }

    private AdaptorStatus[] getAdaptorStatuses() {
        AdaptorStatus[] adaptors = {};
        try {
            Xenon xenon = XenonFactory.newXenon(null);
            adaptors = xenon.getAdaptorStatuses();
            XenonFactory.endXenon(xenon);
            return adaptors;
        } catch (XenonException e) {
            LOGGER.info("Failed to getAdaptorStatuses", e);
        }
        return adaptors;
    }
}
