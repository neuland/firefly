package de.neuland.firefly.extensionfinder;

import de.neuland.firefly.HybrisAdapter;
import de.neuland.firefly.model.FireflyMigrationModel;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.io.File;
import java.util.*;


/**
 * Representation of a hybris tenant and its configuration.
 */
public class FireflySystem {
    private static final Logger LOG = Logger.getLogger(FireflySystem.class);
    private HybrisAdapter hybrisAdapter;
    private List<FireflyExtension> extensions;

    FireflySystem(HybrisAdapter hybrisAdapter, List<FireflyExtension> extensions) {
        Assert.notNull(hybrisAdapter);
        Assert.notNull(extensions);
        this.hybrisAdapter = hybrisAdapter;
        this.extensions = extensions;
    }

    public void update(FireflyMigrationModel migration) throws Exception {
        Assert.notNull(migration);
        if (isUpdateRequired()) {
            LOG.info("Starting system update...");
            hybrisAdapter.updateSystem(migration.getPk());
            LOG.info("...system update done.");
        } else if (isHmcResetRequired()) {
            LOG.info("Clean hMC configuration.");
            hybrisAdapter.clearHmcConfiguration(migration.getPk());
        } else {
            LOG.info("No update required. System is up to date.");
        }
    }

    public void setBaseline(FireflyMigrationModel migration) {
        for (FireflyExtension extension : extensions) {
            if (extension.isUpdateRequired()) {
                extension.onUpdate(migration.getPk());
            } else if (extension.isHmcResetRequired()) {
                extension.onHmcReset(migration.getPk());
            }
        }
    }

    public boolean isHmcResetRequired() {
        boolean resetRequired = false;
        Iterator<FireflyExtension> extensionIterator = extensions.iterator();
        while (!resetRequired && extensionIterator.hasNext()) {
            resetRequired = extensionIterator.next().isHmcResetRequired();
        }
        return resetRequired;
    }

    public boolean isUpdateRequired() {
        boolean updateRequired = false;
        Iterator<FireflyExtension> extensionIterator = extensions.iterator();
        while (!updateRequired && extensionIterator.hasNext()) {
            updateRequired = extensionIterator.next().isUpdateRequired();
        }
        return updateRequired;
    }

    List<FireflyExtension> getExtensions() {
        return extensions;
    }

    public Map<String, File> getExtensionPaths() {
        Map<String, File> result = new HashMap<>(extensions.size());
        for (FireflyExtension extension : extensions) {
            result.put(extension.getName(), extension.getRootPath());
        }
        return Collections.unmodifiableMap(result);
    }
}
