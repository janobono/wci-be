package sk.janobono.wci.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.janobono.wci.dal.domain.ApplicationProperty;
import sk.janobono.wci.dal.domain.ApplicationPropertyGroup;
import sk.janobono.wci.dal.domain.ApplicationPropertyId;

import java.util.List;

public interface ApplicationPropertyRepository extends JpaRepository<ApplicationProperty, ApplicationPropertyId> {

    List<ApplicationProperty> getApplicationPropertiesById_LangAndGroup(String lang, ApplicationPropertyGroup group);
}
