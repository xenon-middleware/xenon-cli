package nl.esciencecenter.xenon.cli;

import nl.esciencecenter.xenon.XenonException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;

import static org.junit.Assert.assertTrue;

/**
 * Tests on main class, which cause System.exit
 *
 * Separated from rest of tests, because normal asserts are not reached when an exit occurs
 * must perform asserts in checkAssertionAfterwards callback
 */
public class MainExitTest {
    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog();

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void mainRootHelp() throws XenonException {
        exit.expectSystemExitWithStatus(2);
        exit.checkAssertionAfterwards(() -> assertTrue("System out starts with 'usage: xenon'", systemOutRule.getLog().startsWith("usage: xenon")));

        String[] args = {"--help"};
        Main main = new Main();
        main.run(args);
    }

    @Test
    public void run_argumentparserexception_usageinstdout() throws XenonException {
        exit.expectSystemExitWithStatus(2);
        exit.checkAssertionAfterwards(() -> {
            String expected = "error: invalid choice: 'badadaptorname'";
            assertTrue("Stderr contains: " + expected, systemErrRule.getLog().contains(expected));
        });
        Main main = new Main();

        String[] badCommands = new String[]{"badadaptorname"};
        main.run(badCommands);
    }
}
