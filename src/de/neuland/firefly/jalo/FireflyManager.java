package de.neuland.firefly.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.neuland.firefly.constants.FireflyConstants;


public class FireflyManager extends GeneratedFireflyManager {
    public static FireflyManager getInstance() {
        ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
        return (FireflyManager) em.getExtension(FireflyConstants.EXTENSIONNAME);
    }

}
