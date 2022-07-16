package fr.fredos.dvdtheque.rest.dao.specifications.filter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Join;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
@Component
public class SpecificationFactory<T> {

	private Map<FilterOperation, Function<SearchCriteria, Specification<T>>> specs;
	
	@PostConstruct
	private void init() {
		specs = new HashMap<>();
		specs.put(FilterOperation.EQUAL, this::getEqualsSpecification);
		specs.put(FilterOperation.GREATER_THAN, this::getGreaterThanSpecification);
		specs.put(FilterOperation.LESS_THAN, this::getLessThanSpecification);
		specs.put(FilterOperation.IN, this::getInSpecification);
	}
	
	public Specification<T> getByCriteria(SearchCriteria criteria) {
		return specs.get(criteria.getOperation()).apply(criteria);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Specification<T> getEqualsSpecification(SearchCriteria criteria) {
		return (root, query, builder) -> {
			if(root.get(criteria.getKey()).getJavaType().getSuperclass() == Enum.class) {
				Class<Enum> clazz = (Class<Enum>) root.get(criteria.getKey()).getJavaType();
				return builder.equal(root.get(criteria.getKey()),Enum.valueOf(clazz, (String) criteria.getValue()));
			}else if(root.get(criteria.getKey()).getJavaType() == Integer.class) {
				return builder.equal(root.get(criteria.getKey()),Integer.valueOf((String)criteria.getValue()));
			}else if(root.get(criteria.getKey()).getJavaType() == Set.class) {
				Join join = null;
            	if(((String)criteria.getKey()).equalsIgnoreCase("realisateur")) {
    				join = root.join(criteria.getKey());
    			}
            	if(((String)criteria.getKey()).equalsIgnoreCase("acteur")) {
    				join = root.join(criteria.getKey());
    			}
            	return builder.like(join.get("nom"), "%"+criteria.getValue()+"%");
            } else {
            	return builder.equal(root.get(criteria.getKey()), (String)criteria.getValue());
            }
		};
	}
	
	private Specification<T> getGreaterThanSpecification(SearchCriteria criteria) {
		return (root, query, builder) -> {
			return builder
				.greaterThan(root.<String> get(criteria.getKey()), criteria.getValue().toString());
		};
	}
	
	private Specification<T> getLessThanSpecification(SearchCriteria criteria) {
		return (root, query, builder) -> {
			return builder
				.lessThan(root.<String> get(criteria.getKey()), criteria.getValue().toString());
		};
	}
	private Specification<T> getInSpecification(SearchCriteria criteria) {
		return (root, query, builder) -> {
			return builder.in(root.get(criteria.getKey())).value(criteria.getValue());
		};
	}
}
