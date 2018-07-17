package myLibs.machineLearning.algorithms.clustering;

import java.util.ArrayList;
import java.util.List;

import myLibs.machineLearning.algorithms.clustering.components.*;
import myLibs.calculation.*;
import utils.Util;

/**
 * KMEAN Algotihm :
 * 1) Initialize K centers with Datas (choosen randomly)
 * 2) Affect datas to clusters (Minimal Euclidian Distance between data and the center of the cluster)
 * 3) Recalculate center for each cluster (mean of datas)
 * 4) Do 2)
 * 5) If centers have changed, go to 3)
 *
 * @author Victor Duvert
 */
public class MyKMeans {

    public static void clusterize(int k, List<Element> datas) {
        Cluster[] clusters = new Cluster[k];

        if (k > datas.size()) {
            System.out.println("Error, not enough datas.");
            return;
        }

        // Choose a center for each cluster
        for (int i = 0; i < k; i++) {
            clusters[i] = new Cluster();
            clusters[i].center = datas.get(i).getTraits();
            System.out.println("Initial center chosen : " + datas.get(i).getName() + " | " + datas.get(i).getTraits());
        }

        // Affect datas to clusters
        for (Element element : datas) {
            clusters[findCluster(element, clusters)].datas.add(element);
        }

        Boolean changed = true;
        int iteration = 0;

        // While center change
        while (changed) {
            // Recalculate centers
            for (int j = 0; j < k; j++) {
                List<Double> newCentre = newCenter(clusters[j]);
                changed = changed && (!Util.equals(newCentre, clusters[j].center));
                clusters[j].center = newCentre;
                clusters[j].datas.clear();
            }

            // Affect datas to clusters //
            for (Element element : datas) {
                clusters[findCluster(element, clusters)].datas.add(element);
            }
            iteration++;

        }

        System.out.println("KMeans finished in " + iteration + " iterations.");
        displayClusters(clusters);
    }

    /**
     * Find center with minimal euclidean distance e->centre
     *
     * @param e : element
     * @param clusters : clusters
     * @return
     */
    private static int findCluster(Element e, Cluster[] clusters) {

        double distanceMini = Distances.vectorEuclideanDistance(e.getTraits(), clusters[0].center);
        int centre = 0;

        for (int i = 0; i < clusters.length; i++) {
            double dist = Distances.vectorEuclideanDistance(e.getTraits(), clusters[i].center);

            if (dist < distanceMini) {
                distanceMini = dist;
                centre = i;
            }
        }
        return centre;
    }

    /**
     * Calculate the mean of a cluster of datas
     * @param cluster
     * @return
     */
    private static List<Double> newCenter(Cluster cluster) {
        List<Double> newCenter = new ArrayList<Double>(cluster.center.size());

        for (int i = 0; i < cluster.center.size(); i++) {
            newCenter.add(0d);
            for (Element e : cluster.datas) {
                newCenter.set(i, newCenter.get(i) + e.getTraits().get(i));
            }
            newCenter.set(i, newCenter.get(i) / cluster.datas.size());
        }

        return newCenter;
    }

    private static void displayClusters(Cluster[] clusters) {
        System.out.println();
        int i = 0;
        for (Cluster c : clusters) {
            System.out.println();
            System.out.println("Cluster number " + i);
            c.display(20);
            i++;
        }
    }
}