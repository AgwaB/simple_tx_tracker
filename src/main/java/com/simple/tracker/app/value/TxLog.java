package com.simple.tracker.app.value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TxLog {
    private static Logger logger = LoggerFactory.getLogger("txLogger");

    public static void info(String logName, String ...keyvals) {
        StringBuffer log = new StringBuffer();
        if (keyvals.length == 0 ) {
            return;
        }

        log.append("[" + logName + "]");

        // TODO : 홀수 일 때
        for(int i = 0 ; i < keyvals.length ; i+=2) {
            log.append("\n");
            log.append(keyvals[i]);
            log.append(" : ");
            log.append(keyvals[i+1]);
        }

        logger.info(log.toString());
    }
}
