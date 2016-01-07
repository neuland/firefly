package de.neuland.firefly.hmc;

import de.hybris.platform.hmc.AbstractEditorMenuChip;
import de.hybris.platform.hmc.AbstractExplorerMenuTreeNodeChip;
import de.hybris.platform.hmc.EditorTabChip;
import de.hybris.platform.hmc.extension.HMCExtension;
import de.hybris.platform.hmc.extension.MenuEntrySlotEntry;
import de.hybris.platform.hmc.generic.ClipChip;
import de.hybris.platform.hmc.generic.ToolbarActionChip;
import de.hybris.platform.hmc.webchips.Chip;
import de.hybris.platform.hmc.webchips.DisplayState;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


public class FireflyHMCExtension extends HMCExtension {
    /**
     * Path to the resource bundles.
     */
    public final static String RESOURCE_PATH = "de.neuland.firefly.hmc.locales";

    @Override
    public List<AbstractExplorerMenuTreeNodeChip> getTreeNodeChips(final DisplayState displayState, final Chip parent) {
        return Collections.emptyList();
    }

    @Override
    public List<MenuEntrySlotEntry> getMenuEntrySlotEntries(final DisplayState displayState, final Chip parent) {
        return Collections.emptyList();
    }

    @Override
    public List<ClipChip> getSectionChips(final DisplayState displayState, final ClipChip parent) {
        return Collections.emptyList();
    }

    @Override
    public List<EditorTabChip> getEditorTabChips(final DisplayState displayState, final AbstractEditorMenuChip parent) {
        return Collections.emptyList();
    }

    @Override
    public List<ToolbarActionChip> getToolbarActionChips(final DisplayState displayState, final Chip parent) {
        return Collections.emptyList();
    }

    @Override
    public ResourceBundle getLocalizeResourceBundle(final Locale locale) {
        return null;
    }

    @Override
    public String getResourcePath() {
        return RESOURCE_PATH;
    }
}
