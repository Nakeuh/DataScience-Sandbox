package myLibs.machineLearning.algorithms.clustering.components;

import java.util.ArrayList;
import java.util.List;

public class Element {

    private String name;
    private List<Double> traits;

    public Element(String name, List<Double>traits){
        this.name = name;
        this.traits = traits;
    }

    public static Element randomDoubleElement (int id,int nbTraits){
        String name = "Element "+id;

        List<Double>traits = new ArrayList<Double>();

        for(int i=0;i<nbTraits;i++){
            traits.add((Double)Math.random());
        }

        return new Element(name,traits);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Double> getTraits() {
        return traits;
    }

    public void setTraits(List<Double> traits) {
        this.traits = traits;
    }
}