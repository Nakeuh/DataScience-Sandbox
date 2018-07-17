package usage.spark.modulary_data_processing_architecture.modules.paris_market;

import java.io.Serializable;

import myLibs.spark.modulary_data_processing_architecture.AModule;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;
import usage.spark.modulary_data_processing_architecture.modules.utils.Constantes;

public class MarketModule extends AModule implements Constantes,Serializable {

	private static final long serialVersionUID = 1L;

	public MarketModule(JavaStreamingContext ssc, double pertinence) {
		super(ssc, pertinence);
	}

	/**
	 * Calcule a score for each geographical zone : 
	 * score = nbMarketCurrentlyOpenInZOne / MaxNumberOfMarket
	 */
	@Override
	protected JavaPairDStream<String, Double> computeStream() {
		return ssc.receiverStream(new CustomMarchesStream()).window(Durations.seconds(10))
				.flatMapToPair(jsonStr ->{
					return MarketUtils.numberOfmarketOpenInEachZone(jsonStr).iterator();
					})
				.mapToPair(t -> {
					if (t._2 > maxMarket) {
						return new Tuple2<String, Double>(t._1, (double) 1);
					} else {
						return new Tuple2<String, Double>(t._1, (double) t._2 / maxMarket);
					}
				});
	}

	
}
