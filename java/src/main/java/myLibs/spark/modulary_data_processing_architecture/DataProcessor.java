package myLibs.spark.modulary_data_processing_architecture;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairDStream;

import scala.Tuple2;

public class DataProcessor {

	List<AModule> modules = new ArrayList<AModule>();

	public void initialize(List<AModule> modules) {
		this.modules = modules;
	}

	public void addModule(AModule module) {
		modules.add(module);
	}

	public List<AModule> getModules() {
		return modules;
	}

	/**
	 * Compute datas for each module and union all input stream in one
	 * @param windowDuration
	 * @param windowSlide
	 * @return
	 */
	private JavaPairDStream<String, Double> computeAndUnion(int windowDuration, int windowSlide) {
		JavaPairDStream<String, Double> stream=null;
		if (!modules.isEmpty()) {
			stream = modules.get(0).compute().window(Durations.seconds(windowDuration), Durations.seconds(windowSlide));
			for (int i = 1; i < modules.size(); i++) {
				stream.union(modules.get(i).compute().window(Durations.seconds(windowDuration),
						Durations.seconds(windowSlide)));
			}
		}
		return stream;
	}

	/**
	 * Union computed datas for each modules and calculate the mean score for each label
	 */
	public JavaPairDStream<String, Double>  getMeanedResultsByLabel(int windowDuration, int windowSlide) {
		JavaPairDStream<String, Double> stream = computeAndUnion(windowDuration, windowSlide);
		
		// Take result from each modules, and compute a percentage between 0 and 1 for each label
		// ==> It's just a mean : Add every result for a same label and then divide by number of result for this label.
		if(stream !=null) {
			stream = stream.mapToPair(t->new Tuple2<String,Tuple2<Double,Integer>>(t._1,new Tuple2<Double,Integer>(t._2,1)))
			.reduceByKey((a,b)-> new Tuple2<Double,Integer>(a._1+b._1,a._2+b._2))
			.mapToPair(t->new Tuple2<String,Double>(t._1,t._2._1/t._2._2));
		}
		
		return stream;
	}

}
