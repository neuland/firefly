package de.neuland.firefly.changes;

import de.hybris.platform.core.Registry;
import de.hybris.platform.jdbcwrapper.JDBCConnectionPool;
import de.neuland.firefly.utils.ScriptRunner;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;


public class SqlChange extends Change {
    protected static final Logger LOG = Logger.getLogger(SqlChange.class);

    public SqlChange(ChangeBasic changeBasic, ChangeDependency changeDependency) {
        super(changeBasic, changeDependency);
    }

    @Override void executeChange() throws ChangeExecutionException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream outputPrintStream = new PrintStream(output);
        Logger changeLogger = Logger.getLogger(toString());

        Connection connection = null;
        try {
            connection = getConnectionPool().borrowConnection();
            ScriptRunner scriptRunner = new ScriptRunner(connection, false, true);
            scriptRunner.setLog(new Log4JPrintStream(changeLogger, Level.INFO, outputPrintStream));
            scriptRunner.setErrorLog(new Log4JPrintStream(changeLogger, Level.ERROR, outputPrintStream));
            scriptRunner.runScript(new StringReader(getChangeContent()));
        } catch (Exception e) {
            throw new ChangeExecutionException(e, this);
        } finally {
            if (connection != null) {
                getConnectionPool().returnConnection(connection);
            }
            try {
                setExecutionLog(output.toString("UTF-8").trim());
            } catch (UnsupportedEncodingException e) {
                LOG.fatal("", e);
            }
        }
    }

    private static JDBCConnectionPool getConnectionPool() {
        return Registry.getCurrentTenant().getDataSource().getConnectionPool();
    }
}
