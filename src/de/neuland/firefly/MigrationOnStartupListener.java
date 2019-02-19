package de.neuland.firefly;

import de.neuland.firefly.web.ApplicationStartupEvent;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


@Component
public class MigrationOnStartupListener implements ApplicationListener<ApplicationStartupEvent>, ApplicationContextAware, InitializingBean {
    private static final Logger LOG = Logger.getLogger(MigrationOnStartupListener.class);
    private FireflyService fireflyService;
    private boolean automaticMigration = false;

    @Autowired
    public MigrationOnStartupListener(FireflyService fireflyService,
                                      @Value("${firefly.migrationOnStartup:false}") boolean automaticMigration) {
        this.fireflyService = fireflyService;
        this.automaticMigration = automaticMigration;
    }

    @Override
    public void onApplicationEvent(ApplicationStartupEvent applicationStartupEvent) {
        if (automaticMigration) {
            LOG.debug("Starting automatic migration after startup.");
            fireflyService.migrate();
        } else {
            LOG.debug("Skipping automatic migration. To change this set the parameter 'firefly.migrationOnStartup' to true.");
            fireflyService.simulate();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // do nothing
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // do nothing
    }
}
