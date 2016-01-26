package de.neuland.firefly;

import org.apache.log4j.Logger;

import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.io.StringWriter;


public class FireflyJspWriter extends JspWriter {
    private StringWriter writer;

    protected FireflyJspWriter() {
        super(1000, false);
        writer = new StringWriter(bufferSize);
    }

    @Override public void newLine() throws IOException {
        writer.append("\n");
    }

    @Override public void print(boolean b) throws IOException {
        writer.append(b ? "true" : "false");
    }

    @Override public void print(char c) throws IOException {
        writer.append(c);
    }

    @Override public void print(int i) throws IOException {
        writer.append(Integer.toString(i));
    }

    @Override public void print(long l) throws IOException {
        writer.append(Long.toString(l));
    }

    @Override public void print(float v) throws IOException {
        writer.append(Float.toString(v));
    }

    @Override public void print(double v) throws IOException {
        writer.append(Double.toString(v));
    }

    @Override public void print(char[] chars) throws IOException {
        writer.write(chars);
    }

    @Override public void print(String s) throws IOException {
        writer.append(s);
    }

    @Override public void print(Object o) throws IOException {
        writer.append(o != null ? o.toString() : "null");
    }

    @Override public void println() throws IOException {
        newLine();
    }

    @Override public void println(boolean b) throws IOException {
        print(b);
        newLine();
    }

    @Override public void println(char c) throws IOException {
        print(c);
        newLine();
    }

    @Override public void println(int i) throws IOException {
        print(i);
        newLine();
    }

    @Override public void println(long l) throws IOException {
        print(l);
        newLine();
    }

    @Override public void println(float v) throws IOException {
        print(v);
        newLine();
    }

    @Override public void println(double v) throws IOException {
        print(v);
        newLine();
    }

    @Override public void println(char[] chars) throws IOException {
        print(chars);
        newLine();
    }

    @Override public void println(String s) throws IOException {
        print(s);
        newLine();
    }

    @Override public void println(Object o) throws IOException {
        print(o);
        newLine();
    }

    @Override public void clear() throws IOException {
        writer = new StringWriter(bufferSize);
    }

    @Override public void clearBuffer() throws IOException {
        // no op - this implementation does not use a buffer
    }

    @Override public void write(char[] cbuf, int off, int len) throws IOException {
        writer.write(cbuf, off, len);
    }

    @Override public void flush() throws IOException {
        writer.flush();
    }

    @Override public void close() throws IOException {
        writer.close();
    }

    @Override public int getRemaining() {
        return bufferSize;
    }

    public String getString() {
        return writer.getBuffer().toString();
    }
}
