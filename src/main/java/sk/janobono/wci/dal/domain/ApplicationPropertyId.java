package sk.janobono.wci.dal.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ApplicationPropertyId implements Serializable {

    @Column(name = "property_key")
    @Enumerated(EnumType.STRING)
    ApplicationPropertyKey key;

    @Column(name = "property_lang")
    private String lang;

    public ApplicationPropertyId() {
    }

    public ApplicationPropertyId(ApplicationPropertyKey key, String lang) {
        this.key = key;
        this.lang = lang;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApplicationPropertyId that = (ApplicationPropertyId) o;

        if (key != that.key) return false;
        return Objects.equals(lang, that.lang);
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (lang != null ? lang.hashCode() : 0);
        return result;
    }

    public ApplicationPropertyKey getKey() {
        return key;
    }

    public void setKey(ApplicationPropertyKey key) {
        this.key = key;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @Override
    public String toString() {
        return "ApplicationPropertyId{" +
                "key=" + key +
                ", lang='" + lang + '\'' +
                '}';
    }
}
