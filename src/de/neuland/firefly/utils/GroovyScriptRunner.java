package de.neuland.firefly.utils;

import de.hybris.platform.core.Registry;
import de.hybris.platform.jdbcwrapper.JDBCConnectionPool;
import de.neuland.firefly.changes.Change;
import de.neuland.firefly.changes.Log4JPrintStream;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.sql.Sql;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static java.util.Collections.unmodifiableMap;
import static org.apache.commons.lang.StringUtils.*;


@Service
public class GroovyScriptRunner {
    private Map<String, Boolean> fireflyContext;

    @Autowired
    public GroovyScriptRunner(@Value("${firefly.context:}") String fireflyContextConfig) {
        Map<String, Boolean> fireflyContext = new HashMap<>();
        for (String context : split(fireflyContextConfig, ",")) {
            if (isNotBlank(context)) {
                fireflyContext.put(trim(context), TRUE);
            }
        }
        this.fireflyContext = unmodifiableMap(fireflyContext);
    }

    public Object execute(Change change, String script) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Log4JPrintStream log4JPrintStream = new Log4JPrintStream(change.getChangeLogger(), new PrintStream(output));
        Connection connection = null;

        try {
            connection = getConnectionPool().borrowConnection();
            connection.setReadOnly(true);

            Binding binding = new Binding(createShellContext(log4JPrintStream, connection));
            return new GroovyShell(binding).evaluate(script);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            returnConnection(connection);
            log4JPrintStream.close();
        }
    }

    public boolean evaluate(Change change, String script) {
        Binding binding = new Binding();
        binding.setVariable("x", execute(change, script));
        return (boolean) new GroovyShell(binding).evaluate("x?true:false");
    }

    private Map<String, Object> createShellContext(Log4JPrintStream output, Connection connection) {
        final Map<String, Object> shellContext = new HashMap<>();
        shellContext.put("ctx", Registry.getApplicationContext());
        shellContext.put("out", output);
        shellContext.put("fireflyContext", fireflyContext);
        shellContext.put("sql", new Sql(connection));
        return shellContext;
    }

    private void returnConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.setReadOnly(false);
                getConnectionPool().returnConnection(connection);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static JDBCConnectionPool getConnectionPool() {
        return Registry.getCurrentTenant().getDataSource().getConnectionPool();
    }
}
