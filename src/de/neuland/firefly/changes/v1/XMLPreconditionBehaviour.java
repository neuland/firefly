package de.neuland.firefly.changes.v1;

import de.neuland.firefly.changes.PreconditionBehaviour;


public enum XMLPreconditionBehaviour {
    HALT(PreconditionBehaviour.HALT),
    CONTINUE(PreconditionBehaviour.CONTINUE),
    MARK_RAN(PreconditionBehaviour.MARK_RAN),
    WARN(PreconditionBehaviour.WARN);

    private PreconditionBehaviour preconditionBehaviour;

    XMLPreconditionBehaviour(PreconditionBehaviour preconditionBehaviour) {
        this.preconditionBehaviour = preconditionBehaviour;
    }

    public PreconditionBehaviour getPreconditionBehaviour() {
        return preconditionBehaviour;
    }
}
