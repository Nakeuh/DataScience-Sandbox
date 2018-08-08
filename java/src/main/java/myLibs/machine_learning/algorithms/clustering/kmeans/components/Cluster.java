package myLibs.machine_learning.algorithms.clustering.kmeans.components;

import java.util.ArrayList;
import java.util.List;

public class Cluster {

    public List<Element> datas;

    public List<Double> center;

    public Cluster() {
        datas = new ArrayList<Element>();
        center = new ArrayList<Double>();
    }

    public void display(int limit) {
        System.out.println("Center : " + this.center);
        System.out.println(this.datas.size() + " elements");

        int count = 0;
        for (Element dArr : this.datas) {
            System.out.println("ElementClassifier :" + dArr.getName());
            count++;
            if(count>= limit){
                break;
            }
        }
        if (this.datas.size() > limit) {
            System.out.println("... ("+(this.datas.size()-limit)+" more)");

        }
    }
}