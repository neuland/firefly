package de.neuland.firefly.changes;

import java.util.ArrayList;
import java.util.List;


public class ChangeList {
    private List<Change> changes = new ArrayList<>();

    void addChange(Change change) {
        changes.add(change);
    }

    public List<Change> getChangesThatRequiredExecution() {
        return new ArrayList<>();
    }

    public void executeChanges() {

    }
}
