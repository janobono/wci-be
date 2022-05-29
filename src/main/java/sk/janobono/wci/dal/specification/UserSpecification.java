package sk.janobono.wci.dal.specification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import sk.janobono.wci.api.service.so.UserSearchCriteriaSO;
import sk.janobono.wci.component.ScDf;
import sk.janobono.wci.dal.domain.User;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public record UserSpecification(ScDf scDf, UserSearchCriteriaSO criteria) implements Specification<User> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSpecification.class);

    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        criteriaQuery.distinct(true);
        if (
                !StringUtils.hasLength(criteria.searchField())
                        && !StringUtils.hasLength(criteria.username())
                        && !StringUtils.hasLength(criteria.email())
        ) {
            LOGGER.debug("Empty criteria.");
            return criteriaQuery.getRestriction();
        }
        List<Predicate> predicates = new ArrayList<>();


        // search field
        if (StringUtils.hasLength(criteria.searchField())) {
            predicates.add(searchFieldToPredicate(criteria.searchField(), root, criteriaBuilder));
        }

        // username
        if (StringUtils.hasLength(criteria.username())) {
            predicates.add(criteriaBuilder.like(root.get("username"), "%" + criteria.username() + "%"));
        }

        // email
        if (StringUtils.hasLength(criteria.username())) {
            predicates.add(criteriaBuilder.like(
                    toScDf(root.get("email"), criteriaBuilder),
                    "%" + scDf.toScDf(criteria.email()) + "%"
            ));
        }

        return criteriaQuery.where(criteriaBuilder.and(predicates.toArray(Predicate[]::new))).getRestriction();
    }


    private Predicate searchFieldToPredicate(String searchField, Root<User> root, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        String[] fieldValues = searchField.split(" ");
        for (String fieldValue : fieldValues) {
            fieldValue = "%" + scDf.toScDf(fieldValue) + "%";
            List<Predicate> subPredicates = new ArrayList<>();
            subPredicates.add(criteriaBuilder.like(toScDf(root.get("username"), criteriaBuilder), fieldValue));
            subPredicates.add(criteriaBuilder.like(toScDf(root.get("firstName"), criteriaBuilder), fieldValue));
            subPredicates.add(criteriaBuilder.like(toScDf(root.get("midName"), criteriaBuilder), fieldValue));
            subPredicates.add(criteriaBuilder.like(toScDf(root.get("lastName"), criteriaBuilder), fieldValue));
            subPredicates.add(criteriaBuilder.like(toScDf(root.get("email"), criteriaBuilder), fieldValue));
            predicates.add(criteriaBuilder.or(subPredicates.toArray(Predicate[]::new)));
        }
        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }

    private Expression<String> toScDf(Path<String> path, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.lower(criteriaBuilder.function("unaccent", String.class, path));
    }
}
