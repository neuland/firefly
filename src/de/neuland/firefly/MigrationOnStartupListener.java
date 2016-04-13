package de.neuland.firefly;

import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.neuland.firefly.web.ApplicationStartupEvent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("singleton")
public class MigrationOnStartupListener extends AbstractEventListener<ApplicationStartupEvent> {
    private static final Logger LOG = Logger.getLogger(MigrationOnStartupListener.class);
    private FireflyService fireflyService;
    private boolean automaticMigration = false;

    @Autowired
    public MigrationOnStartupListener(FireflyService fireflyService,
                                      @Value("${firefly.migrationOnStartup:false}") boolean automaticMigration) {
        this.fireflyService = fireflyService;
        this.automaticMigration = automaticMigration;
    }

    @Override protected void onEvent(ApplicationStartupEvent event) {
        //TODO get a list of tenants and the fireflyService-bean for this scope
        if (automaticMigration) {
            LOG.debug("Starting automatic migration after startup.");
            fireflyService.migrate();
        } else {
            LOG.debug("Skipping automatic migration. To change this set the parameter 'firefly.migrationOnStartup' to true.");
            fireflyService.simulate();
        }
    }
}
