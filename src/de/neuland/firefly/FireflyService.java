package de.neuland.firefly;

import de.hybris.platform.core.Registry;
import de.neuland.firefly.changes.Change;
import de.neuland.firefly.changes.ChangeFactory;
import de.neuland.firefly.changes.ChangeList;
import de.neuland.firefly.extensionfinder.FireflyExtensionRepository;
import de.neuland.firefly.extensionfinder.FireflySystem;
import de.neuland.firefly.extensionfinder.FireflySystemFactory;
import de.neuland.firefly.migration.MigrationRepository;
import de.neuland.firefly.model.FireflyMigrationModel;
import org.apache.log4j.Logger;
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

    public void migrate() {
        try {
            LOG.info("Starting migration for tenant " + Registry.getCurrentTenant() + " ...");
            performMigration();
            LOG.info("...migration complete.");
        } catch (FireflyExtensionRepository.FireflyNotInstalledException e) {
            LOG.warn("...Firefly is not installed yet. A update is performed to install firefly.");
            try {
                Registry.getGlobalApplicationContext().getBean(HybrisAdapter.class).initFirefly();
                LOG.info("...Firefly has been installed.");
                performMigration();
            } catch (Exception initException) {
                throw new RuntimeException(initException);
            }
        }
    }

    private void performMigration() throws FireflyExtensionRepository.FireflyNotInstalledException {
        FireflySystemFactory fireflySystemFactory = Registry.getGlobalApplicationContext().getBean(FireflySystemFactory.class);
        FireflySystem fireflySystem = fireflySystemFactory.createFireflySystem();
        try {
            FireflyMigrationModel migration = null;
            if (fireflySystem.isUpdateRequired() || fireflySystem.isHmcResetRequired()) {
                migration = getOrCreateMigration(migration);
                fireflySystem.update(migration);
            }
            ChangeList changeList = Registry.getGlobalApplicationContext().getBean(ChangeFactory.class).createChangeList();
            if (!changeList.getChangesThatRequiredExecution().isEmpty()) {
                migration = getOrCreateMigration(migration);
                changeList.executeChanges(migration);
            }
        } catch (FireflyExtensionRepository.FireflyNotInstalledException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (fireflySystemFactory != null) {
                fireflySystemFactory.destroyFireflySystem(fireflySystem);
            }
        }
    }

    private FireflyMigrationModel getOrCreateMigration(FireflyMigrationModel migration) {
        if (migration == null) {
            MigrationRepository migrationRepository = Registry.getGlobalApplicationContext().getBean(MigrationRepository.class);
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

        FireflySystemFactory fireflySystemFactory = Registry.getGlobalApplicationContext().getBean(FireflySystemFactory.class);
        ChangeFactory changeFactory = Registry.getGlobalApplicationContext().getBean(ChangeFactory.class);
        FireflySystem fireflySystem = fireflySystemFactory.createFireflySystem();
        try {
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
        } catch (FireflyExtensionRepository.FireflyNotInstalledException e) {
            resultMessage.append("Firefly is not installed yet. A update is required to run simulation.");
        } finally {
            if (fireflySystemFactory != null) {
                fireflySystemFactory.destroyFireflySystem(fireflySystem);
            }
        }
        LOG.info(resultMessage.toString());
        return resultMessage.toString();
    }
}
