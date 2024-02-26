package fr.fredos.dvdtheque.rest.dao.specifications.filter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import fr.fredos.dvdtheque.rest.dao.domain.Dvd;
import fr.fredos.dvdtheque.rest.dao.domain.Personne;
import jakarta.persistence.criteria.Join;
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
			}else if(root.get(criteria.getKey()).getJavaType() == Dvd.class) {
				Join join = null;
            	if(((String)criteria.getKey()).equalsIgnoreCase("dvd")) {
    				join = root.join(criteria.getKey());
    			}
            	if(((String) criteria.getValue()).equals("true")|| ((String) criteria.getValue()).equals("false")) {
            		return builder.equal(join.get("ripped"),Boolean.valueOf((String) criteria.getValue()));
            	}
            	return builder.equal(join.get("format"),(String) criteria.getValue());
			}else if(root.get(criteria.getKey()).getJavaType() == Personne.class) {
				Join join = null;
            	if(((String)criteria.getKey()).equalsIgnoreCase("realisateur") || ((String)criteria.getKey()).equalsIgnoreCase("acteur")) {
    				join = root.join(criteria.getKey());
    				return builder.like(join.get("nom"), "%"+StringUtils.upperCase((String) criteria.getValue()) +"%");
    			}else {
    				// means genre
    				join = root.join(criteria.getKey());
    				return builder.equal(join.get("name"),(String) criteria.getValue());
    			}
			}else if(root.get(criteria.getKey()).getJavaType() == boolean.class || root.get(criteria.getKey()).getJavaType() == Boolean.class) {
				return builder.equal(root.get(criteria.getKey()),Boolean.valueOf((String) criteria.getValue()));
			} else {
            	return builder.like(root.get(criteria.getKey()), "%"+StringUtils.upperCase(criteria.getValue().toString()) +"%");
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
