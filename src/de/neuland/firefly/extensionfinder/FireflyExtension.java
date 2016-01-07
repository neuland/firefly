package de.neuland.firefly.extensionfinder;

import de.neuland.firefly.model.FireflyExtensionModel;
import de.neuland.firefly.model.FireflyExtensionStateModel;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static de.neuland.firefly.constants.FireflyConstants.NO_HMC_XML_MD5;
import static de.neuland.firefly.constants.FireflyConstants.NO_ITEMS_XML_MD5;
import static de.neuland.firefly.utils.MD5Util.generateMD5;
import static de.neuland.firefly.utils.XMLUtil.loadAndNormalizeXML;
import static java.util.Arrays.asList;


/**
 * Representation of a single hybris extension.
 */
public class FireflyExtension {
    private FireflyExtensionRepository fireflyExtensionRepository;
    private boolean relaxedMode;
    private String name;
    private File rootPath;

    FireflyExtension(String name, File rootPath, FireflyExtensionRepository fireflyExtensionRepository, boolean relaxedMode) {
        this.name = name;
        this.rootPath = rootPath;
        this.fireflyExtensionRepository = fireflyExtensionRepository;
        this.relaxedMode = relaxedMode;
    }

    public boolean isUpdateRequired() {
        try {
            FireflyExtensionModel fireflyExtensionModel = fireflyExtensionRepository.findByName(name);
            return !containsItemDefinitionHash(getItemsDefinitionHash(),
                                               relaxedMode ? fireflyExtensionModel.getStates() : asList(getOrCreateLastState(fireflyExtensionModel)));
        } catch (FireflyExtensionRepository.FireflyExtensionNotFoundException e) {
            return true;
        }
    }

    public boolean isHmcResetRequired() {
        try {
            FireflyExtensionModel fireflyExtensionModel = fireflyExtensionRepository.findByName(name);
            return !containsHmcDefinitionHash(getHmcDefinitionHash(),
                                              relaxedMode ? fireflyExtensionModel.getStates() : asList(getOrCreateLastState(fireflyExtensionModel)));
        } catch (FireflyExtensionRepository.FireflyExtensionNotFoundException e) {
            return true;
        }
    }

    public void onHmcReset() {
        FireflyExtensionModel fireflyExtensionModel = getOrCreateFireflyExtensionModel();
        FireflyExtensionStateModel lastState = getLastState(fireflyExtensionModel);
        String hmcDefinitionHash = getHmcDefinitionHash();
        if (lastState == null || !equalsHash(lastState,
                                             lastState.getItemsDefinitionHash(),
                                             hmcDefinitionHash)) {
            createState(fireflyExtensionModel,
                        lastState != null ? lastState.getItemsDefinitionHash() : getItemsDefinitionHash(),
                        getHmcDefinitionHash());
        }
        fireflyExtensionRepository.save(fireflyExtensionModel);
    }

    public void onUpdate() {
        FireflyExtensionModel fireflyExtensionModel = getOrCreateFireflyExtensionModel();
        String itemsDefinitionHash = getItemsDefinitionHash();
        String hmcDefinitionHash = getHmcDefinitionHash();
        if (!equalsHash(getLastState(fireflyExtensionModel),
                        itemsDefinitionHash,
                        hmcDefinitionHash)) {
            createState(fireflyExtensionModel, itemsDefinitionHash, hmcDefinitionHash);
        }
        fireflyExtensionRepository.save(fireflyExtensionModel);
    }

    String getName() {
        return name;
    }

    File getRootPath() {
        return rootPath;
    }

    String getItemsDefinitionHash() {
        return getHashFromFile(new File(rootPath, "resources/" + name + "-items.xml"), NO_ITEMS_XML_MD5);
    }

    String getHmcDefinitionHash() {
        return getHashFromFile(new File(rootPath, "hmc/resources/hmc.xml"), NO_HMC_XML_MD5);
    }

    @Override public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    private boolean containsItemDefinitionHash(String itemsDefinitionHash, List<FireflyExtensionStateModel> states) {
        for (FireflyExtensionStateModel state : states) {
            if (StringUtils.equals(itemsDefinitionHash, state.getItemsDefinitionHash())) {
                return true;
            }
        }
        return false;
    }

    private boolean containsHmcDefinitionHash(String hmcDefinitionHash, List<FireflyExtensionStateModel> states) {
        for (FireflyExtensionStateModel state : states) {
            if (StringUtils.equals(hmcDefinitionHash, state.getHmcDefinitionHash())) {
                return true;
            }
        }
        return false;
    }

    private boolean equalsHash(FireflyExtensionStateModel lastState, String itemsDefinitionHash, String hmcDefinitionHash) {
        return lastState != null &&
               StringUtils.equals(lastState.getItemsDefinitionHash(), itemsDefinitionHash) &&
               StringUtils.equals(lastState.getHmcDefinitionHash(), hmcDefinitionHash);
    }

    private String getHashFromFile(File xml, String defaultHash) {
        if (xml.exists()) {
            try {
                return generateMD5(loadAndNormalizeXML(xml));
            } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
                throw new RuntimeException(e);
            }
        } else {
            return defaultHash;
        }
    }

    private FireflyExtensionModel getOrCreateFireflyExtensionModel() {
        FireflyExtensionModel fireflyExtensionModel;
        try {
            fireflyExtensionModel = fireflyExtensionRepository.findByName(name);
        } catch (FireflyExtensionRepository.FireflyExtensionNotFoundException e) {
            fireflyExtensionModel = fireflyExtensionRepository.create(name);
        }
        return fireflyExtensionModel;
    }

    private FireflyExtensionStateModel getOrCreateLastState(FireflyExtensionModel fireflyExtensionModel) {
        FireflyExtensionStateModel result = getLastState(fireflyExtensionModel);
        if (result == null) {
            result = createState(fireflyExtensionModel, "items", "hmc");
        }
        return result;
    }

    private FireflyExtensionStateModel createState(FireflyExtensionModel fireflyExtensionModel, String itemsDefinitionHash, String hmcDefinitionHash) {
        FireflyExtensionStateModel state = new FireflyExtensionStateModel();
        state.setExtension(fireflyExtensionModel);
        state.setItemsDefinitionHash(itemsDefinitionHash);
        state.setHmcDefinitionHash(hmcDefinitionHash);
        addState(fireflyExtensionModel, state);
        return state;
    }

    private void addState(FireflyExtensionModel fireflyExtensionModel, FireflyExtensionStateModel state) {
        ArrayList<FireflyExtensionStateModel> states;
        if (fireflyExtensionModel.getStates() == null) {
            states = new ArrayList<>();
        } else {
            states = new ArrayList<>(fireflyExtensionModel.getStates());
        }
        states.add(state);
        fireflyExtensionModel.setStates(states);
    }

    private FireflyExtensionStateModel getLastState(FireflyExtensionModel fireflyExtensionModel) {
        List<FireflyExtensionStateModel> states = fireflyExtensionModel.getStates();
        if (states == null || states.isEmpty()) {
            return null;
        } else {
            return states.get(states.size() - 1);
        }
    }
}
