package de.neuland.firefly.extensionfinder;

import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.exceptions.FlexibleSearchException;
import de.neuland.firefly.model.FireflyExtensionModel;
import de.neuland.firefly.model.FireflyExtensionStateModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.ArrayList;


@Repository
@Scope("prototype")
public class FireflyExtensionRepository {
    @Autowired ModelService modelService;
    @Autowired FlexibleSearchService searchService;

    public FireflyExtensionModel findByName(String name) throws FireflyExtensionNotFoundException, FireflyNotInstalledException {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(
                        "SELECT {" + FireflyExtensionModel.PK + "} " +
                        "FROM {" + FireflyExtensionModel._TYPECODE + "} " +
                        "WHERE {" + FireflyExtensionModel.NAME + "} = ?name");
        query.addQueryParameter("name", name);
        try {
            return searchService.searchUnique(query);
        } catch (FlexibleSearchException e) {
            throw new FireflyNotInstalledException(e);
        } catch (ModelNotFoundException e) {
            throw new FireflyExtensionNotFoundException(name);
        }
    }

    public FireflyExtensionModel create(String name) {
        Assert.hasText(name);
        FireflyExtensionModel result = new FireflyExtensionModel();
        result.setName(name);
        result.setStates(new ArrayList<FireflyExtensionStateModel>());
        return result;
    }

    public void save(FireflyExtensionModel model) {
        modelService.save(model);
    }

    public static class FireflyNotInstalledException extends RuntimeException {
        public FireflyNotInstalledException(Exception e) {
            super(e);
        }
    }

    public static class FireflyExtensionNotFoundException extends Exception {
        private String name;

        public FireflyExtensionNotFoundException(String name) {
            super("No " + FireflyExtensionModel._TYPECODE + " found for name=" + name);
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
