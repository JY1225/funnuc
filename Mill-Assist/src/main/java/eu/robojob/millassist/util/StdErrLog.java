package eu.robojob.millassist.util;

import java.io.PrintStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class StdErrLog {
	
	private StdErrLog() { }

    private static Logger logger = LogManager.getLogger(StdErrLog.class.getName());

    public static void tieSystemOutAndErrToLog() {
       // System.setOut(createLoggingProxy(System.out));
        System.setErr(createLoggingProxy(System.err));
    }

    public static PrintStream createLoggingProxy(final PrintStream realPrintStream) {
        return new PrintStream(realPrintStream) {
            public void print(final String string) {
                realPrintStream.print(string);
                logger.fatal(string);
            }
        };
    }
}