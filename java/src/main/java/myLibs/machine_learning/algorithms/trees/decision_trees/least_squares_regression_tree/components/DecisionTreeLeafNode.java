package myLibs.machine_learning.algorithms.trees.decision_trees.least_squares_regression_tree.components;

public class DecisionTreeLeafNode implements DecisionTreeNodes{
    private double value;

    public DecisionTreeLeafNode(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
