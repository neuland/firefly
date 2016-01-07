package de.neuland.firefly.changes.v1;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;


@XmlRootElement(name = "changeList", namespace = "http://firefly.neuland-bfi.de/v1")
public class XMLChangeList {
    private List<XMLChangeReference> changeReferences = new ArrayList<>();

    @XmlElement(name = "change", namespace = "http://firefly.neuland-bfi.de/v1")
    public List<XMLChangeReference> getChangeReferences() {
        return changeReferences;
    }

    @Override public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
