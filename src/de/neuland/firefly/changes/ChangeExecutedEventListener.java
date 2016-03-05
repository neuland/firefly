package de.neuland.firefly.changes;

import de.neuland.firefly.EventDistributor;
import de.neuland.firefly.extensionfinder.FireflyExtensionRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("singleton")
public class ChangeExecutedEventListener extends EventDistributor<ChangeExecutedEvent, Change> {
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
