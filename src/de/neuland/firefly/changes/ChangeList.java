package de.neuland.firefly.changes;

import de.neuland.firefly.changes.Change.ChangeModifiedException;
import de.neuland.firefly.extensionfinder.FireflyExtensionRepository;
import de.neuland.firefly.model.FireflyMigrationModel;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;


public class ChangeList {
    private List<Change> changes = new ArrayList<>();

    void addChange(Change change) {
        if (change != null &&
            !contains(change.getFile(), change.getAuthor(), change.getId())) {
            changes.add(change);
        }
    }

    void addChanges(List<Change> changes) {
        if (changes != null) {
            for (Change change : changes) {
                addChange(change);
            }
        }
    }

    List<Change> getChanges() {
        return unmodifiableList(changes);
    }

    boolean contains(String file, String author, String id) {
        for (Change change : changes) {
            if (file.equals(change.getFile()) &&
                author.equals(change.getAuthor()) &&
                id.equals(change.getId())) {
                return true;
            }
        }
        return false;
    }

    public List<Change> getChangesThatRequiredExecution() throws ChangeModifiedException {
        List<Change> result = new ArrayList<>();
        for (Change change : getChanges()) {
            if (change.executionRequired()) {
                result.add(change);
            }
        }
        return result;
    }

    public void executeChanges(FireflyMigrationModel migration) throws ChangeExecutionException, ChangeModifiedException, PreconditionFailedException {
        for (Change change : getChangesThatRequiredExecution()) {
            change.execute(migration);
        }
    }

    public void markChangesAsExecuted(FireflyMigrationModel migration) throws ChangeModifiedException, FireflyExtensionRepository.FireflyExtensionNotFoundException {
        for (Change change : getChangesThatRequiredExecution()) {
            change.setExecutionLog("Change as been marked as executed.");
            change.onExecution(migration.getPk());
        }
    }
}
