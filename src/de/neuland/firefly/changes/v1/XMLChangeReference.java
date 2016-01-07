package de.neuland.firefly.changes.v1;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


@XmlType
public class XMLChangeReference {
    private String file;

    @XmlAttribute
    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
