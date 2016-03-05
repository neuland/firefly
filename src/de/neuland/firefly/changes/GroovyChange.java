package de.neuland.firefly.changes;

import de.hybris.platform.core.Registry;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class GroovyChange extends Change {
    private static final Logger LOG = Logger.getLogger(GroovyChange.class);

    public GroovyChange(ChangeBasic changeBasic, ChangeDependency changeDependency) {
        super(changeBasic, changeDependency);
    }

    @Override void executeChange() throws ChangeExecutionException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            Binding binding = new Binding(createShellContext(output));
            new GroovyShell(binding).evaluate(getChangeContent());
        } catch (Exception e) {
            throw new ChangeExecutionException(e, this);
        } finally {
            try {
                setExecutionLog(output.toString("UTF-8").trim());
            } catch (UnsupportedEncodingException e) {
                LOG.fatal("", e);
            }
        }
    }

    private Map<String, Object> createShellContext(OutputStream output) {
        final Map<String, Object> shellContext = new HashMap<>();
        shellContext.put("ctx", Registry.getApplicationContext());
        shellContext.put("out", new Log4JPrintStream(Logger.getLogger(toString()), new PrintStream(output)));
        return shellContext;
    }
}
