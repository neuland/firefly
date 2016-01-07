package de.neuland.firefly.extensionfinder;

import de.neuland.firefly.HybrisAdapter;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;


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

    public void update() throws Exception {
        if (isUpdateRequired()) {
            LOG.info("Starting system update...");
            hybrisAdapter.updateSystem();
            LOG.info("...system update done.");
        } else if (isHmcResetRequired()) {
            LOG.info("Clean hMC configuration.");
            hybrisAdapter.clearHmcConfiguration();
        } else {
            LOG.info("No update required. System is up to date.");
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
}
