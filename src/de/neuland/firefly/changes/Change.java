package de.neuland.firefly.changes;

import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.event.EventService;
import de.neuland.firefly.HybrisAdapter;
import de.neuland.firefly.extensionfinder.FireflyExtensionRepository;
import de.neuland.firefly.migration.MigrationRepository;
import de.neuland.firefly.model.FireflyChangeModel;
import de.neuland.firefly.model.FireflyMigrationModel;
import de.neuland.firefly.utils.GroovyScriptRunner;
import de.neuland.firefly.utils.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang.StringUtils.isNotBlank;


public abstract class Change {
    private final Logger LOG = Logger.getLogger(getClass());
    private ChangeRepository changeRepository;
    private LogRepository logRepository;
    private FireflyExtensionRepository fireflyExtensionRepository;
    private MigrationRepository migrationRepository;
    private EventService eventService;
    private HybrisAdapter hybrisAdapter;
    private GroovyScriptRunner groovyScriptRunner;
    private String extensionName;
    private String author;
    private String id;
    /**
     * The relative name of the change file. This is used to identify the change.
     */
    private String file;
    private String description;
    private String changeContent;
    private String precondition;
    private PreconditionBehaviour onPreconditionFail;
    private String executionLog;

    protected Change(ChangeBasic changeBasic, ChangeDependency changeDependency) {
        this.changeRepository = changeDependency.getChangeRepository();
        this.logRepository = changeDependency.getLogRepository();
        this.fireflyExtensionRepository = changeDependency.getFireflyExtensionRepository();
        this.eventService = changeDependency.getEventService();
        this.hybrisAdapter = changeDependency.getHybrisAdapter();
        this.migrationRepository = changeDependency.getMigrationRepository();
        this.groovyScriptRunner = changeDependency.getGroovyScriptRunner();
        this.extensionName = changeBasic.getExtensionName();
        this.file = changeBasic.getFile();
        this.author = changeBasic.getAuthor();
        this.id = changeBasic.getId();
        this.description = changeBasic.getDescription();
        this.changeContent = changeBasic.getChangeContent();
        this.precondition = changeBasic.getPrecondition();
        this.onPreconditionFail = changeBasic.getOnPreconditionFail();
    }

    public String getExtensionName() {
        return extensionName;
    }

