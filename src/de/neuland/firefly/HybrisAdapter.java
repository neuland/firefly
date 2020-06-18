package de.neuland.firefly;

import de.hybris.platform.core.Initialization;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.system.InitializationLockHandler;
import de.hybris.platform.core.system.impl.DefaultInitLockDao;
import de.hybris.platform.regioncache.CacheController;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.util.JspContext;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import static java.lang.String.format;


@Component
@Scope("prototype")
public class HybrisAdapter {
    private static final Logger LOG = Logger.getLogger(HybrisAdapter.class);
    private static final String SYSTEM_UPDATE = "System update";
    private static final String HMC_RESET = "HMC Reset";
    @Autowired EventService eventService;
    @Autowired CacheController cacheController;

    public void initFirefly() throws Exception {
        doInitialize(true);
    }

    public void updateSystem(PK migration) throws Exception {
        doInitialize(true);
        eventService.publishEvent(new SystemUpdatedEvent(getTenantId(), migration));
    }

    private void doInitialize(boolean update) throws Exception {
        FireflyJspWriter jspWriter = new FireflyJspWriter();
        try {
            MockHttpServletRequest request = new MockHttpServletRequest();
            if (update) {
                // up to version 5
                request.addParameter("initmethod", "update");
                request.addParameter("essential", "true");
                // version 6
                request.addParameter("initMethod", "UPDATE");
                request.addParameter("createEssentialData", "true");
            }
            request.addParameter("init", "true");
            request.addParameter("default", "false");
            request.addParameter("ALL_EXTENSIONS", "true");
            // up to version 5
            request.addParameter("localizetypes", "true");
            request.addParameter("clearhmc", "true");
            // version 6
            request.addParameter("localizeTypes", "true");
            request.addParameter("clearHMC", "true");

            JspContext jspContext = new JspContext(jspWriter, request, new MockHttpServletResponse());

            try {
                InitializationLockHandler handler = new InitializationLockHandler(new DefaultInitLockDao());
                String operationName = update ? SYSTEM_UPDATE : HMC_RESET;

                handler.performLocked(Registry.getCurrentTenant(),
                                      createInitializeCallable(jspContext),
                                      operationName);
            } catch (NoSuchMethodException e) {
                LOG.info(format("Internal reflection unsuccessful: system is not locked while performing '%s only' change.", HMC_RESET));
                Initialization.doInitialize(jspContext);
            }
        } finally {
            LOG.debug(de.hybris.platform.util.Utilities.filterOutHTMLTags(jspWriter.getString()));
        }
    }

    private Callable<Boolean> createInitializeCallable(final JspContext jspContext) {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception {
                Method m = Initialization.class.getDeclaredMethod("doInitializeImpl", JspContext.class);
                m.setAccessible(true);
                m.invoke(null, jspContext);
                return Boolean.TRUE;
            }
        };
    }

    public void clearHmcConfiguration(PK migration) throws Exception {
        doInitialize(false);
        eventService.publishEvent(new HmcResetEvent(getTenantId(), migration));
    }

    public String getTenantId() {
        return Registry.getCurrentTenant().getTenantID();
    }

    public void clearJaloCache() {
        Registry.getCurrentTenant().getCache().clear();
    }

    /**
     * This event is triggered after a system update is done.
     */
    public static class SystemUpdatedEvent extends TenantEvent {
        private PK migration;

        SystemUpdatedEvent(String tenantId, PK migration) {
            super(tenantId);
            this.migration = migration;
        }

        public PK getMigration() {
            return migration;
        }
    }

    /**
     * This event is triggered after a hMC reset is done.
     */
    public static class HmcResetEvent extends TenantEvent {
        private PK migration;

        HmcResetEvent(String tenantId, PK migration) {
            super(tenantId);
            this.migration = migration;
        }

        public PK getMigration() {
            return migration;
        }
    }

}
