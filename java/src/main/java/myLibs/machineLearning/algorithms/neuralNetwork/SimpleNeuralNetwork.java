package myLibs.machineLearning.algorithms.neuralNetwork;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.javatuples.Pair;

/**
 * Inspired by : http://neuralnetworksanddeeplearning.com/chap1.html by Michael Nielsen
 */

public class SimpleNeuralNetwork {

    private int numLayer;

    private double[][] biases;

    private double[][][] weights;

    private int[] sizes;

    /**
     * @param sizes contains the number of neurons int the respective layers
     */
    public SimpleNeuralNetwork(final int[] sizes) {
        numLayer = sizes.length;
        this.sizes = sizes;
        biases = new double[numLayer][];
        weights = new double[numLayer - 1][][];

        // Initialize Bias and Weights arrays :
        // No Bias for first layer (Input layer)
        // No Weights for last layer
        System.out.println("numLayer : " + numLayer);

        // initialize all Bias and Weights with default values
        for (int layer = 1; layer < numLayer; layer++) {
            biases[layer] = new double[sizes[layer]];
            weights[layer - 1] = new double[sizes[layer]][];
            for (int neuron = 0; neuron < sizes[layer]; neuron++) {
                weights[layer - 1][neuron] = new double[sizes[layer - 1]];
                biases[layer][neuron] = defaultBias();
                for (int weight = 0; weight < weights[layer - 1][neuron].length; weight++) {
                    weights[layer - 1][neuron][weight] = defaultWeight();
                }
            }
        }

    }

    /**
     * Compute a default bias value
     *
     * @return
     */
    private double defaultBias() {
        return new Random().nextGaussian();
    }

    /**
     * Compute a default weight value
     *
     * @return
     */
    private double defaultWeight() {
        return new Random().nextGaussian();
    }

    /**
     * logistical function
     *
     * @param d
     * @return
     */
    private double sigmoid(final double d) {
        return 1 / (1 + Math.exp(-d));
    }

    /**
     * derivative of the logistical function
     *
     * @param d
     * @return
     */
    private double sigmoidPrime(final double d) {
        return sigmoid(d) * (1 - sigmoid(d));
    }

    /**
     * Give the output of the network for a given input
     * // TODO : Error in this function
     * @param input
     * @return
     */
    private double[] feedForward(final double[] input) {
        double[] layerResult = input;

        for (int layer = 1; layer < numLayer; layer++) {
            layerResult = dotProduct21(weights[layer - 1], layerResult);
            for (int i = 0; i < layerResult.length; i++) {
                layerResult[i] = sigmoid(layerResult[i] + biases[layer][i]); // should return a 2d array
            }
        }

        return layerResult;
    }

    /**
     * Train the neural network using mini-batch stochastic gradient descent
     *
     * @param trainingInputs : list of tuples (x,y) where x is the input and y the expected output
     * @param numEpoch
     * @param miniBatchSize
     * @param learningRate
     */
    public void stochasticGradientDescent(final List<Pair<double[], double[]>> trainingInputs,
                                          final int numEpoch,
                                          final int miniBatchSize,
                                          final double learningRate,
                                          final List<Pair<double[], double[]>> testInputs) {
        final int numTrainingInputs = trainingInputs.size();
        int numTestInputs = 0;
        if (testInputs != null) {
            numTestInputs = testInputs.size();
        }

        System.out.println("Starting training the neural network with " + numTrainingInputs + " input datas");
        for (int epoch = 0; epoch < numEpoch; epoch++) {
            final long time = System.currentTimeMillis();

            Collections.shuffle(trainingInputs);

            for (int j = 0; j < numTrainingInputs; j += miniBatchSize) {
                final List<Pair<double[], double[]>> miniBatch = trainingInputs.subList(j, j + miniBatchSize);
                updateMiniBatch(miniBatch, learningRate);
            }
            System.out.println(
                    "Epoch " + epoch + " completed in " + (System.currentTimeMillis() - time) / 1000 + " seconds");

            if (testInputs != null) {
                final int result = evaluate(testInputs);
                System.out.println(
                        "Success : " + result + " on " + numTestInputs + " => " + (double)result*100/numTestInputs+ "%");
            }
            // drawNetwork();
        }
    }

