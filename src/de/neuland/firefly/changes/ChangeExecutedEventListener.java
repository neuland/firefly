package de.neuland.firefly.changes;

import de.hybris.platform.servicelayer.cluster.ClusterService;
import de.hybris.platform.servicelayer.tenant.TenantService;
import de.neuland.firefly.EventDistributor;
import de.neuland.firefly.extensionfinder.FireflyExtensionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("singleton")
public class ChangeExecutedEventListener extends EventDistributor<ChangeExecutedEvent, Change> {

    @Autowired
    public ChangeExecutedEventListener(ClusterService clusterService,
                                       TenantService tenantService,
                                       ApplicationContext applicationContext) {
        super(clusterService, tenantService);

        super.setApplicationContext(applicationContext);
    }

    @Override protected void onEvent(ChangeExecutedEvent event) {
        if (!hasListenersForTenant(event.getTenantId())) {
            return;
        }

        try {
            for (Change change : getListenersForTenant(event.getTenantId())) {
                if (change.equals(event.getChange())) {
                    change.onExecution(event.getMigration());
                }
            }
        } catch (FireflyExtensionRepository.FireflyExtensionNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
