package de.neuland.firefly;

import de.hybris.platform.core.Initialization;
import de.hybris.platform.core.Registry;
import de.hybris.platform.hmc.jalo.HMCManager;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.util.JspContext;
import de.hybris.platform.util.localization.TypeLocalization;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;


@Component
@Scope("tenant")
public class HybrisAdapter {
    private static final Logger LOG = Logger.getLogger(HybrisAdapter.class);
    @Autowired EventService eventService;

    public void updateSystem() throws Exception {
        doInitialize(true);
        eventService.publishEvent(new SystemUpdatedEvent(getTenantId()));
    }

    private void doInitialize(boolean update) throws Exception {
        FireflyJspWriter jspWriter = new FireflyJspWriter();
        try {
            MockHttpServletRequest request = new MockHttpServletRequest();
            if (update) {
                request.addParameter("init", "true");
                request.addParameter("initmethod", "update");
                request.addParameter("essential", "true");
            }
            request.addParameter("default", "false");
            request.addParameter("localizetypes", "true");
            request.addParameter("clearhmc", "true");
            request.addParameter("ALL_EXTENSIONS", "true");
            JspContext jspContext = new JspContext(jspWriter, request, new MockHttpServletResponse());
            Initialization.doInitialize(jspContext);
        } finally {
            LOG.debug(de.hybris.platform.util.Utilities.filterOutHTMLTags(jspWriter.getString()));
        }
    }

    public void clearHmcConfiguration() throws Exception {
        doInitialize(false);
        eventService.publishEvent(new HmcResetEvent(getTenantId()));
    }

    public String getTenantId() {
        return Registry.getCurrentTenant().getTenantID();
    }

    /**
     * This event is triggered after a system update is done.
     */
    public static class SystemUpdatedEvent extends TenantEvent {

        SystemUpdatedEvent(String tenantId) {
            super(tenantId);
        }
    }

    /**
     * This event is triggered after a hMC reset is done.
     */
    public static class HmcResetEvent extends TenantEvent {

        HmcResetEvent(String tenantId) {
            super(tenantId);
        }
    }

    public static class TenantEvent extends AbstractEvent {

        protected String tenantId;
        TenantEvent(String tenantId) {
            this.tenantId = tenantId;
            // This event can only be triggered from der HybrisAdapter.
        }

        public String getTenantId() {
            return tenantId;
        }

        @Override public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }

    }
}
