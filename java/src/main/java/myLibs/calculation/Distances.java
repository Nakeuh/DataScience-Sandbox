package myLibs.calculation;

import java.util.List;

/**
 * This class contains function used to compute distance between two elements
 */
public class Distances {

    /**
     * Compute the euclidean distance between two element of same dimension
     * @param e1 the first element
     * @param e2 the second element. Should have the same dimension as e1
     * @return result
     */
    public static double vectorEuclideanDistance(final List<Double> e1, final List<Double> e2) {
        double distance = 0;
        for (int i = 0; i < e1.size(); i++) {
            distance += Math.pow(e1.get(i) - e2.get(i), 2);
        }

        if (distance > 0)
            distance = Math.sqrt(distance);

        return distance;
    }
}