    /**
     * Update the neural network's weights and biases with gradient descent using backpropagation to a single mini batch
     *
     * @param miniBatch
     * @param learningRate
     */
    private void updateMiniBatch(final List<Pair<double[], double[]>> miniBatch, final double learningRate) {

        // Shape nabla_b on biases array
        final double[][] nabla_b = new double[numLayer][];
        for (int layer = 1; layer < numLayer; layer++) {
            nabla_b[layer] = new double[sizes[layer]];
        }

        // Shape nabla_w on weights array
        final double[][][] nabla_w = new double[weights.length][][];

        for (int layer = 1; layer < numLayer; layer++) {
            nabla_w[layer - 1] = new double[weights[layer - 1].length][];

            for (int neuron = 0; neuron < sizes[layer]; neuron++) {
                nabla_w[layer - 1][neuron] = new double[weights[layer - 1][neuron].length];
            }
        }

        for (final Pair<double[], double[]> input : miniBatch) {
            final Pair<double[][], double[][][]> backProp = backpropagation(input);

            final double[][] delta_nabla_b = backProp.getValue0();
            final double[][][] delta_nabla_w = backProp.getValue1();

            for (int layer = 1; layer < numLayer; layer++) {
                for (int neuron = 0; neuron < sizes[layer]; neuron++) {
                    nabla_b[layer][neuron] += delta_nabla_b[layer][neuron];
                    biases[layer][neuron] -= (learningRate / miniBatch.size()) * nabla_b[layer][neuron];
                    for (int weight = 0; weight < weights[layer - 1][neuron].length; weight++) {
                        nabla_w[layer - 1][neuron][weight] += delta_nabla_w[layer - 1][neuron][weight];
                        weights[layer - 1][neuron][weight] -=
                                (learningRate / miniBatch.size()) * nabla_w[layer - 1][neuron][weight];
                    }
                }
            }
        }
    }

    /**
     * Return a pair nabla_b,nabla_w representing the gradient for the cost function c_x
     * // TODO : error in this function
     * @param input
     * @return
     */
    private Pair<double[][], double[][][]> backpropagation(final Pair<double[], double[]> input) {
        // Shape nabla_b on biases array
        final double[][] nabla_b = new double[biases.length][];
        for (int layer = 1; layer < numLayer; layer++) {
            nabla_b[layer] = new double[sizes[layer]];
        }

        // Shape nabla_w on weights array
        final double[][][] nabla_w = new double[weights.length][][];
        for (int layer = 1; layer < numLayer; layer++) {
            nabla_w[layer - 1] = new double[weights[layer - 1].length][];
            for (int neuron = 0; neuron < sizes[layer]; neuron++) {
                nabla_w[layer - 1][neuron] = new double[weights[layer - 1][neuron].length];
            }
        }

        // Free forward
        double[] activation = input.getValue0();
        final double[][] activations = new double[numLayer][];
        activations[0] = activation;
        final double[][] zVectors = new double[numLayer - 1][];

        for (int layer = 1; layer < numLayer; layer++) {
            final int nbNeuron = sizes[layer];
            double[] z = new double[nbNeuron];
            z=dotProduct21(weights[layer-1],activations[layer-1]);
            activation = new double[z.length];

            for(int i=0 ; i<z.length;i++){
                z[i]+=biases[layer][i];// should return a 2d array
                activation[i]=sigmoid(z[i]);
            }
            zVectors[layer-1]=z;
            activations[layer]=activation;
        }

        // Backward pass
        final int lastLayer = numLayer - 1;

        double[] delta = costDerivative(activations[lastLayer], input.getValue1());

        for (int i = 0; i < delta.length; i++) {
            delta[i] *= sigmoidPrime(zVectors[lastLayer - 1][i]);
        }

        nabla_b[lastLayer] = delta;


        // TODO : transpose activations ???
        //dotProduct21(delta, activations[lastLayer - 1])

        for (int neuron = 0; neuron < sizes[lastLayer]; neuron++) {
            for (int weight = 0; weight < weights[lastLayer - 1][neuron].length; weight++) {
                nabla_w[lastLayer - 1][neuron][weight] = delta[neuron] * activations[lastLayer - 1][weight];
            }
        }


        for (int layer = numLayer - 2; layer > 0; layer--) {

            final double[] z = zVectors[layer - 1];

            final double[] sigmoidPrime = new double[z.length];

            for (int neuron = 0; neuron < sizes[layer]; neuron++) {
                sigmoidPrime[neuron] = sigmoidPrime(z[neuron]);
            }

            final double[] oldDelta = delta;
            delta = new double[sizes[layer]];

            final double[][] transposedWeight = new double[weights[layer][0].length][weights[layer].length];

            for (int neuron = 0; neuron < sizes[layer + 1]; neuron++) {
                for (int weight = 0; weight < weights[layer][neuron].length; weight++) {
                    transposedWeight[weight][neuron] = weights[layer][neuron][weight];
                }
            }

            for (int i = 0; i < transposedWeight.length; i++) {
                double sum = 0;
                for (int j = 0; j < transposedWeight[i].length; j++) {
                    sum += transposedWeight[i][j] * oldDelta[j];
                }
                delta[i] = sum * sigmoidPrime[i];
            }

            nabla_b[layer] = delta;


            for (int neuron = 0; neuron < sizes[layer]; neuron++) {
                for (int weight = 0; weight < weights[layer - 1][neuron].length; weight++) {
                    //nabla_w[layer - 1][neuron][weight] = delta[neuron] * activations[layer - 1][weight];
                    nabla_w[layer - 1][neuron][weight] = delta[neuron] * activations[layer - 1][neuron]; // TODO : work better than previous line, why ?
                }
            }
        }

        return new Pair<double[][], double[][][]>(nabla_b, nabla_w);
    }

