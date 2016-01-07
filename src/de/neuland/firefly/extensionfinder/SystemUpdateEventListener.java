package de.neuland.firefly.extensionfinder;

import de.neuland.firefly.HybrisAdapter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("singleton")
public class SystemUpdateEventListener extends EventDistributor<HybrisAdapter.SystemUpdatedEvent> {
    @Override protected void onEvent(HybrisAdapter.SystemUpdatedEvent event) {
        if (!hasListenersForTenant(event.getTenantId())) {
            return;
        }

        for (FireflyExtension fireflyExtension : getListenersForTenant(event.getTenantId())) {
            fireflyExtension.onUpdate();
        }
    }
}
