package myLibs.machine_learning.algorithms.trees.decision_trees.least_squares_regression_tree;

import myLibs.machine_learning.algorithms.trees.decision_trees.least_squares_regression_tree.components.DecisionTreeLeafNode;
import myLibs.machine_learning.algorithms.trees.decision_trees.least_squares_regression_tree.components.DecisionTreeNode;
import myLibs.machine_learning.algorithms.trees.decision_trees.least_squares_regression_tree.components.DecisionTreeNodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Least Square Regression Tree (LS) in an algorithm of Decision Tree used for Regression.
 * This is a Binary Decision Tree (only 2 branch for each nodes)
 * This implementation is largely inspired from :
 *          http://www.dcc.fc.up.pt/~ltorgo/PhD/th3.pdf
 *          http://www.saedsayad.com/decision_tree.htm and http://www.saedsayad.com/decision_tree_reg.htm
 * Carefull : This code is not well written (at all :) ) A lot of reorganization and cleaning are necessary
 */
public class MyLeastSquareRegressionTree {

    private final int termination_number_elements = 10;
    private final double termination_deviation_coefficient = 0.1;
    private DecisionTreeNodes decisionTree;

    public void trainDecisionTree(DecisionTreeDataset dataset){
        decisionTree = growingTree(dataset);
    }

    /**
     * The function used to train the algorithm and build the DecisionTree
     * @param dataset
     * @return
     */
    private DecisionTreeNodes growingTree(DecisionTreeDataset dataset){
        boolean termination = false;

        double deviation_coefficient = dataset.stddev() / dataset.avg();

        // If not enough element in the dataset or deviation of the dataset is low (homogenous dataset), stop the splitting
        if(dataset.size()<termination_number_elements || deviation_coefficient<termination_deviation_coefficient){
            termination = true;
        }

        if (termination){ // End of the algorithm
            double value = dataset.avg();
            return new DecisionTreeLeafNode(value);
        }else{ // Split the dataset
            DecisionTreeNode node = new DecisionTreeNode(dataset);

            int bestSplit = 0 ;

            String columnToSplit = dataset.bestColumnToSplit();

            node.setSplitColumn(columnToSplit);

            List<DecisionTreeElement> leftBranch = new ArrayList<DecisionTreeElement>();
            List<DecisionTreeElement> rightBranch = new ArrayList<DecisionTreeElement>();

            if(dataset.getSchema().get(columnToSplit)){ // if continuous column
                double splitValue = dataset.bestNumericSplit(columnToSplit);
                node.setSplitValue(splitValue);
                for(DecisionTreeElement e: dataset.getElements()){
                    if(e.getDouble(columnToSplit)<splitValue){
                        leftBranch.add(e);
                    }else{
                        rightBranch.add(e);
                    }
                }
            }else{ // if discrete column
                String splitValue = dataset.bestNominalSplit(columnToSplit);
                node.setSplitValue(splitValue);
                for(DecisionTreeElement e: dataset.getElements()){
                    if(e.getString(columnToSplit).equals(splitValue)){
                        leftBranch.add(e);
                    }else{
                        rightBranch.add(e);
                    }
                }
            }

            node.setLeftBranch(growingTree(new DecisionTreeDataset(dataset.getSchema(),leftBranch,dataset.getTargetName())));
            node.setRightBranch(growingTree(new DecisionTreeDataset(dataset.getSchema(),rightBranch,dataset.getTargetName())));
            return node;
        }
    }


    public double prediction(DecisionTreeElement e, Map<String,Boolean> schema){
        return predict(e,schema,decisionTree);
    }

    /**
     * The function to use to get a prediction from a trained DecisionTree and an input data
     * @param e
     * @param schema
     * @param d
     * @return
     */
    private double predict(DecisionTreeElement e, Map<String,Boolean> schema, DecisionTreeNodes d){
        if(d instanceof DecisionTreeNode){
            DecisionTreeNode node = (DecisionTreeNode)d;
            if(schema.get(e)){ // is numeric
                if(e.getDouble(node.getSplitColumn()) < (double)node.getSplitValue()){
                    return predict(e,schema,node.getLeftBranch());
                }else{
                    return predict(e,schema,node.getRightBranch());
                }
            }else{ // is discrete
                if(e.getString(node.getSplitColumn()).equals((String)node.getSplitValue())){
                    return predict(e,schema,node.getLeftBranch());
                }else{
                    return predict(e,schema,node.getRightBranch());
                }
            }
        }else{
            DecisionTreeLeafNode leaf = (DecisionTreeLeafNode)d;
            return leaf.getValue();
        }
    }


    public DecisionTreeDataset prediction(DecisionTreeDataset dataset){
        Map<String,Boolean> schema = dataset.getSchema();

        List<DecisionTreeElement> elements = new ArrayList<>();
        for(DecisionTreeElement e: dataset.getElements()){
            DecisionTreeElement element = e;
            element.addValue("prediction", prediction(e,schema));
            elements.add(element);
        }

        schema.put("prediction",true);

        return new DecisionTreeDataset(schema,elements,dataset.getTargetName());
    }
}