    public String getAuthor() {
        return author;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getFile() {
        return file;
    }

    public String getChangeContent() {
        return changeContent;
    }

    public boolean executionRequired() throws ChangeModifiedException {
        try {
            FireflyChangeModel changeModel = changeRepository.findChange(file, author, id);
            checkIfChangeIsModified(changeModel);
            return false;
        } catch (ChangeRepository.ChangeNotFoundException e) {
            return true;
        }
    }

    public void execute(FireflyMigrationModel migration) throws ChangeExecutionException, ChangeModifiedException, PreconditionFailedException {
        try {
            FireflyChangeModel changeModel = changeRepository.findChange(file, author, id);
            checkIfChangeIsModified(changeModel);
        } catch (ChangeRepository.ChangeNotFoundException e) {
            if (preconditionSuccess()) {
                LOG.info("Executing change " + toString());
                executeChange();
                eventService.publishEvent(new ChangeExecutedEvent(hybrisAdapter.getTenantId(), this, migration.getPk()));
            } else {
                switch (onPreconditionFail) {
                    case CONTINUE:
                        LOG.debug("Precondition for change " + toString() + " failed. Continue with migration.");
                        break;
                    case WARN:
                        LOG.warn("Precondition for change " + toString() + " failed. Continue with migration.");
                        break;
                    case MARK_RAN:
                        LOG.info("Precondition for change " + toString() + " failed. Mark change as ran.");
                        this.setExecutionLog("Precondition failed:\n" + precondition);
                        eventService.publishEvent(new ChangeExecutedEvent(hybrisAdapter.getTenantId(), this, migration.getPk()));
                        break;
                    default:
                        throw new PreconditionFailedException(this);
                }
            }
        }
    }

    private boolean preconditionSuccess() {
        return isNotBlank(precondition) && TRUE.equals(groovyScriptRunner.execute(this, precondition));
    }

    abstract void executeChange() throws ChangeExecutionException;

    protected void setExecutionLog(String executionLog) {
        this.executionLog = executionLog;
    }

    public String getExecutionLog() {
        return executionLog;
    }

    public void onExecution(PK migration) throws FireflyExtensionRepository.FireflyExtensionNotFoundException {
        LOG.debug("Change " + this + " has been executed.");
        FireflyChangeModel changeModel;
        try {
            changeModel = changeRepository.findChange(getFile(), getAuthor(), getId());
        } catch (ChangeRepository.ChangeNotFoundException e) {
            changeModel = changeRepository.create(migrationRepository.findByPk(migration),
                                                  fireflyExtensionRepository.findByName(getExtensionName()),
                                                  getFile(),
                                                  getAuthor(),
                                                  getId());
            if (StringUtils.isNotEmpty(getDescription())) {
                changeModel.setDescription(getDescription().trim());
            }
        }
        changeModel.setHash(MD5Util.generateMD5(changeContent));
        changeRepository.save(changeModel);

        if (isNotBlank(getExecutionLog())) {
            changeModel.setLog(logRepository.create(changeModel));
            changeRepository.save(changeModel);
            logRepository.addLog(changeModel.getLog(), getExecutionLog());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Change)) {
            return false;
        }

        Change change = (Change) o;

        if (!author.equals(change.author)) {
            return false;
        }
        if (!file.equals(change.file)) {
            return false;
        }
        if (!id.equals(change.id)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = author.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + file.hashCode();
        return result;
    }

    public String toString() {
        return getFile() + ":" + getAuthor() + ":" + getId();
    }

    public Logger getChangeLogger() {
        int lastIndexOf = getFile().lastIndexOf('.');
        String fileWithoutSuffix = lastIndexOf > 0 ? getFile().substring(0, lastIndexOf) : getFile();
        return Logger.getLogger(fileWithoutSuffix + ":" + getAuthor() + ":" + getId());
    }

    private void checkIfChangeIsModified(FireflyChangeModel changeModel) throws ChangeModifiedException {
        if (changeModel.getHash() != null && !MD5Util.generateMD5(changeContent).equals(changeModel.getHash())) {
            throw new ChangeModifiedException(this);
        }
    }

    public static class ChangeModifiedException extends Exception {
        public ChangeModifiedException(Change change) {
            super("Change " + change.getFile() + ":" + change.getAuthor() + ":" + change.getId() + " has been modified since execution.");
        }
    }

    static class ChangeDependency {
        private final ChangeRepository changeRepository;
        private final LogRepository logRepository;
        private final FireflyExtensionRepository fireflyExtensionRepository;
        private final EventService eventService;
        private final HybrisAdapter hybrisAdapter;
        private final MigrationRepository migrationRepository;
        private final GroovyScriptRunner groovyScriptRunner;

        ChangeDependency(ChangeRepository changeRepository, LogRepository logRepository, FireflyExtensionRepository fireflyExtensionRepository, EventService eventService,
                         HybrisAdapter hybrisAdapter, MigrationRepository migrationRepository, GroovyScriptRunner groovyScriptRunner) {
            this.changeRepository = changeRepository;
            this.logRepository = logRepository;
            this.fireflyExtensionRepository = fireflyExtensionRepository;
            this.eventService = eventService;
            this.hybrisAdapter = hybrisAdapter;
            this.migrationRepository = migrationRepository;
            this.groovyScriptRunner = groovyScriptRunner;
        }

        public ChangeRepository getChangeRepository() {
            return changeRepository;
        }

        public LogRepository getLogRepository() {
            return logRepository;
        }

        public FireflyExtensionRepository getFireflyExtensionRepository() {
            return fireflyExtensionRepository;
        }

        public EventService getEventService() {
            return eventService;
        }

        public HybrisAdapter getHybrisAdapter() {
            return hybrisAdapter;
        }

        public MigrationRepository getMigrationRepository() {
            return migrationRepository;
        }

        public GroovyScriptRunner getGroovyScriptRunner() {
            return groovyScriptRunner;
        }
    }

    static class ChangeBasic {
        private final String extensionName;
        private final String file;
        private final String author;
        private final String id;
        private final String description;
        private final String changeContent;
        private final String precondition;
        private final PreconditionBehaviour onPreconditionFail;

        ChangeBasic(String extensionName, String file, String author, String id,
                    String description, String changeContent, String precondition,
                    PreconditionBehaviour onPreconditionFail) {
            this.extensionName = extensionName;
            this.file = file;
            this.author = author;
            this.id = id;
            this.description = description;
            this.changeContent = changeContent;
            this.precondition = precondition;
            this.onPreconditionFail = onPreconditionFail;
        }

        public String getExtensionName() {
            return extensionName;
        }

        public String getFile() {
            return file;
        }

        public String getAuthor() {
            return author;
        }

        public String getId() {
            return id;
        }

        public String getDescription() {
            return description;
        }

        public String getChangeContent() {
            return changeContent;
        }

        public String getPrecondition() {
            return precondition;
        }

        public PreconditionBehaviour getOnPreconditionFail() {
            return onPreconditionFail;
        }
    }
}
