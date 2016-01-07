import de.hybris.platform.core.Registry
import de.hybris.platform.servicelayer.config.impl.HybrisConfiguration
import de.hybris.platform.servicelayer.tenant.TenantService
import de.hybris.platform.util.Utilities
import de.hybris.platform.validation.enums.RegexpFlag

TenantService tenantService = ctx.getBean('tenantService')
def allExtensions = Registry.getTenantByID(tenantService.getCurrentTenantId()).tenantSpecificExtensionNames
allExtensions.each {
    File itemsXml = new File(Utilities.getExtensionInfo(it).extensionDirectory, "resources/$it-items.xml")
    println "${itemsXml.absolutePath}=${itemsXml.exists()}"
}


if( false ) {
    // restart hybris
    Registry.destroyAndForceStartup()
}
//println Registry.getGlobalApplicationContext().beanDefinitionNames
//println Registry.getApplicationContext().beanDefinitionNames


/* WTF
   hac-spring.xml
*/
