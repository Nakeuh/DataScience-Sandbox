package usage.spark.modulary_data_processing_architecture.modules.twitter;

import java.util.List;

import myLibs.spark.modulary_data_processing_architecture.AModule;
import org.apache.spark.mllib.clustering.StreamingKMeans;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;

import scala.Tuple2;
import scala.Tuple3;
import twitter4j.Status;
import twitter4j.TwitterObjectFactory;
import usage.spark.modulary_data_processing_architecture.modules.utils.*;

/**
 * Implementation of a modul to process twitter data
 */
public class TwitterModule extends AModule implements Constantes {

	private static final int NB_CLUSTER = 2;
	private static final int dimensions = 100;

	private StreamingKMeans model;
	private JavaDStream<Status> inputTweetStream;

	public TwitterModule(JavaStreamingContext ssc, double pertinence, boolean simulated) {
		super(ssc, pertinence);

		initModel();
		trainModel();

		if (simulated) {
			inputTweetStream = createTweetStreamFromSimulatedDatas();
		} else {
			inputTweetStream = createTweetStreamFromTwitter();
		}

	}

	private void initModel() {
		// Initialize Streaming KMeans Model
		model = new StreamingKMeans().setK(NB_CLUSTER).setDecayFactor(1).setRandomCenters(dimensions, 100, 0);
	}

	private void trainModel() {
		// Generate training Stream
		JavaDStream<Vector> trainingStream = ssc.textFileStream(TRAINING_DATA_PATH_SPARK)
				.map(line -> StringUtils.customSplit(line)).transform(rdd -> {
					return VectorUtils.transformListStringToVector(rdd, dimensions);
				});

		// Train the model
		model.trainOn(trainingStream);
	}

	/**
	 * Calcule a score for each geographical zone Use a KMeans clustering to detect
	 * if a tweet is 'casual' or 'dangerous'
	 * 
	 * score = nbDangerousTweetInZOne *
	 * (numberTweetReceived/minimalNumberTweetInZone)
	 */
	@Override
	protected JavaPairDStream<String, Double> computeStream() {
		// Filter and parse inputStream to vectorStream with labels
		JavaDStream<VectorizedTweet<Status>> tweetStream = inputTweetStream
				.window(Durations.seconds(30), Durations.seconds(10))
				.filter(tweet -> ZoneUtils.isInParis(tweet.getGeoLocation()))
				.mapToPair(tweet -> new Tuple2<Status, List<String>>(tweet, StringUtils.customSplit(tweet.getText())))
				.transform(rdd -> VectorUtils.<Status>transformListStringToLabeledVector(rdd, dimensions));
		// Affect each data to a cluster, thanks to the trained KMeans model
		JavaDStream<Tuple3<Status, Integer, ZoneID>> clusterizedZonedTweets = model
				.predictOnValues(tweetStream.mapToPair(lp -> new Tuple2<Status, Vector>(lp.label(), lp.features())))
				.map(clusterizedTweet -> new Tuple3<Status, Integer, ZoneID>(clusterizedTweet._1, clusterizedTweet._2,
						ZoneUtils.getZoneID(clusterizedTweet._1.getGeoLocation())));

		JavaPairDStream<String, Double> outputStream = clusterizedZonedTweets.mapToPair(
				zonedClusterizedTweet -> new Tuple2<String, Tuple2<Double, Integer>>(zonedClusterizedTweet._3().getID(),
						new Tuple2<Double, Integer>((double) zonedClusterizedTweet._2(), 1)))
				.mapToPair(t -> {

					return t;
				})

				// aggregate scores by zone

				.reduceByKey(
						(a, b) -> new Tuple2<Double, Integer>(1 / ((a._1 + b._1) * Math.pow(10, -10) + 1), a._2 + b._2))
				// calculate confidence score (depend of the number of tweets)
				.mapToPair(t -> {
					double scoreWithConfidence = 0;
					if (t._2._2 > maxTweets)
						scoreWithConfidence = t._2._1;
					else
						scoreWithConfidence = t._2._1 * (t._2._2 / maxTweets);

					return new Tuple2<String, Double>(t._1, scoreWithConfidence);
				});

		return outputStream;
	}

	private JavaDStream<Status> createTweetStreamFromTwitter() {
		return TwitterUtils.createStream(ssc, Utils.getAuthorization());
	}

	private JavaDStream<Status> createTweetStreamFromSimulatedDatas() {
		return ssc.textFileStream(SIMULATION_DATA_PATH_SPARK).map(str -> TwitterObjectFactory.createStatus(str));
	}
}