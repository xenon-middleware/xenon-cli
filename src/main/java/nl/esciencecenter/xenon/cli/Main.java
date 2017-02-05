package nl.esciencecenter.xenon.cli;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.*;
import nl.esciencecenter.xenon.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final String PROGRAM_NAME = "xenon";
    private static final String PROGRAM_VERSION = "1.0.0";
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        ArgumentParser parser = main.buildArgumentParser();
        Namespace res = parser.parseArgsOrFail(args);
        LOGGER.debug(res.toString());
        ICommand subcommand = res.get("subcommand");
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
        ArgumentParser parser = ArgumentParsers.newArgumentParser(PROGRAM_NAME)
                .defaultHelp(true)
                .description("Files and Jobs operations with Xenon")
                .version(PROGRAM_VERSION);
        parser.addArgument("--version").action(Arguments.version());

        parser.addArgument("--scheme").choices(getSchemeNames())
                .setDefault(getDefaultSchemeName())
                .help("Xenon scheme to use for subcommands");
        parser.addArgument("--json").action(Arguments.storeTrue()).setConst(false).help("Output in JSON format");

        ArgumentGroup propGroup = parser.addArgumentGroup("optional adaptor properties");
        propGroup.addArgument("--prop")
                .action(Arguments.append())
                .metavar("KEY=VALUE")
                .help("Xenon adaptor properties, " + getSupportedPropertiesHelp())
                .dest("props");

        addCredentialArguments(parser);

        Subparsers subparsers = parser.addSubparsers().title("subcommands");
        ICommand[] subcommands = {
                new ListCommand(),
                new UploadCommand(),
                new DownloadCommand(),
        };
        for (ICommand subcommand: subcommands) {
            subcommand.buildArgumentParser(subparsers);
        }
        return parser;
    }

    private void addCredentialArguments(ArgumentParser parser) {
        ArgumentGroup credGroup = parser.addArgumentGroup("optional credential arguments");
        credGroup.addArgument("--username").help("Username");
        credGroup.addArgument("--password").help("Password or passphrase");
        credGroup.addArgument("--certfile").help("Certificate file");
    }

    private String getDefaultSchemeName() {
        return "local";
    }

    private List<String> getSchemeNames() throws XenonException {
        ArrayList<String> schemeNames = new ArrayList<String>();
        Xenon xenon = XenonFactory.newXenon(null);
        AdaptorStatus[] adaptors = xenon.getAdaptorStatuses();
        XenonFactory.endXenon(xenon);
        Arrays.stream(adaptors).forEach((adaptor) -> Collections.addAll(schemeNames, adaptor.getSupportedSchemes()));
        return schemeNames;
    }

    private List<XenonPropertyDescription> getSupportedProperties() {
        ArrayList<XenonPropertyDescription> props = new ArrayList<>();
        try {
            Xenon xenon = XenonFactory.newXenon(null);
            AdaptorStatus[] adaptors = xenon.getAdaptorStatuses();
            Arrays.stream(adaptors).forEach((adaptor) -> Collections.addAll(props, adaptor.getSupportedProperties()));
            XenonFactory.endXenon(xenon);
        } catch (XenonException e) {
            e.printStackTrace();
        }
        return props;
    }

    private String getSupportedPropertiesHelp() {
        String sep = System.getProperty("line.separator");
        List<String> helps = getSupportedProperties()
                .stream().distinct().map(
                    (property) -> "- " + property.getName() + "=" + property.getDefaultValue() + " ("+ property.getDescription() + ", type:" + property.getType() + ") "
                ).collect(Collectors.toList());

        helps.add(0, "Supported properties:");
        return String.join(sep, helps);
    }
}
