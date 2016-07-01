package de.neuland.firefly.ant;

import de.hybris.ant.taskdefs.AbstractAntPerformable;
import de.hybris.platform.core.Registry;
import de.neuland.firefly.FireflyService;
import org.apache.log4j.Logger;


public class SimulateAntTask extends AbstractAntPerformable {
    private static final Logger LOG = Logger.getLogger(SimulateAntTask.class);

    public SimulateAntTask(String tenantId) {
        super(tenantId);
    }

    @Override protected void performImpl() throws Exception {
        Registry.getApplicationContext().getBean(FireflyService.class).simulate();
    }
}
