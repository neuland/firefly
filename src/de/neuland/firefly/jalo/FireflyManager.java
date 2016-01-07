package de.neuland.firefly.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.neuland.firefly.constants.FireflyConstants;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class FireflyManager extends GeneratedFireflyManager
{
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger( FireflyManager.class.getName() );
	
	public static final FireflyManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (FireflyManager) em.getExtension(FireflyConstants.EXTENSIONNAME);
	}
	
}
