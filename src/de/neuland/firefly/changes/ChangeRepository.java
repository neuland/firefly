package de.neuland.firefly.changes;

import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.neuland.firefly.model.FireflyChangeModel;
import de.neuland.firefly.model.FireflyExtensionModel;
import de.neuland.firefly.model.FireflyMigrationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import static java.util.Arrays.asList;


@Repository
@Scope("tenant")
public class ChangeRepository {
    @Autowired private ModelService modelService;
    @Autowired private FlexibleSearchService searchService;

    public FireflyChangeModel findChange(String file, String author, String id) throws ChangeNotFoundException {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(
                        "SELECT {" + FireflyChangeModel.PK + "} " +
                        "FROM {" + FireflyChangeModel._TYPECODE + "} " +
                        "WHERE {" + FireflyChangeModel.FILENAME + "} = ?file" +
                        "      AND {" + FireflyChangeModel.AUTHOR + "} = ?author" +
                        "      AND {" + FireflyChangeModel.ID + "} = ?id");
        query.addQueryParameter("file", file);
        query.addQueryParameter("author", author);
        query.addQueryParameter("id", id);
        try {
            return searchService.searchUnique(query);
        } catch (ModelNotFoundException e) {
            throw new ChangeNotFoundException(file, author, id);
        }
    }

    public FireflyChangeModel create(FireflyMigrationModel migration, FireflyExtensionModel extension, String file, String author, String id) {
        Assert.notNull(migration, "migration");
        Assert.notNull(extension, "extension");
        Assert.hasText(file, "file");
        Assert.hasText(author, "author");
        Assert.hasText(id, "id");
        FireflyChangeModel result = modelService.create(FireflyChangeModel.class);
        result.setMigrations(asList(migration));
        result.setExtension(extension);
        result.setFilename(file);
        result.setAuthor(author);
        result.setId(id);
        return result;
    }

    public void save(FireflyChangeModel changeModel) {
        modelService.save(changeModel);
    }

    public static class ChangeNotFoundException extends Exception {
        public ChangeNotFoundException(String file, String author, String id) {
            super("Change " + file + ":" + author + ":" + id + " not found.");
        }
    }
}
