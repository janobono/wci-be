package sk.janobono.wci.dal.domain;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "wci_application_property")
public class ApplicationProperty {

    @EmbeddedId
    private ApplicationPropertyId id;

    @Column(name = "property_group")
    @Enumerated(EnumType.STRING)
    ApplicationPropertyGroup group;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "property_value")
    private String value;

    public ApplicationProperty() {
    }

    public ApplicationProperty(ApplicationPropertyId id, ApplicationPropertyGroup group, String value) {
        this.id = id;
        this.group = group;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApplicationProperty that = (ApplicationProperty) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public ApplicationPropertyId getId() {
        return id;
    }

    public void setId(ApplicationPropertyId id) {
        this.id = id;
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
                "id=" + id +
                ", group=" + group +
                ", value='" + value + '\'' +
                '}';
    }
}
