package sarah.metrics;

import java.util.HashMap;
import java.util.Set;

/*
 * A SarahMetric represents something that is observed about a data set.
 * SarahMetrics have a name and a description.
 * SarahMetrics can represent a set of metrics, one per function.
 */

public class SarahMetric<TYPE> {
	private TYPE value = null;
	public String name;
	public String description;
	private boolean parameterizedByFunction = false;
	
	private HashMap<String,TYPE> functionMetrics = new HashMap<String,TYPE>();
	public static HashMap<String,SarahMetric<?>> metrics = new HashMap<String,SarahMetric<?>>();
	
	public SarahMetric(String theName, String theDescription) {
		name = theName;
		description = theDescription;
		parameterizedByFunction = theName.contains("%func");
		metrics.put(name, this);
	}
	
	public boolean parameterizedByFunction() {
		return parameterizedByFunction;
	}
	
	public void setValue(TYPE theValue) {
		value = theValue;
	}
	public void setValue(String functionName, TYPE theValue) {
		functionMetrics.put(functionName, theValue);
	}
	public TYPE getValue() {
		return value;
	}
	
	@SuppressWarnings("unchecked")
	public TYPE getValue(String functionName) {
		// Special case where value is a string and it is parameterized by function
		if ((value!=null) && (value instanceof String) && (((String)value).contains("%func"))) {
			return (TYPE) ((String)value).replace("%func", functionName);
		} else {
			return functionMetrics.get(functionName);
		}
	}
	public Set<String> functionNames() {
		Set<String> result = functionMetrics.keySet();
		return result;
	}

}
