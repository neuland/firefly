package de.neuland.firefly.changes;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.neuland.firefly.model.FireflyChangeModel;
import de.neuland.firefly.model.FireflyExtensionModel;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.assertNotNull;


@IntegrationTest
public class ChangeRepositoryTest extends ServicelayerTransactionalTest {
    @Resource private ChangeRepository changeRepository;
    @Resource private ModelService modelService;

    @Test
    public void shouldFindChange() throws Exception {
        // given
        FireflyExtensionModel extension = new FireflyExtensionModel();
        extension.setName(getClass().getSimpleName());
        modelService.save(extension);
        FireflyChangeModel fireflyChange = new FireflyChangeModel();
        fireflyChange.setExtension(extension);
        fireflyChange.setFilename("firefly-junit-file");
        fireflyChange.setAuthor("firefly-junit");
        fireflyChange.setId("1");
        modelService.save(fireflyChange);
        // when
        FireflyChangeModel result = changeRepository.findChange("firefly-junit-file", "firefly-junit", "1");
        // then
        assertNotNull(result);
    }

    @Test(expected = ChangeRepository.ChangeNotFoundException.class)
    public void shouldNotFindChange() throws Exception {
        // when
        changeRepository.findChange("firefly-junit-file", "firefly-junit", "4711");
    }
}
