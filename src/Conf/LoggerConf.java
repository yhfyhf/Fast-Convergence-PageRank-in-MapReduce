package Conf;

import java.util.logging.*;

/**
 * Created by yhf on 4/25/16.
 */
public class LoggerConf {

    public static Logger getInfoLogger() {
        Logger infoLogger = Logger.getLogger("Info Logger");
        infoLogger.setLevel(Level.INFO);
        Handler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        infoLogger.addHandler(handler);
        return infoLogger;
    }

    public static Logger getConfigLogger() {
        Logger configLogger = Logger.getLogger("Config Logger");
        configLogger.setLevel(Level.CONFIG);
        Handler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        configLogger.addHandler(handler);
        return configLogger;
    }

    public static Logger getWarningLogger() {
        Logger warningLogger = Logger.getLogger("Warning Logger");
        warningLogger.setLevel(Level.WARNING);
        Handler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        warningLogger.addHandler(handler);
        return warningLogger;
    }
}
