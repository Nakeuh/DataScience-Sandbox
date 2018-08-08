package myLibs.machine_learning.algorithms.trees.decision_trees.least_squares_regression_tree;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DecisionTreeElement {

    /*
    An element is composed of values.
    Values are represented by a map<K,V>
    K : variable name
    V : value
     */
    private Map<String, Object> values = new HashMap<>();

    public DecisionTreeElement(Map<String, Object> values) {
        this.values = values;
    }

    public Set<String> columns(){
        return values.keySet();
    }

    public Object get(String column){
        return values.get(column);
    }

    public Double getDouble(String column){
        Object d = values.get(column);
        if(d==null){
            d=0;
        }
        return (double)d;
    }

    public String getString(String column){
        return (String)values.get(column);
    }

    public void addValue(String name, Object value){
        values.put(name,value);
    }

}
