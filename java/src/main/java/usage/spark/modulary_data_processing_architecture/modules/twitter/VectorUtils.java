package usage.spark.modulary_data_processing_architecture.modules.twitter;

import java.util.List;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.feature.IDF;
import org.apache.spark.mllib.feature.IDFModel;
import org.apache.spark.mllib.linalg.Vector;

public class VectorUtils {

    /**
     * Transform string list to unlabeled vectors
     * @param rdd
     * @param dimension
     * @return
     */
	public static JavaRDD<Vector> transformListStringToVector(JavaRDD<List<String>> rdd, int dimension) {
		HashingTF tf = new HashingTF(dimension);

		JavaRDD<Vector> rdd_tf = tf.transform(rdd);
		rdd_tf.cache();
		IDFModel idf = null;

		try {
			idf = new IDF().fit(rdd_tf);
		} catch (Exception e) {

		}

		if (idf != null)
			return idf.transform(rdd_tf);
		else
			return rdd_tf;
	}

    /**
     * Transform string list to labeled vectors
     * @param rdd
     * @param dimension
     * @param <LabelType>
     * @return
     */
	public static <LabelType> JavaRDD<VectorizedTweet<LabelType>> transformListStringToLabeledVector(
			JavaPairRDD<LabelType, List<String>> rdd, int dimension) {
		HashingTF tf = new HashingTF(dimension);

		JavaRDD<VectorizedTweet<LabelType>> labeled_tf = rdd
				.map(pair -> new VectorizedTweet<LabelType>(pair._1, tf.transform(pair._2)));
		JavaRDD<Vector> tf_vector_rdd = labeled_tf.map(label -> label.features());

		IDFModel idfModel = null;

		try {
			idfModel = new IDF().fit(tf_vector_rdd);
		} catch (Exception e) {
			System.out.println("No document seen for training model. Will retry at next batch");
		}

		if (idfModel != null) {
			JavaRDD<Vector> idf_vector_rdd = idfModel.transform(tf_vector_rdd);

			JavaRDD<VectorizedTweet<LabelType>> idfTransformed = idf_vector_rdd.zip(labeled_tf).map(t -> {
				return new VectorizedTweet<LabelType>(t._2.label(), t._1);
			});

			return idfTransformed;
		} else {
			return labeled_tf;
		}

	}
}
