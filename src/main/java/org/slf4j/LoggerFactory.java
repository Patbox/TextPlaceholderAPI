package org.slf4j;

public class LoggerFactory {
    public static Logger getLogger(String text) {
        return new Logger();
    }
}
