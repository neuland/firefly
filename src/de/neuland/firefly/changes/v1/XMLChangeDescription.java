package de.neuland.firefly.changes.v1;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;


@XmlRootElement(name = "changeDescription", namespace = "http://firefly.neuland-bfi.de/v1")
public class XMLChangeDescription {
    private List<XMLChange> changes = new ArrayList<>();
    private List<XMLPrecondition> preconditions = new ArrayList<>();

    @XmlElements({ @XmlElement(name = "beanShell", namespace = "http://firefly.neuland-bfi.de/v1", type = XMLBeanShell.class),
                   @XmlElement(name = "groovy", namespace = "http://firefly.neuland-bfi.de/v1", type = XMLGroovy.class),
                   @XmlElement(name = "impEx", namespace = "http://firefly.neuland-bfi.de/v1", type = XMLImpEx.class),
                   @XmlElement(name = "sql", namespace = "http://firefly.neuland-bfi.de/v1", type = XMLSql.class) })
    public List<XMLChange> getChanges() {
        return changes;
    }

    @XmlElement(name = "precondition", namespace = "http://firefly.neuland-bfi.de/v1")
    public List<XMLPrecondition> getPreconditions() {
        return preconditions;
    }

    @Override public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
