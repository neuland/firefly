package de.neuland.firefly.migration;

import com.google.common.base.Optional;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.neuland.firefly.model.FireflyLockModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;


@Repository
@Scope("prototype")
public class LockRepository {
    @Autowired private ModelService modelService;
    @Autowired private FlexibleSearchService searchService;

    public FireflyLockModel findByPk(PK pk) {
        return modelService.get(pk);
    }

    public FireflyLockModel create() {
        return modelService.create(FireflyLockModel.class);
    }

    public Optional<FireflyLockModel> findLock() {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(
                        "SELECT {" + FireflyLockModel.PK + "} " +
                        "FROM {" + FireflyLockModel._TYPECODE + "}");
        try {
            return Optional.fromNullable((FireflyLockModel) searchService.searchUnique(query));
        } catch (ModelNotFoundException e) {
            return Optional.absent();
        }
    }

    public void save(FireflyLockModel lock) {
        modelService.save(lock);
    }

    public void remove(FireflyLockModel lock) {
        modelService.remove(lock);
    }
}
