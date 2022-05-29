package sk.janobono.wci.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.janobono.wci.dal.domain.ApplicationPropertyGroup;
import sk.janobono.wci.dal.domain.ApplicationPropertyKey;
import sk.janobono.wci.dal.domain.ApplicationProperty;

import java.util.List;

public interface ApplicationPropertyRepository extends JpaRepository<ApplicationProperty, ApplicationPropertyKey> {

    List<ApplicationProperty> getApplicationPropertiesByGroup(ApplicationPropertyGroup group);
}
