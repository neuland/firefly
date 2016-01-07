package de.neuland.firefly.changes.v1;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import static org.apache.commons.lang.StringUtils.trimToNull;


@XmlType
public abstract class XMLChange {
    private String author;
    private String id;
    private String file;
    private String changeContent;

    @XmlAttribute
    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @XmlAttribute
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlValue
    public String getChangeContent() {
        return changeContent;
    }

    public void setChangeContent(String changeContent) {
        this.changeContent = trimToNull(changeContent);
    }

    @Override public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
