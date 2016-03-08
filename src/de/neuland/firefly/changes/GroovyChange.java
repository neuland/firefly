package de.neuland.firefly.changes;

import de.hybris.platform.core.Registry;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
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
        Log4JPrintStream log4JPrintStream = new Log4JPrintStream(getChangeLogger(), new PrintStream(output));
        try {
            Binding binding = new Binding(createShellContext(log4JPrintStream));
            new GroovyShell(binding).evaluate(getChangeContent());
        } catch (Exception e) {
            throw new ChangeExecutionException(e, this);
        } finally {
            log4JPrintStream.close();
            try {
                setExecutionLog(output.toString("UTF-8").trim());
            } catch (UnsupportedEncodingException e) {
                LOG.fatal("", e);
            }
        }
    }

    private Map<String, Object> createShellContext(Log4JPrintStream output) {
        final Map<String, Object> shellContext = new HashMap<>();
        shellContext.put("ctx", Registry.getApplicationContext());
        shellContext.put("out", output);
        return shellContext;
    }
}
