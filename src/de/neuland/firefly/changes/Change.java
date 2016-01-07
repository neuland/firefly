package de.neuland.firefly.changes;

public abstract class Change {
    public boolean executionRequired() {
        return true;
    }
    abstract void execute();
}
