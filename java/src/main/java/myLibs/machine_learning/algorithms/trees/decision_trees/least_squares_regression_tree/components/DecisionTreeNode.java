package myLibs.machine_learning.algorithms.trees.decision_trees.least_squares_regression_tree.components;

import myLibs.machine_learning.algorithms.trees.decision_trees.least_squares_regression_tree.DecisionTreeDataset;

/**
 * TODO : This class is not really good since it contains the whole datasets used for the training and is used for the prediction.
 * There should be a recursive structure that contains the spliColumn / splitValue without datasets.
 */
public class DecisionTreeNode implements DecisionTreeNodes{
    private DecisionTreeDataset dataset;
    private DecisionTreeNodes leftBranch;
    private DecisionTreeNodes rightBranch;

    private String splitColumn;
    private Object splitValue;


    public DecisionTreeNode(DecisionTreeDataset dataset){
        this.dataset = dataset;
    }

    public void setLeftBranch(DecisionTreeNodes leftBranch) {
        this.leftBranch = leftBranch;
    }

    public void setRightBranch(DecisionTreeNodes rightBranch) {
        this.rightBranch = rightBranch;
    }


    public DecisionTreeNodes getLeftBranch() {
        return leftBranch;
    }

    public DecisionTreeNodes getRightBranch() {
        return rightBranch;
    }

    public String getSplitColumn() {
        return splitColumn;
    }

    public void setSplitColumn(String splitColumn) {
        this.splitColumn = splitColumn;
    }

    public Object getSplitValue() {
        return splitValue;
    }

    public void setSplitValue(Object splitValue) {
        this.splitValue = splitValue;
    }


}
