package myLibs.machine_learning.algorithms.trees.decision_trees.least_squares_regression_tree;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import scala.Tuple1;
import scala.Tuple2;

import java.util.*;
import java.util.stream.Collectors;

public class DecisionTreeDataset {

    private List<DecisionTreeElement> elements = new ArrayList<>();
    // <K,V> with K : the column name and V a boolean isNumeric (or isNotNominal)
    private Map<String,Boolean> schema;
    private String targetName;

    // A bit of optimization
    private Multimap<String,Object> internal_values = HashMultimap.create();
    private Double avg = null;
    private Double stddev = null;
    private Integer size = null;


    public DecisionTreeDataset(Map<String,Boolean> schema, List<DecisionTreeElement> elements, String targetName,Boolean testSchema) {
        this.schema=schema;

        List<DecisionTreeElement> validElements = elements;

        if(testSchema){
            for(DecisionTreeElement e: elements){
                if(!columns().containsAll(e.columns())){
                    validElements.remove(e);
                    System.out.println("Warning : The element "+e+" does not match the dataset schema. It has be ignored.");
                }
            }
        }

        this.elements = validElements;
        internal_values = mapValues(this.elements);
        this.targetName = targetName;
    }

    public DecisionTreeDataset(Map<String,Boolean> schema, List<DecisionTreeElement> elements, String targetName) {
        this(schema,elements,targetName,false);
    }

    public List<DecisionTreeElement> getElements() {
        return elements;
    }

    public int size() {
        if(size==null){
            size=elements.size();
        }
        return size;
    }

    public DecisionTreeDataset subdataset(int start, int end){
        return new DecisionTreeDataset(schema,elements.subList(start,end),targetName);
    }

    /**
     * Return the mean of the target column
     * @return
     */
    public double avg(){
        if(avg==null){
            avg=internal_values.get(targetName).stream().mapToDouble(a->(double)a).average().getAsDouble();
        }
        return avg;
    }

    /**
     * Return the mean of target for each class from the given column (discrete column)
     * todo : not efficient
     * @param column
     * @return
     */
    public Map<String,Double> avgDiscrete(String column){
        Map<String,Double> targetAveragePerClass = new HashMap<>();

        for(String c: allClasses(column)){
            double avg=0;
            double count=0;
            for(DecisionTreeElement e: elements){
                if(e.getString(column).equals(c)){
                    avg+=e.getDouble(targetName);
                    count+=1;
                }
            }

            avg /= count;
            targetAveragePerClass.put(c,avg);
        }

        return targetAveragePerClass;
    }

    /**
     * Return the standard deviation of the target column
     * @return
     */
    public double stddev(){
        if(stddev==null){
            double avg = avg();

            for (Object d: internal_values.get(targetName)){
                stddev+=Math.pow((double)d - avg,2);
            }

            stddev/=size();
            stddev = Math.sqrt(stddev);
        }
        return stddev;
    }


    /**
     * Compute the stddev of target for each class from the given column (discrete column)
     * Then multiply each stddev by it's probability (countClass/countTotal) and sum them
     * @param column
     * @return
     */
    public double stddevColumnDiscrete(String column){
        Map<String,Double> avgClass = avgDiscrete(column);
        Map<String,Double> stddevClass = new HashMap<>();
        double globalStddev = 0;
        for(DecisionTreeElement e: elements){
            String myclass = (String)e.get(column);
            double tosum = Math.pow(e.getDouble(targetName)-avgClass.get(column),2);
            stddevClass.put(myclass,stddevClass.get(myclass) + tosum);
        }

        for(Map.Entry<String,Double> e : stddevClass.entrySet()){
            String myclass = e.getKey();
            Double stddev = Math.sqrt(e.getValue() / sum(column,myclass));

            globalStddev += (count(column,myclass) / size()) * stddev;
        }

        return globalStddev;
    }

    /**
     * Compute the stddev of target for the given column (continuous column)
     * The computation use the same principle than the stddevColumnDiscrete, but using bestSplitValue to split the continuous column into a 2 class column
     * @param column
     * @return
     */
    public double stddevColumnContinuous(String column){
        double globalStddev = 0;
        double bestSplit = bestNumericSplit(column);

        double tmpAvg1=0;
        double tmpAvg2=0;

        int count1 = 0;
        int count2 = 0;

        for (DecisionTreeElement e: elements){
            if(e.getDouble(column) < bestSplit){
                tmpAvg1+=e.getDouble(targetName);
                count1++;
            }else{
                tmpAvg2+=e.getDouble(targetName);
                count2++;
            }
        }

        tmpAvg1/=count1;
        tmpAvg2/=count2;


        double tmpStddev1 = 0;
        double tmpStddev2 = 0;

        for (DecisionTreeElement e: elements){
            if(e.getDouble(column) < bestSplit){
                tmpStddev1+=Math.pow((double)e.getDouble(targetName) - tmpAvg1,2);
            }else{
                tmpStddev2+=Math.pow((double)e.getDouble(targetName) - tmpAvg2,2);
            }
        }

        tmpStddev1/=count1;
        tmpStddev1 = Math.sqrt(tmpStddev1);

        tmpStddev2/=count2;
        tmpStddev2 = Math.sqrt(tmpStddev2);

        globalStddev += (count1 / size()) * tmpStddev1;
        globalStddev += (count2 / size()) * tmpStddev2;

        return globalStddev;
    }


