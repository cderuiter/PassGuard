package test;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Provides static methods to setup class-level loggers.
 *
 * @author Dimitri Lialios 01/23/2019
 */
public final class LoggerSetup {

    /**
     * Sets up a logger whose output will be written to a file.
     *
     * @param className    the name of the class this logger will be used in
     * @param logFileTitle the title of the log file that will be created
     * @return             the logger object to be stored in this class
     */
    public static Logger initLogger(String className, String logFileTitle) {
        Logger logger = Logger.getLogger(className);
        logger.setUseParentHandlers(false);
        try {
            FileHandler fHandler = new FileHandler(logFileTitle, true);
            fHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fHandler);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
        return logger;
    }

}
