package de.neuland.firefly.changes;

public class PreconditionFailedException extends Exception {
    private Change change;

    public PreconditionFailedException(Change change) {
        super("Precondition for change " + change + " failed.");
        this.change = change;
    }

    public Change getChange() {
        return change;
    }
}
