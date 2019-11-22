package com.carson.quicker.logger.formatter;

import com.carson.quicker.logger.Utils;

import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author yanxu QQ:981385016
 * @name QLogger
 * @class name：com.skyguard.carson.log.formatter
 * @time 2018/10/19 11:34
 * @desc describe
 */
public class FileFormatter extends Formatter {
    private static final String LINE_SEPARATOR = "\n";

    public FileFormatter() {
        //unused
    }

    private String getLevelName(Level level) {
        if (Level.FINE == level) {
            return "DEBUG ";
        } else if (Level.INFO == level) {
            return "INFO  ";
        } else if (Level.WARNING == level) {
            return "WARN  ";
        } else if (Level.SEVERE == level) {
            return "ERROR ";
        } else if (Level.ALL == level) {
            return "TRACE ";
        }
        return "TRACE ";
    }

    /**
     * Converts a object into a human readable string
     * representation.
     */
    @Override
    public String format(LogRecord r) {
        StringBuilder sb = new StringBuilder();
        sb.append(MessageFormat.format("{0,date,MM-dd HH:mm:ss.SSS} ",
                new Object[]{new Date(r.getMillis())}));
//        sb.append(r.getLoggerName()).append(": ");
        sb.append(getLevelName(r.getLevel()));
        sb.append(formatMessage(r));
        sb.append(LINE_SEPARATOR);
        if (r.getThrown() != null) {
            sb.append("Throwable : ");
            sb.append(Utils.getStackTraceString(r.getThrown()));
        }
        return sb.toString();
    }

}
