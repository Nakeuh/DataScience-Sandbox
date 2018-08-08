package usage.machineLearning.algorithms.trees;

import myLibs.machine_learning.algorithms.trees.decision_trees.least_squares_regression_tree.DecisionTreeDataset;
import myLibs.machine_learning.algorithms.trees.decision_trees.least_squares_regression_tree.MyLeastSquareRegressionTree;

import scala.Tuple2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class TestDecisionTree {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        MyLeastSquareRegressionTree dt = new MyLeastSquareRegressionTree();

        DecisionTreeDataset dataset = readData();

        Tuple2<DecisionTreeDataset,DecisionTreeDataset> splitted = dataset.splitTraining(0.7);

        dt.trainDecisionTree(splitted._1);

        DecisionTreeDataset results = dt.prediction(splitted._2);
    }

    private static DecisionTreeDataset readData(){
        return null;
    }

}
