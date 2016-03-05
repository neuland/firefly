package de.neuland.firefly.changes;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;
import de.hybris.platform.core.Registry;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;


public class BeanShellChange extends Change {
    private static final Logger LOG = Logger.getLogger(BeanShellChange.class);

    protected BeanShellChange(ChangeBasic changeBasic, ChangeDependency changeDependency) {
        super(changeBasic, changeDependency);
    }

    @Override void executeChange() throws ChangeExecutionException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream outputPrintStream = new PrintStream(output);
        Logger changeLogger = Logger.getLogger(toString());
        try {
            Interpreter interpreter = new Interpreter();
            interpreter.setNameSpace(new NameSpace(interpreter.getClassManager(), "hybris"));
            interpreter.set("ctx", Registry.getApplicationContext());
            interpreter.setOut(new Log4JPrintStream(changeLogger, Level.INFO, outputPrintStream));
            interpreter.setErr(new Log4JPrintStream(changeLogger, Level.ERROR, outputPrintStream));
            interpreter.eval(getChangeContent());
        } catch (EvalError e) {
            throw new ChangeExecutionException(e, this);
        } finally {
            try {
                setExecutionLog(output.toString("UTF-8").trim());
            } catch (UnsupportedEncodingException e) {
                LOG.fatal("", e);
            }
        }
    }
}
