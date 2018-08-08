package usage.machineLearning.algorithms.clustering;


import myLibs.machine_learning.algorithms.clustering.kmeans.MyKMeans;
import myLibs.machine_learning.algorithms.clustering.kmeans.components.Cluster;
import myLibs.machine_learning.algorithms.clustering.kmeans.components.Element;
import org.json.JSONObject;
import utils.FileUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Quick example for using KMeans clustering algorithm.
 * Regroup League of Legends champions by types based on there stats.
 * (League of Legends champions also strongly depends of their spells that are not taken into account here)
 */
public class TestKMeans {

    public static void main(String[] args){
        try {
            Cluster[] clusters = MyKMeans.clusterize(4,getData());
            MyKMeans.displayClusters(clusters);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Parse champions data.
     * Data come from a local json. You can get a more recent one using the riot api
     * https://developer.riotgames.com/api-methods/#lol-static-data-v3
     * @return
     * @throws Exception
     */
    public static List<Element> getData()throws Exception{
        String content = FileUtil.readFile("src/main/resources/riot/champions.json");

        JSONObject json = new JSONObject(content.toString()).getJSONObject("data");

        List<Element> list = new ArrayList<Element>();
        for(String champion : json.keySet()){
            JSONObject stats = json.getJSONObject(champion).getJSONObject("stats");
            List<Double> statsList = new ArrayList<Double>();

            // Warning : This will work only if we are sure that the stats are in the same order for each champion.
            // If not, you have to manually request each stat one by one.
            // You may also want to use only a part of the stats, so this should be modify
            for(String stat : stats.keySet()){
                statsList.add(stats.getDouble(stat));
            }

            Element element = new Element(champion, statsList);

            list.add(element);
        }

        return list;
    }
}