    private Map<String,Double> stddevReduction(){
        Map<String,Double> stddevReductions = new HashMap<>();
        for(String predictor: predictors()){
            if(getSchema().get(predictor)){ // If is numeric
                stddevReductions.put(predictor,stddev - stddevColumnContinuous(predictor));
            }else{
                stddevReductions.put(predictor,stddev - stddevColumnDiscrete(predictor));
            }

        }
        return stddevReductions;
    }

    public String bestColumnToSplit(){
        Map<String,Double> stddevReduction = stddevReduction();
        Tuple2<String,Double> bestReduction = null;
        for(Map.Entry<String,Double> e: stddevReduction.entrySet()){
            if(bestReduction == null){
                bestReduction = new Tuple2<String,Double>(e.getKey(),e.getValue());
            }else{
                if(e.getValue()>bestReduction._2){
                    bestReduction = new Tuple2<String,Double>(e.getKey(),e.getValue());
                }
            }
        }
        return bestReduction._1;
    }

    /**
     * Return the sum for a given column (should be a double column) in an efficient way
     * @param column
     * @return
     */
    public double sum(String column){
        return internal_values.get(column).stream().mapToDouble(a->(double)a).sum();
    }

    public Double sum(String column, String dataClass){
        double sum=0;

        for(DecisionTreeElement e: elements){
            if(e.getString(column).equals(dataClass)){
                sum+=e.getDouble(targetName);
            }
        }

        return sum;
    }

    public int count(String column, String dataClass){
        int count=0;

        for(DecisionTreeElement e: elements){
            if(e.getString(column).equals(dataClass)){
                count+=1;
            }
        }

        return count;
    }

    public void sort(String column){
        elements.sort(new Comparator<DecisionTreeElement>() {
            @Override
            public int compare(DecisionTreeElement e1, DecisionTreeElement e2) {
                if(e1.getDouble(column)>e2.getDouble(column)){
                    return 1;
                }else{
                    return 0;
                }
            }
        });
    }



    /**
     * Return all the different class for a nominal variable
     * todo : check for a more efficient way ?
     * @param column
     */
    public Set<String> allClasses(String column){
        Set<String> classes = new HashSet<>();

        for(DecisionTreeElement e: elements){
            classes.add(e.getString(column));
        }

        return classes;
    }

    public Set<String> columns(){
        return schema.keySet();
    }

    public Set<String> predictors(){
        Set<String> predictors = columns();
        predictors.remove(targetName);
        return predictors;
    }

    private Multimap<String,Object> mapValues(List<DecisionTreeElement> elements){
        Multimap<String,Object> map = HashMultimap.create();
        for(DecisionTreeElement e : elements){
            for(String column : e.columns()){
                map.put(column,e.get(column));
            }
        }

        return map;

    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public Map<String, Boolean> getSchema() {
        return schema;
    }

    public void setSchema(Map<String, Boolean> schema) {
        this.schema = schema;
    }

    public DecisionTreeElement get(int index){
        return elements.get(index);
    }


    /**
     * For a given predictor (continuous), return the best split value
     * @param predictor
     * @return
     */
    public Double bestNumericSplit(String predictor){
        double bestSplit=0;
        double totalSum = sum(targetName);

        sort(predictor);
        double rightSum = totalSum;
        double leftSum = 0;

        int rightCount = size();
        int leftCount = 0;

        double bestTmp = 0;

        for (int i=0; i<size(); i++){
            leftSum += elements.get(i).getDouble(targetName);
            rightSum -= elements.get(i).getDouble(targetName);

            leftCount += 1;
            rightCount -= 1;

            if(elements.get(i+1).getDouble(predictor) > elements.get(i).getDouble(predictor)){ // no need to test this split if the values are equals
                double split = (leftSum*leftSum / leftCount) + (rightSum*rightSum / rightCount); // maximal value => best split
                if(split > bestTmp){
                    bestTmp = split;

                    bestSplit = elements.get(i+1).getDouble(predictor)+elements.get(i).getDouble(predictor)/2;
                    // the best split is the value between the value of this element and the value of the next element when split is maximized

                }
            }
        }

        return bestSplit;
    }

    /**
     * For a given predictor (discrete), return the best class to split
     * TODO : this method work but is really ugly and non efficient
     * @param predictor
     * @return
     */
    public String bestNominalSplit( String predictor){
        String bestSplit="";

        double totalSum = sum(targetName);

        Map<String,Double> targetAveragePerClass = avgDiscrete(predictor);

        List<String> sortedClasses = new ArrayList<>(targetAveragePerClass.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue))
                .keySet());

        double rightSum = totalSum;
        double leftSum = 0;

        int rightCount = size();
        int leftCount = 0;

        double bestTmp = 0;

        for(String dataClass : sortedClasses){
            double sumForClass = sum(predictor,dataClass);
            double countForClass = count(predictor,dataClass);

            leftSum += sumForClass;
            rightSum -= sumForClass;

            leftCount += countForClass;
            rightCount -= countForClass;

            double split = (leftSum*leftSum / leftCount) + (rightSum*rightSum / rightCount); // maximal value => best split
            if(split > bestTmp){
                bestTmp = split;

                // todo : to check
                bestSplit = dataClass;
            }
        }

        return bestSplit;
    }


    public Tuple2<DecisionTreeDataset,DecisionTreeDataset> splitTraining(double ratio){
        if(ratio>= 0 && ratio <= 1){
            return new Tuple2<DecisionTreeDataset,DecisionTreeDataset>(
                    new DecisionTreeDataset(schema, elements.subList(0,(int)(elements.size()*ratio)),targetName),
                    new DecisionTreeDataset(schema, elements.subList((int)(elements.size()*ratio),elements.size()),targetName));
        }

        return null;
    }
}
