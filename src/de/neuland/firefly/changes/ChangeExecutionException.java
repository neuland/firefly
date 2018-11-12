package de.neuland.firefly.changes;

public class ChangeExecutionException extends Exception {
    private Change change;

    public ChangeExecutionException(Throwable cause, Change change) {
        super("Error during execution of change " + change, cause);
        this.change = change;
    }

    public Change getChange() {
        return change;
    }
}
