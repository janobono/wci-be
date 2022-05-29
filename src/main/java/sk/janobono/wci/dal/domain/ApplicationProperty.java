package sk.janobono.wci.dal.domain;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "wci_application_property")
public class ApplicationProperty {

    @Id
    @Column(name = "property_key")
    @Enumerated(EnumType.STRING)
    ApplicationPropertyKey key;

    @Column(name = "property_group")
    @Enumerated(EnumType.STRING)
    ApplicationPropertyGroup group;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "property_value")
    private String value;

    public ApplicationProperty() {
    }

    public ApplicationProperty(ApplicationPropertyKey key, ApplicationPropertyGroup group, String value) {
        this.key = key;
        this.group = group;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApplicationProperty that = (ApplicationProperty) o;

        return key == that.key;
    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }

    public ApplicationPropertyKey getKey() {
        return key;
    }

    public void setKey(ApplicationPropertyKey key) {
        this.key = key;
    }

    public ApplicationPropertyGroup getGroup() {
        return group;
    }

    public void setGroup(ApplicationPropertyGroup group) {
        this.group = group;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ApplicationProperty{" +
                "key=" + key +
                ", group=" + group +
                ", value='" + value + '\'' +
                '}';
    }
}
