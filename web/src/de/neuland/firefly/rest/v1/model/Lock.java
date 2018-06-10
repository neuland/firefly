package de.neuland.firefly.rest.v1.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Lock implements Serializable {
    private final Date creationDate;
    private final long clusterNode;

    public Lock() {
        this(null,
             -1);
    }

    public Lock(Date creationDate,
                long clusterNode) {
        this.creationDate = creationDate;
        this.clusterNode = clusterNode;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public long getClusterNode() {
        return clusterNode;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Lock))
            return false;

        Lock lock = (Lock) o;

        return new EqualsBuilder()
                        .append(getClusterNode(), lock.getClusterNode())
                        .append(getCreationDate(), lock.getCreationDate())
                        .isEquals();
    }

    @Override public int hashCode() {
        return new HashCodeBuilder(17, 37)
                        .append(getCreationDate())
                        .append(getClusterNode())
                        .toHashCode();
    }

    @Override public String toString() {
        return new ToStringBuilder(this)
                        .append("creationDate", creationDate)
                        .append("clusterNode", clusterNode)
                        .toString();
    }
}