    private double[] costDerivative(final double[] outputActivation, final double[] expectedOutput) {

        final double[] derivative = new double[outputActivation.length];

        for (int i = 0; i < derivative.length; i++) {
            derivative[i] = outputActivation[i] - expectedOutput[i];
        }

        return derivative;
    }

    /**
     * Return the number of correctly classified input
     *
     * @param testInputs
     * @return
     */
    private int evaluate(final List<Pair<double[], double[]>> testInputs) {
        int count = 0;
        for (final Pair<double[], double[]> input : testInputs) {

            final double[] output = feedForward(input.getValue0());

            int outputResult = 0;
            for (int i = 1; i < output.length; i++) {
                if (output[i] > output[outputResult]) {
                    outputResult = i;
                }
            }

            int expectedResult = 0;
            for (int i = 1; i < input.getValue1().length; i++) {
                if (output[i] > input.getValue1()[expectedResult]) {
                    expectedResult = i;
                }
            }

            if (outputResult == expectedResult) {
                count++;
            }
/*
            System.out.print("expected :" + expectedResult + " : ");
            for (int i = 0; i < input.getValue1().length; i++) {
                System.out.print((int) input.getValue1()[i] + " ");
            }
            System.out.println();
            System.out.print("output :" + outputResult + " : ");
            for (int i = 0; i < output.length; i++) {
                System.out.print(output[i] + " ");
            }
            System.out.println();
*/
        }

        return count;

    }

    private void drawNetwork() {
        for (int i = 1; i < numLayer; i++) {
            System.out.println("Layer " + i);

            System.out.println("Biases :");
            for (int j = 0; j < biases[i].length; j++) {
                System.out.print(biases[i][j] + " ");
            }
            System.out.println();

            System.out.println("Weights :" + weights[i - 1].length);
            for (int j = 0; j < weights[i - 1].length; j += 100) {
                System.out.print(weights[i - 1][j] + "\n");
            }
            System.out.println();
        }
    }

    private double[] dotProduct21(final double[][] a, final double[] b) {

        final double[] result = new double[a.length];

        for (int i = 0; i < a.length; i++) {
            double sum = 0;

            if (a[i].length != b.length) {
                System.out.println("Error size array in dotProduct!");
                System.exit(0);
            }

            for (int j = 0; j < a[i].length; j++) {
                sum += a[i][j] * b[j];
            }
            result[i] = sum;
        }
        return result;
    }
}