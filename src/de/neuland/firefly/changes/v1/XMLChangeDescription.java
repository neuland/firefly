package de.neuland.firefly.changes.v1;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;


@XmlRootElement(name = "changeDescription", namespace = "http://firefly.neuland-bfi.de/v1")
public class XMLChangeDescription {
    private String comment;
    private List<XMLChange> changes = new ArrayList<>();

    @XmlElement(namespace = "http://firefly.neuland-bfi.de/v1")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @XmlElements({ @XmlElement(name = "beanShell", namespace = "http://firefly.neuland-bfi.de/v1", type = XMLBeanShell.class),
                   @XmlElement(name = "groovy", namespace = "http://firefly.neuland-bfi.de/v1", type = XMLGroovy.class),
                   @XmlElement(name = "impEx", namespace = "http://firefly.neuland-bfi.de/v1", type = XMLImpEx.class),
                   @XmlElement(name = "sql", namespace = "http://firefly.neuland-bfi.de/v1", type = XMLSql.class) })
    public List<XMLChange> getChanges() {
        return changes;
    }

    @Override public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
