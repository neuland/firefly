package de.neuland.firefly;

import de.neuland.firefly.changes.Change;
import de.neuland.firefly.changes.ChangeFactory;
import de.neuland.firefly.changes.ChangeList;
import de.neuland.firefly.extensionfinder.FireflySystem;
import de.neuland.firefly.extensionfinder.FireflySystemFactory;
import de.neuland.firefly.migration.MigrationRepository;
import de.neuland.firefly.model.FireflyMigrationModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * This service holds the main public interface for the Firefly migration tools.
 */
@Service
@Scope("tenant")
public class FireflyService {
    private static final Logger LOG = Logger.getLogger(FireflyService.class);
    @Autowired MigrationRepository migrationRepository;
    @Autowired FireflySystemFactory fireflySystemFactory;
    @Autowired ChangeFactory changeFactory;

    public void migrate() {
        LOG.info("Starting migration...");
        try {
            FireflyMigrationModel migration = null;
            FireflySystem fireflySystem = fireflySystemFactory.createFireflySystem();
            if (fireflySystem.isUpdateRequired() || fireflySystem.isHmcResetRequired()) {
                migration = getOrCreateMigration(migration);
                fireflySystem.update(migration);
            }
            ChangeList changeList = changeFactory.createChangeList();
            if (!changeList.getChangesThatRequiredExecution().isEmpty()) {
                migration = getOrCreateMigration(migration);
                changeList.executeChanges(migration);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        LOG.info("...migration complete.");
    }

    private FireflyMigrationModel getOrCreateMigration(FireflyMigrationModel migration) {
        if (migration == null) {
            FireflyMigrationModel result = migrationRepository.create();
            migrationRepository.save(result);
            return result;
        } else {
            return migration;
        }
    }

    public String simulate() {
        StringBuilder resultMessage = new StringBuilder();
        resultMessage.append("Running simulation: \n");

        FireflySystem fireflySystem = fireflySystemFactory.createFireflySystem();
        resultMessage.append("- ").append(fireflySystem.isUpdateRequired() ? "System update is required" : "System update is not required").append("\n");
        resultMessage.append("- ").append(fireflySystem.isHmcResetRequired() ? "hMC reset is required" : "hMC reset is not required").append("\n");

        try {
            List<Change> changesThatRequiredExecution = changeFactory.createChangeList().getChangesThatRequiredExecution();
            if (changesThatRequiredExecution.isEmpty()) {
                resultMessage.append("- No new changes found.");
            } else {
                resultMessage.append("- Changes found:").append("\n");
                for (Change change : changesThatRequiredExecution) {
                    resultMessage.append("\t").append(change).append("\n");
                }
            }
        } catch (Change.ChangeModifiedException changeModifiedException) {
            resultMessage.append("- ").append(changeModifiedException.getMessage());
        }

        LOG.info(resultMessage.toString());
        return resultMessage.toString();
    }
}
