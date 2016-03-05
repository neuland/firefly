package de.neuland.firefly.migration;

import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.model.ModelService;
import de.neuland.firefly.model.FireflyMigrationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;


@Repository
@Scope("tenant")
public class MigrationRepository {
    @Autowired private ModelService modelService;

    public FireflyMigrationModel findByPk(PK pk) {
        return modelService.get(pk);
    }

    public FireflyMigrationModel create() {
        return new FireflyMigrationModel();
    }

    public void save(FireflyMigrationModel migration) {
        modelService.save(migration);
    }
}
