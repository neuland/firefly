package de.neuland.firefly.utils;

import de.hybris.platform.core.Registry;
import de.neuland.firefly.changes.Change;
import de.neuland.firefly.changes.Log4JPrintStream;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;


@Service
public class GroovyScriptRunner {
    public Object execute(Change change, String script) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Log4JPrintStream log4JPrintStream = new Log4JPrintStream(change.getChangeLogger(), new PrintStream(output));
        try {
            Binding binding = new Binding(createShellContext(log4JPrintStream));
            return new GroovyShell(binding).evaluate(script);
        } finally {
            log4JPrintStream.close();
        }
    }

    private Map<String, Object> createShellContext(Log4JPrintStream output) {
        final Map<String, Object> shellContext = new HashMap<>();
        shellContext.put("ctx", Registry.getApplicationContext());
        shellContext.put("out", output);
        return shellContext;
    }
}
