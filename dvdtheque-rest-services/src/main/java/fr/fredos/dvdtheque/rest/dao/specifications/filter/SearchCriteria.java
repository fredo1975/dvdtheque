package fr.fredos.dvdtheque.rest.dao.specifications.filter;

public class SearchCriteria {
	private String key;
    private FilterOperation operation;
    private Object value;
    private String predicate;
    
    public SearchCriteria(String key, String operation, Object value, String predicate) {
    	this.key = key;
    	this.operation = FilterOperation.fromValue(operation);
    	this.value = value;
    	this.predicate = predicate;
    }
    
    public boolean isOrPredicate() {
    	return predicate.equalsIgnoreCase("OR");
    }

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public FilterOperation getOperation() {
		return operation;
	}

	public void setOperation(FilterOperation operation) {
		this.operation = operation;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getPredicate() {
		return predicate;
	}

	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}
    
}
