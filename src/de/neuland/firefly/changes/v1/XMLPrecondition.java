package de.neuland.firefly.changes.v1;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import static org.apache.commons.lang.StringUtils.trimToNull;


@XmlType
public class XMLPrecondition {
    private String id;
    private String value;

    @XmlAttribute @XmlID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlValue
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = trimToNull(value);
    }

    @Override public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
