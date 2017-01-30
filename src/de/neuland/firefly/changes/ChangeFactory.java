package de.neuland.firefly.changes;

import de.hybris.platform.servicelayer.event.EventService;
import de.neuland.firefly.HybrisAdapter;
import de.neuland.firefly.changes.v1.*;
import de.neuland.firefly.extensionfinder.FireflyExtensionRepository;
import de.neuland.firefly.extensionfinder.FireflySystemFactory;
import de.neuland.firefly.migration.MigrationRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static de.neuland.firefly.utils.XMLUtil.loadXML;


@Component
@Scope("tenant")
public class ChangeFactory {
    private static final Logger LOG = Logger.getLogger(ChangeFactory.class);
    @Autowired private FireflySystemFactory fireflySystemFactory;
    @Autowired private EventService eventService;
    @Autowired private HybrisAdapter hybrisAdapter;
    @Autowired private ChangeExecutedEventListener changeExecutedEventListener;
    @Autowired private ChangeRepository changeRepository;
    @Autowired private LogRepository logRepository;
    @Autowired private FireflyExtensionRepository fireflyExtensionRepository;
    @Autowired private MigrationRepository migrationRepository;

    public ChangeList createChangeList() {
        final ChangeList changeList = new ChangeList();

        Map<String, File> extensionPaths = fireflySystemFactory.createFireflySystem().getExtensionPaths();
        for (Map.Entry<String, File> extension : extensionPaths.entrySet()) {
            changeList.addChanges(getChangesFromExtension(extension.getKey(), extension.getValue()));
        }

        return changeList;
    }

    private List<Change> getChangesFromExtension(String extensionName, File extensionPath) {
        List<Change> result = new ArrayList<>();
        File changeDir = new File(extensionPath, "changes");
        if (changeDir.exists()) {
            List<File> changeFiles = Arrays.asList(changeDir.listFiles(new ChangeFileFilter()));
            Collections.sort(changeFiles);
            for (File file : changeFiles) {
                if (isChangeList(file)) {
                    result.addAll(readChangeList(extensionName, extensionPath, file));
                } else {
                    result.addAll(readChangeFile(extensionName, extensionPath, file));
                }
            }
        } else {
            LOG.debug("Change dir " + changeDir.getAbsolutePath() + " not found. Skipping path.");
        }
        return result;
    }

    boolean isChangeList(File changeFile) {
        try {
            loadXML(XMLChangeList.class, changeFile);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }


    private List<? extends Change> readChangeList(String extensionName, File extensionPath, File changeFile) {
        List<Change> result = new ArrayList<>();

        XMLChangeList xmlChangeList = loadXML(XMLChangeList.class, "schema/firefly-v1.xsd", changeFile);
        for (XMLChangeReference xmlChangeReference : xmlChangeList.getChangeReferences()) {
            result.addAll(readChangeFile(extensionName, extensionPath, new File(changeFile.getParentFile(), xmlChangeReference.getFile())));
        }

        return result;
    }

    private List<? extends Change> readChangeFile(String extensionName, File extensionPath, File changeFile) {
        List<Change> result = new ArrayList<>();

        XMLChangeDescription xmlChangeDescription = loadXML(XMLChangeDescription.class, "schema/firefly-v1.xsd", changeFile);
        for (XMLChange xmlChange : xmlChangeDescription.getChanges()) {
            String osSpecificFile = extensionPath.getName() + changeFile.getPath().replace(extensionPath.getPath(), "");
            String relativePathChangeFile = osSpecificFile.replace(File.separator, "/");

            String changeContent;
            if (StringUtils.isNotEmpty(xmlChange.getFile())) {
                File changeContentFile = new File(extensionPath, xmlChange.getFile());
                try {
                    changeContent = new Scanner(changeContentFile).useDelimiter("\\Z").next();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                changeContent = xmlChange.getChangeContent();
            }

            Change.ChangeBasic changeBasic = new Change.ChangeBasic(extensionName,
                                                                    relativePathChangeFile,
                                                                    xmlChange.getAuthor(),
                                                                    xmlChange.getId(),
                                                                    xmlChange.getDescription(),
                                                                    changeContent);
            Change.ChangeDependency changeDependency = new Change.ChangeDependency(changeRepository,
                                                                                   logRepository,
                                                                                   fireflyExtensionRepository,
                                                                                   eventService,
                                                                                   hybrisAdapter,
                                                                                   migrationRepository);
            final Change change;
            if (xmlChange instanceof XMLBeanShell) {
                change = new BeanShellChange(changeBasic, changeDependency);
            } else if (xmlChange instanceof XMLImpEx) {
                change = new ImpExChange(changeBasic, changeDependency);
            } else if (xmlChange instanceof XMLGroovy) {
                change = new GroovyChange(changeBasic, changeDependency);
            } else if (xmlChange instanceof XMLSql) {
                change = new SqlChange(changeBasic, changeDependency);
            } else {
                throw new RuntimeException("Unknown change of type " + xmlChange.getClass().getName());
            }
            result.add(change);
            changeExecutedEventListener.registerListener(hybrisAdapter.getTenantId(), change);
        }

        return result;
    }
}
