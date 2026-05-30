package appengx.guidebook.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class FabricGuidebookLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger("Fabric Guidebook");

    private FabricGuidebookLogger() {
    }

    static void error(String message, Object arg, Throwable throwable) {
        LOGGER.error(message, arg, throwable);
    }
}
