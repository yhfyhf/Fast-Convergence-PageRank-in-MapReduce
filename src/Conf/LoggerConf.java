package Conf;

import java.util.logging.*;

/**
 * Created by yhf on 4/25/16.
 */
public class LoggerConf {

    public static Logger getInfoLogger() {
        Logger infoLogger = Logger.getLogger("Info Logger");
        infoLogger.setLevel(Level.INFO);
        return infoLogger;
    }

    public static Logger getFineLogger() {
        Logger fineLogger = Logger.getLogger("Fine Logger");
        fineLogger.setLevel(Level.FINE);
        return fineLogger;
    }

    public static Logger getWarningLogger() {
        Logger warningLogger = Logger.getLogger("Warning Logger");
        warningLogger.setLevel(Level.WARNING);
        return warningLogger;
    }
}
