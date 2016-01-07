package de.neuland.firefly.changes;

import org.springframework.stereotype.Component;


@Component
public class ChangeFactory {
    public ChangeList createChangeList() {
        ChangeList changeList = new ChangeList();
        changeList.addChange(null);
        return changeList;
    }
}
