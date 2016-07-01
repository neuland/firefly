package de.neuland.firefly.ant;

import de.hybris.ant.taskdefs.AbstractAntPerformable;
import de.hybris.platform.core.Registry;
import de.neuland.firefly.FireflyService;
import org.apache.log4j.Logger;


public class UpdateAntTask extends AbstractAntPerformable {
    private static final Logger LOG = Logger.getLogger(UpdateAntTask.class);

    public UpdateAntTask(String tenantId) {
        super(tenantId);
    }

    @Override protected void performImpl() throws Exception {
        Registry.getApplicationContext().getBean(FireflyService.class).migrate();
    }
}
