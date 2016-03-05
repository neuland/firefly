package de.neuland.firefly.changes;

public class ChangeExecutionException extends Exception {
    private Change change;

    public ChangeExecutionException(Throwable cause, Change change) {
        super(cause);
        this.change = change;
    }

    public Change getChange() {
        return change;
    }
}
