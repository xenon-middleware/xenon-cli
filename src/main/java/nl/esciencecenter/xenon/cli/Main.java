package nl.esciencecenter.xenon.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.sourceforge.argparse4j.inf.*;
import nl.esciencecenter.xenon.AdaptorStatus;
import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.XenonFactory;
import nl.esciencecenter.xenon.XenonPropertyDescription;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final String PROGRAM_NAME = "xenon";
    private static final String PROGRAM_VERSION = "1.0.0";
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        ArgumentParser parser = main.buildArgumentParser();
        Namespace res = parser.parseArgsOrFail(args);
        LOGGER.warn(res.toString());
        ICommand subcommand = res.get("command");
        Xenon xenon = XenonFactory.newXenon(buildXenonProperties(res));
        subcommand.run(res, xenon);
        XenonFactory.endXenon(xenon);
    }

    public static Map<String,String> buildXenonProperties(Namespace res) {
        List<String> propsIn = res.getList("props");
        Map<String, String> propsOut = new HashMap<>();
        if (propsIn != null) {
            for (String prop : propsIn) {
                String[] keyval = prop.split("=", 2);
                propsOut.put(keyval[0], keyval[1]);
            }
        }
        return propsOut;
    }

    public ArgumentParser buildArgumentParser() throws XenonException {
        ArgumentParser parser = ArgumentParsers.newArgumentParser(PROGRAM_NAME, true, "-", "@")
                .defaultHelp(true)
                .description("Files and Jobs operations with Xenon")
                .version(PROGRAM_VERSION);
        parser.addArgument("--version").action(Arguments.version());
        parser.addArgument("--format").choices("cwljson").help("Output in JSON format");
        addSchemeSubParsers(parser);
        addCredentialArguments(parser);
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
                Argument locationArgument = schemeParser.addArgument("--location").help("Location, " + getSupportedLocationHelp(adaptor));
                if (sshSchemes.contains(scheme)) {
                    locationArgument.required(true);
                }
                schemeParser.addArgument("--prop")
                    .action(Arguments.append())
                    .metavar("KEY=VALUE")
                    .help("Xenon adaptor properties, " + getSupportedPropertiesHelp(adaptor))
                    .dest("props");
                Subparsers commandsParser = schemeParser.addSubparsers().title("commands");
                if (filesSchemes.contains(scheme)) {
                    if (!localSchemes.contains(scheme)) {
                        // upload
                        new UploadCommand().buildArgumentParser(commandsParser);
                        // download
                        new DownloadCommand().buildArgumentParser(commandsParser);
                    }
                    // copy
                    // remove
                    // list
                    new ListCommand().buildArgumentParser(commandsParser);
                } else if (jobsSchemes.contains(scheme)) {
                    if (!onlineSchemes.contains(scheme)) {
                        // submit
                        // list
                        // remove
                        // queues
                    }
                    // exec
                }
            }
        }
    }

    private String getSupportedLocationHelp(AdaptorStatus adaptor) {
        List<String> helps = Arrays.stream(adaptor.getSupportedLocations()).map((location) -> "- " + location).collect(Collectors.toList());
        helps.add(0, "Supported locations:");
        String sep = System.getProperty("line.separator");
        return String.join(sep, helps);
    }

    private String getSupportedPropertiesHelp(AdaptorStatus adaptor) {
        String sep = System.getProperty("line.separator");
        List<String> helps = Arrays.stream(adaptor.getSupportedProperties()).map(
                (property) -> "- " + property.getName() + "=" + property.getDefaultValue() + " ("+ property.getDescription() + ", type:" + property.getType() + ") "
            ).collect(Collectors.toList());

        helps.add(0, "Supported properties:");
        return String.join(sep, helps);
    }

    private void addCredentialArguments(ArgumentParser parser) {
        ArgumentGroup credGroup = parser.addArgumentGroup("optional credential arguments");
        credGroup.addArgument("--username").help("Username");
        credGroup.addArgument("--password").help("Password or passphrase");
        credGroup.addArgument("--certfile").help("Certificate file");
    }

    private AdaptorStatus[] getAdaptorStatuses() {
        AdaptorStatus[] adaptors = {};
        try {
            Xenon xenon = XenonFactory.newXenon(null);
            adaptors = xenon.getAdaptorStatuses();
            XenonFactory.endXenon(xenon);
            return adaptors;
        } catch (XenonException e) {
            e.printStackTrace();
        }
        return adaptors;
    }
}
