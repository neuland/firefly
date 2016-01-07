package de.neuland.firefly;

import de.neuland.firefly.extensionfinder.FireflySystem;
import de.neuland.firefly.extensionfinder.FireflySystemFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


/**
 * This service holds the main public interface for the Firefly migration tools.
 */
@Service
@Scope("tenant")
public class FireflyService {
    private static final Logger LOG = Logger.getLogger(FireflyService.class);
    @Autowired FireflySystemFactory fireflySystemFactory;

    public void migrate() {
        LOG.info("Starting migration...");
        try {
            fireflySystemFactory.createFireflySystem().update();
        } catch (Exception e) {
            //TODO error handling
            throw new RuntimeException(e);
        }
        LOG.info("...migration complete.");
    }

    public String simulate() {
        StringBuilder resultMessage = new StringBuilder();
        FireflySystem fireflySystem = fireflySystemFactory.createFireflySystem();
        resultMessage.append(fireflySystem.isUpdateRequired() ? "System update is required" : "System update is not required").append("\n");
        resultMessage.append(fireflySystem.isHmcResetRequired() ? "hMC reset is required" : "hMC reset is not required");
        LOG.info(resultMessage.toString());
        return resultMessage.toString();
    }
}
