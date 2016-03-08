package de.neuland.firefly.changes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Log4JPrintStream extends PrintStream {
    private final Pattern ENDS_WITH_LINEBREAK = Pattern.compile("(.*)([\r\n]{1,2}?)");
    private final Logger logger;
    private final Level priority;
    private StringBuffer lineBuffer = new StringBuffer();

    public Log4JPrintStream(Logger logger, PrintStream upstream) {
        this(logger, Level.INFO, upstream);
    }

    public Log4JPrintStream(Logger logger, Level priority, PrintStream upstream) {
        super(upstream);
        Assert.notNull(logger);
        Assert.notNull(priority);
        Assert.notNull(upstream);
        this.logger = logger;
        this.priority = priority;
    }

    @Override public PrintStream append(CharSequence csq) {
        return super.append(csq);
    }

    @Override public void write(int x) {
        lineBuffer.append(x);
    }

    @Override public void print(boolean x) {
        lineBuffer.append(x);
    }

    @Override public void print(char x) {
        lineBuffer.append(x);
    }

    @Override public void print(int x) {
        lineBuffer.append(x);
    }

    @Override public void print(long x) {
        lineBuffer.append(x);
    }

    @Override public void print(float x) {
        lineBuffer.append(x);
    }

    @Override public void print(double x) {
        lineBuffer.append(x);
    }

    @Override public void print(char[] x) {
        lineBuffer.append(x);
    }

    @Override public void print(String x) {
        Matcher matcher = ENDS_WITH_LINEBREAK.matcher(x);
        if (matcher.matches()) {
            lineBuffer.append(matcher.group(1));
            printCompleteLineAsNeeded();
        } else {
            lineBuffer.append(x);
        }
    }

    @Override public void print(Object x) {
        if (x instanceof String) {
            print((String) x);
        } else {
            lineBuffer.append(x);
        }
    }

    @Override public void println() {
        printCompleteLineAsNeeded();
    }

    @Override public void println(boolean x) {
        print(x);
        printCompleteLineAsNeeded();
    }

    @Override public void println(char x) {
        print(x);
        printCompleteLineAsNeeded();
    }

    @Override public void println(int x) {
        print(x);
        printCompleteLineAsNeeded();
    }

    @Override public void println(long x) {
        print(x);
        printCompleteLineAsNeeded();
    }

    @Override public void println(float x) {
        print(x);
        printCompleteLineAsNeeded();
    }

    @Override public void println(double x) {
        print(x);
        printCompleteLineAsNeeded();
    }

    @Override public void println(char[] x) {
        print(x);
        logger.log(priority, new String(x));
    }

    @Override public void println(String x) {
        print(x);
        printCompleteLineAsNeeded();
    }

    @Override public void println(Object x) {
        print(x);
        printCompleteLineAsNeeded();
    }

    @Override public void flush() {
        printCompleteLineAsNeeded();
        super.flush();
    }

    @Override public void close() {
        printCompleteLineAsNeeded();
        super.close();
    }

    private void printCompleteLineAsNeeded() {
        if (lineBuffer.length() > 0) {
            String line = lineBuffer.toString();
            lineBuffer = new StringBuffer();
            super.print("[" + priority + "]\t" + line);
            super.println();
            logger.log(priority, line);
        }
    }
}
