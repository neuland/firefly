package de.neuland.firefly.web;

import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.event.EventService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class ApplicationListener implements ServletContextListener {
    @Override public void contextInitialized(ServletContextEvent servletContextEvent) {
        Registry.getApplicationContext().getBean(EventService.class).
                        publishEvent(new ApplicationStartupEvent());
    }

    @Override public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
