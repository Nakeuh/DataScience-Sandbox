package usage.spark.modulary_data_processing_architecture.modules.twitter;

import org.apache.spark.mllib.linalg.Vector;

/**
 * Object to store a tweet in mllib vector format
 *
 * @param <LabelType>
 */
public class VectorizedTweet<LabelType> {
	
	private LabelType label;
	private Vector features;
	
	public VectorizedTweet(LabelType label, Vector features) {
		super();
		this.label = label;
		this.features = features;
	}
	
	public LabelType label() {
		return label;
	}
	public void setLabel(LabelType label) {
		this.label = label;
	}
	public Vector features() {
		return features;
	}
	public void setFeatures(Vector features) {
		this.features = features;
	}
	
	

}
