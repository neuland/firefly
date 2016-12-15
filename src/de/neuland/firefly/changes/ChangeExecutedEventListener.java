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
    @Autowired private TenantService tenantService;
    @Autowired private ClusterService clusterService;
    @Autowired private ApplicationContext applicationContext;

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

    @Override public void afterPropertiesSet() {
        super.afterPropertiesSet();
        super.setTenantService(tenantService);
        super.setClusterService(clusterService);
        super.setApplicationContext(applicationContext);
    }
}
