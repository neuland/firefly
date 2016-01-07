package de.neuland.firefly.extensionfinder;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.neuland.firefly.model.FireflyExtensionModel;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.*;


@IntegrationTest
public class FireflyExtensionRepositoryTest extends ServicelayerTransactionalTest {
    @Resource private FireflyExtensionRepository fireflyExtensionRepository;
    @Resource private ModelService modelService;

    @Test
    public void shouldFindByName() throws Exception {
        // given
        FireflyExtensionModel fireflyExtensionModel = new FireflyExtensionModel();
        fireflyExtensionModel.setName("firefly-junit");
        modelService.save(fireflyExtensionModel);
        // when
        FireflyExtensionModel result = fireflyExtensionRepository.findByName("firefly-junit");
        // then
        assertEquals(fireflyExtensionModel, result);
    }

    @Test
    public void shouldNotReturnNullWhileFindByName() throws Exception {
        // given
        final String name = "myName";
        try {
            // when
            fireflyExtensionRepository.findByName(name);
            // then
            fail();
        } catch (FireflyExtensionRepository.FireflyExtensionNotFoundException e) {
            assertEquals(name, e.getName());
        }
    }

    @Test
    public void shouldCreate() throws Exception {
        // given
        String name = "new-extension";
        // when
        FireflyExtensionModel result = fireflyExtensionRepository.create(name);
        // then
        assertNotNull(result);
        assertEquals(name, result.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowCreationWithoutName() throws Exception {
        // when
        fireflyExtensionRepository.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowCreationWithoutEmpty() throws Exception {
        // when
        fireflyExtensionRepository.create("");
    }

    @Test
    public void shouldSave() throws Exception {
        // given
        String name = "value-to-save";
        FireflyExtensionModel fireflyExtensionModel = fireflyExtensionRepository.create(name);
        // when
        fireflyExtensionRepository.save(fireflyExtensionModel);
        // then
        assertEquals(fireflyExtensionModel.getPk(), fireflyExtensionRepository.findByName(name).getPk());
    }
}
