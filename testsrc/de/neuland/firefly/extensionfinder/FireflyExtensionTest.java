package de.neuland.firefly.extensionfinder;

import de.neuland.firefly.model.FireflyExtensionModel;
import de.neuland.firefly.model.FireflyExtensionStateModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xml.sax.SAXException;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import static de.neuland.firefly.TestUtils.unwrapException;
import static de.neuland.firefly.constants.FireflyConstants.NO_HMC_XML_MD5;
import static de.neuland.firefly.constants.FireflyConstants.NO_ITEMS_XML_MD5;
import static de.neuland.firefly.utils.MD5Util.generateMD5;
import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.getTempDirectory;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class FireflyExtensionTest {
    private static final String EXTENSION_WITH_INVALID_ITEMS_XML = "invalid";
    private static final String EXTENSION_WITH_ITEMS_XML = "firefly-junit";
    private static final String EXTENSION_WITHOUT_ITEMS_XML = "none";
    private static final String ITEMS_XML_CONTENT = "<items><a>someValue</a><b/></items>";
    private static final String HMC_XML_CONTENT = "<configuration/>";
    private FireflyExtension fireflyExtension;
    @Mock private FireflyExtensionRepository fireflyExtensionRepository;
    @Captor ArgumentCaptor<ArrayList<FireflyExtensionStateModel>> statesCaptor;

    @Before
    public void setUp() throws Exception {
        fireflyExtension = createExtension(EXTENSION_WITH_ITEMS_XML, false);
    }

    @Test
    public void shouldGetItemsDefinitionHash() throws Exception {
        // when
        String resultHash = fireflyExtension.getItemsDefinitionHash();
        // then
        assertEquals(generateMD5(ITEMS_XML_CONTENT), resultHash);
    }

    @Test
    public void shouldGetHmcConfigurationHash() throws Exception {
        // when
        String resultHash = fireflyExtension.getHmcDefinitionHash();
        // then
        assertEquals(generateMD5(HMC_XML_CONTENT), resultHash);
    }

    @Test
    public void shouldGetDefaultItemsDefinitionHashIfNoItemsXmlWasFound() throws Exception {
        // given
        fireflyExtension = createExtension(EXTENSION_WITHOUT_ITEMS_XML, false);
        // when
        String resultHash = fireflyExtension.getItemsDefinitionHash();
        // then
        assertEquals(NO_ITEMS_XML_MD5, resultHash);
    }

    @Test
    public void shouldGetDefaultHmcDefinitionHashIfNoItemsXmlWasFound() throws Exception {
        // given
        fireflyExtension = new FireflyExtension(EXTENSION_WITHOUT_ITEMS_XML, getTempDirectory(), fireflyExtensionRepository, false);
        // when
        String resultHash = fireflyExtension.getHmcDefinitionHash();
        // then
        assertEquals(NO_HMC_XML_MD5, resultHash);
    }

    @Test
    public void shouldRequireUpdateForNewExtension() throws Exception {
        // given
        givenExtensionIsNotInRepository(EXTENSION_WITH_ITEMS_XML);
        // when
        boolean updateRequired = fireflyExtension.isUpdateRequired();
        // then
        assertTrue(updateRequired);
    }

    @Test
    public void shouldRequireHmcResetForNewExtension() throws Exception {
        // given
        givenExtensionIsNotInRepository(EXTENSION_WITH_ITEMS_XML);
        // when
        boolean hmcResetRequired = fireflyExtension.isHmcResetRequired();
        // then
        assertTrue(hmcResetRequired);
    }

    @Test
    public void shouldRequireUpdateIfDefinitionHashIsChanged() throws Exception {
        // given
        FireflyExtensionModel fireflyExtensionModel = mockExtension(mockState("SOME_VALUE", null));
        given(fireflyExtensionRepository.findByName(EXTENSION_WITH_ITEMS_XML)).willReturn(fireflyExtensionModel);
        // when
        boolean updateRequired = fireflyExtension.isUpdateRequired();
        // then
        assertTrue(updateRequired);
    }

    @Test
    public void shouldNotRequireUpdateIfDefinitionHashIsInHistory() throws Exception {
        // given
        fireflyExtension = createExtension(EXTENSION_WITH_ITEMS_XML, true);
        FireflyExtensionModel fireflyExtensionModel = mockExtension(mockState(generateMD5(ITEMS_XML_CONTENT), null),
                                                                    mockState("SOME_VALUE", null));
        given(fireflyExtensionRepository.findByName(EXTENSION_WITH_ITEMS_XML)).willReturn(fireflyExtensionModel);
        // when
        boolean updateRequired = fireflyExtension.isUpdateRequired();
        // then
        assertFalse(updateRequired);
    }

    @Test
    public void shouldRequireHmcResetIfDefinitionHashIsChanged() throws Exception {
        // given
        FireflyExtensionModel fireflyExtensionModel = mockExtension(mockState(null, "SOME_VALUE"));
        given(fireflyExtensionRepository.findByName(EXTENSION_WITH_ITEMS_XML)).willReturn(fireflyExtensionModel);
        // when
        boolean hmcResetRequired = fireflyExtension.isHmcResetRequired();
        // then
        assertTrue(hmcResetRequired);
    }

    @Test
    public void shouldNotRequireUpdateIfDefinitionHashHasNotChanged() throws Exception {
        // given
        FireflyExtensionModel fireflyExtensionModel = mockExtension(mockState(generateMD5(ITEMS_XML_CONTENT), null));
        given(fireflyExtensionRepository.findByName(EXTENSION_WITH_ITEMS_XML)).willReturn(fireflyExtensionModel);
        // when
        boolean updateRequired = fireflyExtension.isUpdateRequired();
        // then
        assertFalse(updateRequired);
    }

    @Test
    public void shouldNotRequireHmcResetIfDefinitionHashHasNotChanged() throws Exception {
        // given
        FireflyExtensionModel fireflyExtensionModel = mockExtension(mockState(null, generateMD5(HMC_XML_CONTENT)));
        given(fireflyExtensionRepository.findByName(EXTENSION_WITH_ITEMS_XML)).willReturn(fireflyExtensionModel);
        // when
        boolean hmcResetRequired = fireflyExtension.isHmcResetRequired();
        // then
        assertFalse(hmcResetRequired);
    }

    @Test
    public void shouldNotRequireHmcResetIfDefinitionHashIsInHistory() throws Exception {
        // given
        fireflyExtension = createExtension(EXTENSION_WITH_ITEMS_XML, true);
        FireflyExtensionModel fireflyExtensionModel = mockExtension(mockState(null, generateMD5(HMC_XML_CONTENT)),
                                                                    mockState(null, "SOME_VALUE"));
        given(fireflyExtensionRepository.findByName(EXTENSION_WITH_ITEMS_XML)).willReturn(fireflyExtensionModel);
        // when
        boolean updateRequired = fireflyExtension.isHmcResetRequired();
        // then
        assertFalse(updateRequired);
    }

    @Test
    public void shouldNotRequireUpdateIfExtensionHasNoItemsXml() throws Exception {
        // given
        fireflyExtension = createExtension(EXTENSION_WITHOUT_ITEMS_XML, false);
        FireflyExtensionModel fireflyExtensionModel = mockExtension(mockState(NO_ITEMS_XML_MD5, null));
        given(fireflyExtensionRepository.findByName(EXTENSION_WITHOUT_ITEMS_XML)).willReturn(fireflyExtensionModel);
        // when
        boolean updateRequired = fireflyExtension.isUpdateRequired();
        // then
        assertFalse(updateRequired);
    }

    @Test
    public void shouldUpdateItemsDefinitionHashForNewExtension() throws Exception {
        // given
        givenExtensionIsNotInRepository(EXTENSION_WITH_ITEMS_XML);
        FireflyExtensionModel fireflyExtensionModel = mockExtension();
        given(fireflyExtensionRepository.create(EXTENSION_WITH_ITEMS_XML)).willReturn(fireflyExtensionModel);
        // when
        fireflyExtension.onUpdate();
        // then
        verify(fireflyExtensionRepository).create(EXTENSION_WITH_ITEMS_XML);
    }

    @Test
    public void shouldUpdateItemsDefinitionHash() throws Exception {
        // given
        FireflyExtensionModel fireflyExtensionModel = mockExtension(mockState(null, null));
        given(fireflyExtensionRepository.findByName(EXTENSION_WITH_ITEMS_XML)).willReturn(fireflyExtensionModel);
        // when
        fireflyExtension.onUpdate();
        // then
        verify(fireflyExtensionModel).setStates(statesCaptor.capture());
        assertEquals(2, statesCaptor.getValue().size());
        assertEquals(generateMD5(ITEMS_XML_CONTENT), statesCaptor.getValue().get(1).getItemsDefinitionHash());
        verify(fireflyExtensionRepository).save(fireflyExtensionModel);
    }

    @Test
    public void shouldUpdateHmcDefinitionHashOnUpdate() throws Exception {
        // given
        FireflyExtensionModel fireflyExtensionModel = mockExtension(mockState(null, null));
        given(fireflyExtensionRepository.findByName(EXTENSION_WITH_ITEMS_XML)).willReturn(fireflyExtensionModel);
        // when
        fireflyExtension.onUpdate();
        // then
        verify(fireflyExtensionModel).setStates(statesCaptor.capture());
        assertEquals(2, statesCaptor.getValue().size());
        assertEquals(generateMD5(HMC_XML_CONTENT), statesCaptor.getValue().get(1).getHmcDefinitionHash());
        verify(fireflyExtensionRepository).save(fireflyExtensionModel);
    }

    @Test
    public void shouldUpdateHmcDefinitionHashOnHmcReset() throws Exception {
        // given
        FireflyExtensionModel fireflyExtensionModel = mockExtension(mockState(null, null));
        given(fireflyExtensionRepository.findByName(EXTENSION_WITH_ITEMS_XML)).willReturn(fireflyExtensionModel);
        // when
        fireflyExtension.onHmcReset();
        // then
        verify(fireflyExtensionModel).setStates(statesCaptor.capture());
        assertEquals(2, statesCaptor.getValue().size());
        assertEquals(generateMD5(HMC_XML_CONTENT), statesCaptor.getValue().get(1).getHmcDefinitionHash());
        verify(fireflyExtensionRepository).save(fireflyExtensionModel);
    }

    @Test
    public void shouldKeepItemsDefinitionHashOnHmcReset() throws Exception {
        // given
        FireflyExtensionModel fireflyExtensionModel = mockExtension(mockState(generateMD5(ITEMS_XML_CONTENT), null));
        given(fireflyExtensionRepository.findByName(EXTENSION_WITH_ITEMS_XML)).willReturn(fireflyExtensionModel);
        // when
        fireflyExtension.onHmcReset();
        // then
        verify(fireflyExtensionModel).setStates(statesCaptor.capture());
        assertEquals(2, statesCaptor.getValue().size());
        assertEquals(generateMD5(ITEMS_XML_CONTENT), statesCaptor.getValue().get(1).getItemsDefinitionHash());
        verify(fireflyExtensionRepository).save(fireflyExtensionModel);
    }

    @Test(expected = SAXException.class)
    public void shouldHandleExceptionsWhileParsingItemsDefinition() throws Throwable {
        // given
        FireflyExtension extensionWithInvalidItemsXml = createExtension(EXTENSION_WITH_INVALID_ITEMS_XML, false);
        try {
            // when
            extensionWithInvalidItemsXml.getItemsDefinitionHash();
        } catch (RuntimeException e) {
            unwrapException(e);
        }
    }

    @Test
    public void shouldOverwriteToString() throws Exception {
        // when
        String toString = fireflyExtension.toString();
        // then
        assertTrue(toString.contains(fireflyExtension.getName()));
    }

    private FireflyExtensionModel mockExtension(FireflyExtensionStateModel... states) {
        FireflyExtensionModel fireflyExtensionModel = mock(FireflyExtensionModel.class);
        given(fireflyExtensionModel.getStates()).willReturn(new ArrayList<>(asList(states)));
        return fireflyExtensionModel;
    }

    private FireflyExtensionStateModel mockState(String itemDefinitionHash, String hmcDefinitionHash) {
        FireflyExtensionStateModel state = mock(FireflyExtensionStateModel.class);
        given(state.getItemsDefinitionHash()).willReturn(itemDefinitionHash);
        given(state.getHmcDefinitionHash()).willReturn(hmcDefinitionHash);
        return state;
    }

    private FireflyExtension createExtension(String name, boolean relaxedMode) {
        URL currentClassPathFolder = getClass().getClassLoader().getResource("");
        assertNotNull(currentClassPathFolder);
        return new FireflyExtension(name, new File(currentClassPathFolder.getFile()), fireflyExtensionRepository, relaxedMode);
    }

    private void givenExtensionIsNotInRepository(String name) throws FireflyExtensionRepository.FireflyExtensionNotFoundException {
        given(fireflyExtensionRepository.findByName(name))
                        .willThrow(new FireflyExtensionRepository.FireflyExtensionNotFoundException(name));
    }
}
