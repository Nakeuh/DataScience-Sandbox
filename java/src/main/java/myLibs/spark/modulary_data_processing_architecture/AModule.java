package myLibs.spark.modulary_data_processing_architecture;

import java.io.Serializable;

import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

/**
 * A Module is a component that receive input datas, arrange them by label, and
 * return a score for each label.
 * 
 * @author victor
 *
 */
public abstract class AModule implements Serializable {

	private static final long serialVersionUID = 1L;
	protected JavaStreamingContext ssc;
	protected double pertinence;

	public AModule(JavaStreamingContext ssc, double pertinence) {
		this.ssc = ssc;
		this.pertinence = pertinence;
	}

	/**
	 * A Module return a Stream with a score between 0 and 1 for each label
	 * 
	 * @return
	 */
	protected abstract JavaPairDStream<String, Double> computeStream();

	/**
	 * Compute the stream of datas and multiply it by pertinence factor.
	 * 
	 * @return
	 */
	public JavaPairDStream<String, Double> compute() {
		double pert = pertinence;
		return computeStream().mapValues(score -> score * pert);
	}

	public double getPertinence() {
		return pertinence;
	}

	public void setPertinence(double pertinence) {
		this.pertinence = pertinence;
	}
}
