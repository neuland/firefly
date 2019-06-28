package de.neuland.firefly.changes;

import de.hybris.platform.core.Registry;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
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
            ApplicationContext applicationContext = Registry.getApplicationContext();
            Binding binding = new Binding(createShellContext(applicationContext, log4JPrintStream));
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

    public static Map<String, Object> createShellContext(ApplicationContext applicationContext,
                                                         Log4JPrintStream output) {
        final Map<String, Object> shellContext = new HashMap<>();

        // hybris 5
        shellContext.put("ctx", applicationContext);
        // hybris 6
        shellContext.put("spring", applicationContext);
        bindSpringBeans(applicationContext, shellContext);

        // all versions
        shellContext.put("out", output);

        return shellContext;
    }

    private static void bindSpringBeans(ApplicationContext applicationContext, Map<String, Object> shellContext) {
        if (applicationContext instanceof GenericApplicationContext) {
            GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;

            for (String beanName : genericApplicationContext.getBeanDefinitionNames()) {
                BeanDefinition beanDefinition = genericApplicationContext.getBeanDefinition(beanName);
                if (beanDefinition.isAbstract() || beanName.contains(".")) {
                    continue;
                }
                String[] aliases = genericApplicationContext.getAliases(beanName);
                try {
                    Object bean = applicationContext.getBean(beanName);
                    if (aliases.length > 0) {
                        for (String alias : aliases) {
                            shellContext.put(alias, bean);
                        }
                    } else {
                        shellContext.put(beanName, bean);
                    }
                } catch (Exception e) {
                    LOG.warn(String.format("Failed to bind %s to Groovy script: %s",
                                           beanName, e.getMessage()));
                }
            }
        }
    }
}
