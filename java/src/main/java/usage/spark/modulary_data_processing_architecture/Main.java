package usage.spark.modulary_data_processing_architecture;

import java.util.List;

import myLibs.spark.modulary_data_processing_architecture.DataProcessor;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.json.JSONArray;
import org.json.JSONObject;

import scala.Tuple2;
import usage.spark.modulary_data_processing_architecture.modules.paris_market.MarketModule;
import usage.spark.modulary_data_processing_architecture.modules.twitter.TwitterModule;
import usage.spark.modulary_data_processing_architecture.modules.utils.Constantes;
import usage.spark.modulary_data_processing_architecture.modules.utils.SparkUtils;
import usage.spark.modulary_data_processing_architecture.modules.utils.Utils;

/**
 * This example show how to use the modulary data processing pipeline with a real time and a static source of data.
 * The real time data is a stream of tweet from twitter (will keep only those located in paris)
 * The static data is data file about the markets in paris
 * These data will be used to detect sensitive area in paris city.
 * A sensitive area is an area where there is multiple market opened and tweets that express a sense of worry
 * For that, Paris will be divided in square areas called 'zones'
 */
public class Main implements Constantes {

    public static void main(String[] args) {

        JavaStreamingContext ssc = SparkUtils.initStreamingContext("SparkContext",
                Durations.seconds(SPARK_BATCH_DURATION));

        DataProcessor dp = new DataProcessor();

        // Add the twitter module
        // The pertinence is set to 1 because if the 'worry score' for a zone is high, there is a high probability that this is a real problem
        dp.addModule(new TwitterModule(ssc, 1, true));
        // Add the market module
        // The pertinence is set to 0.5 because a high 'market score' for a zone may correspond to a normal situation
        dp.addModule(new MarketModule(ssc, 0.5));


        // Fuse results and send to another component
        dp.getMeanedResultsByLabel(10, 10)
          .foreachRDD(rdd -> {
            sendSensiblesAreaLayer(rdd.collect());
          });

        ssc.start();

        try {
            ssc.awaitTermination();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send all sensibles areas to the endpoint URL_THREATS (interface Constantes)
     * Data are sent in json format
     * @param list sensibles areas
     */
    private static void sendSensiblesAreaLayer(List<Tuple2<String, Double>> list) {
        JSONArray array = new JSONArray();
        for (Tuple2<String, Double> e : list) {
            JSONObject data = new JSONObject();
            data.put("id", e._1);
            data.put("score", e._2);
            array.put(data);
        }
        // Send data
        //Utils.putJson(URL_THREATS, array.toString());
        System.out.println("The sending of data is commented in the code. Uncomment it if you want it to work (if you have a working endpoint to send data)");
    }

}