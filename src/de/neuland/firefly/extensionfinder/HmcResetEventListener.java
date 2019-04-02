package de.neuland.firefly.extensionfinder;

import de.hybris.platform.servicelayer.cluster.ClusterService;
import de.hybris.platform.servicelayer.tenant.TenantService;
import de.neuland.firefly.EventDistributor;
import de.neuland.firefly.HybrisAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("singleton")
public class HmcResetEventListener extends EventDistributor<HybrisAdapter.HmcResetEvent, FireflyExtension> {

    @Autowired
    public HmcResetEventListener(ClusterService clusterService,
                                 TenantService tenantService) {
        super(clusterService, tenantService);
    }

    @Override protected void onEvent(HybrisAdapter.HmcResetEvent event) {
        if (!hasListenersForTenant(event.getTenantId())) {
            return;
        }

        for (FireflyExtension fireflyExtension : getListenersForTenant(event.getTenantId())) {
            fireflyExtension.onHmcReset(event.getMigration());
        }
    }
}
