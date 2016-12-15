package de.neuland.firefly.extensionfinder;

import de.hybris.bootstrap.config.ExtensionInfo;
import de.hybris.platform.core.Registry;
import de.hybris.platform.util.Utilities;
import de.neuland.firefly.HybrisAdapter;
import de.neuland.firefly.migration.MigrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class FireflySystemFactory {
    @Autowired private FireflyExtensionRepository fireflyExtensionRepository;
    @Autowired private MigrationRepository migrationRepository;
    @Autowired private HybrisAdapter hybrisAdapter;
    @Autowired private SystemUpdateEventListener systemUpdateEventListener;
    @Autowired private HmcResetEventListener hmcResetEventListener;
    @Value("${firefly.relaxedMode:true}") private boolean relaxedMode;

    public FireflySystem createFireflySystem() {
        return new FireflySystem(hybrisAdapter, createExtensions());
    }

    public void destroyFireflySystem(FireflySystem fireflySystem) {
        for (FireflyExtension fireflyExtension : fireflySystem.getExtensions()) {
            systemUpdateEventListener.unregisterListener(fireflyExtension);
            hmcResetEventListener.unregisterListener(fireflyExtension);
        }
    }

    List<FireflyExtension> createExtensions() {
        List<String> extensionNames = Registry.getMasterTenant().getTenantSpecificExtensionNames();
        List<FireflyExtension> result = new ArrayList<>(extensionNames.size());
        for (String extensionName : extensionNames) {
            result.add(createFireflyExtension(extensionName));
        }

        return result;
    }

    private FireflyExtension createFireflyExtension(String extensionName) {
        ExtensionInfo extensionInfo = Utilities.getExtensionInfo(extensionName);
        FireflyExtension fireflyExtension = new FireflyExtension(extensionInfo.getName(),
                                                                 extensionInfo.getExtensionDirectory(),
                                                                 fireflyExtensionRepository,
                                                                 migrationRepository,
                                                                 relaxedMode);
        systemUpdateEventListener.registerListener(hybrisAdapter.getTenantId(), fireflyExtension);
        hmcResetEventListener.registerListener(hybrisAdapter.getTenantId(), fireflyExtension);
        return fireflyExtension;
    }
}
