package usage.machineLearning.algorithms.neuralNetwork;

import myLibs.datasets.mnist.MnistExtract;
import myLibs.machineLearning.algorithms.neuralNetwork.SimpleNeuralNetwork;
import org.javatuples.Pair;

import java.util.List;

public class TestSimpleNeuralNetwork {

    /**
     * Stuck at 62/63 % success
     * => Improve the algorithm. Possible to go to 80% (at least I hope :) )
     * @param args
     */
    public static void main(String[] args){
        final int[] sizes = { 784, 30, 10 };
        final SimpleNeuralNetwork network = new SimpleNeuralNetwork(sizes);

        final List<Pair<double[], double[]>> imagesTest = MnistExtract
                .extractInPair("src/main/resources/mnist/test/t10k-images-idx3-ubyte", "src/main/resources/mnist/test/t10k-labels-idx1-ubyte");
        final List<Pair<double[], double[]>> imagesTraining = MnistExtract
                .extractInPair("src/main/resources/mnist/train/train-images-idx3-ubyte", "src/main/resources/mnist/train/train-labels-idx1-ubyte");

        final int epoch = 30;
        final int batchSize = 10;
        final double learningRate = 3;

        network.stochasticGradientDescent(imagesTraining, epoch, batchSize, learningRate,imagesTest);
    }
}
