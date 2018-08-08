package myLibs.machine_learning.algorithms.trees.decision_trees.least_absolute_deviation_regression_tree;

import myLibs.machine_learning.algorithms.trees.decision_trees.least_squares_regression_tree.DecisionTreeDataset;
import myLibs.machine_learning.algorithms.trees.decision_trees.least_squares_regression_tree.components.DecisionTreeLeafNode;
import myLibs.machine_learning.algorithms.trees.decision_trees.least_squares_regression_tree.components.DecisionTreeNode;
import myLibs.machine_learning.algorithms.trees.decision_trees.least_squares_regression_tree.components.DecisionTreeNodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Least Absolute Deviation Regression Tree (LAD) in an algorithm of Decision Tree used for Regression.
 * This implementation is largely inspired from this paper : http://www.dcc.fc.up.pt/~ltorgo/PhD/th3.pdf
 * I have also implemented the alternative method with the Least Square Regression Tree (LS) inspired from the same paper
 * LAD is more robust than LS but requires more computational time.
 */
public class MyLeastAbsoluteDeviationRegressionTree {

    /**
     * Recursive partitioning of the dataset in a decison tree
     * @param dataset
     * @return
     */
    public DecisionTreeNodes partitioning(DecisionTreeDataset dataset){
        boolean termination = false; // todo : what is termination

        if (termination){
            double value = 0; // todo : what is value ?
            return new DecisionTreeLeafNode(value);
        }else{
            int splitIndex = 0 ;// todo find best split
            DecisionTreeNode node = new DecisionTreeNode(dataset);
            node.setLeftBranch(partitioning(dataset.subdataset(0,splitIndex)));
            node.setRightBranch(partitioning(dataset.subdataset(splitIndex,dataset.size()-1)));
            return node;
        }
    }


    public DecisionTreeNodes growingTree(DecisionTreeDataset dataset){
        boolean termination = false; // todo : what is termination

        if (termination){
            double value = dataset.avg(dataset.getTargetName());
            return new DecisionTreeLeafNode(value);
        }else{
            double bestSplit = 0 ;

            for(String predictor : dataset.predictors()){
                double split = bestSplit(dataset,predictor);

                if(true){ // todo : test if better
                    bestSplit=split;
                }
            }

            DecisionTreeNode node = new DecisionTreeNode(dataset);
            //node.setLeftBranch(growingTree(dataset.subdataset(0,bestSplitValue)));
            //node.setRightBranch(growingTree(dataset.subdataset(bestSplitValue,dataset.size()-1)));
            return node;
        }
    }

    public double bestSplit(DecisionTreeDataset dataset, String predictor){
        double bestSplit = 0;
        if(dataset.getSchema().get(predictor)){    // If the variable 'predictor' is numeric
            bestSplit = bestNumericSplit(dataset,predictor);
        }else {                                    // If the variable 'predictor' is nominal
            bestSplit = bestNominalSplit(dataset,predictor);
        }

        return bestSplit;
    }

    private double bestNumericSplit(DecisionTreeDataset dataset, String predictor){
        double totalSum = dataset.sum(dataset.getTargetName());

        dataset.sort(predictor);
        double rightSum = totalSum;
        double leftSum = 0;

        int rightCount = dataset.size();
        int leftCount = 0;

        double bestTmp = 0;
        double bestSplit = 0;

        for (int i=0; i<dataset.size(); i++){
            leftSum += dataset.get(i).getDouble(dataset.getTargetName());
            rightSum -= dataset.get(i).getDouble(dataset.getTargetName());

            leftCount += 1;
            rightCount -= 1;

            if(dataset.get(i+1).getDouble(predictor) > dataset.get(i).getDouble(predictor)){ // no need to test this split if the values are equals
                double split = (leftSum*leftSum / leftCount) + (rightSum*rightSum / rightCount); // maximal value => best split
                if(split > bestTmp){
                    bestTmp = split;

                    bestSplit = (dataset.get(i+1).getDouble(predictor)+dataset.get(i).getDouble(predictor))/2;
                    // the best split is the value between the value of this element and the value of the next element when split is maximized

                }
            }
        }


        return bestSplit;
    }

    /**
     * TODO : this method work but is really ugly and non efficient
     * @param dataset
     * @param predictor
     * @return
     */
    private double bestNominalSplit(DecisionTreeDataset dataset, String predictor){
        double totalSum = dataset.sum(dataset.getTargetName());

        Map<String,Double> targetAveragePerClass = dataset.avgDiscrete(predictor);

        List<String> sortedClasses = new ArrayList<>(targetAveragePerClass.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue))
                .keySet());

        double rightSum = totalSum;
        double leftSum = 0;

        int rightCount = dataset.size();
        int leftCount = 0;

        double bestTmp = 0;
        double bestSplit = 0;

        for(String dataClass : sortedClasses){
            double sumForClass = dataset.sum(predictor,dataClass);
            double countForClass = dataset.count(predictor,dataClass);

            leftSum += sumForClass;
            rightSum -= sumForClass;

            leftCount += countForClass;
            rightCount -= countForClass;

            double split = (leftSum*leftSum / leftCount) + (rightSum*rightSum / rightCount); // maximal value => best split
            if(split > bestTmp){
                bestTmp = split;

                // todo : to check
                bestSplit = sortedClasses.indexOf(dataClass);
            }
        }

        return bestSplit;
    }




















    public void train(DecisionTreeDataset dataset){

        // Take the training dataset

        // Train the Decison Tree

        // Return the model

        // ?
    }

    public void predict(){
        // Take a data and male a prediction
    }


}
