import org.apache.log4j.{ Level, Logger }
import org.apache.spark.streaming.{ Seconds, StreamingContext }
import org.apache.spark._

object SparkUtils {

  def initSpark(): SparkContext = {

    Logger.getLogger("org").setLevel(Level.OFF);
    Logger.getLogger("akka").setLevel(Level.OFF);
    val conf: SparkConf = new SparkConf()
      .setAppName("TestGraphX")
      .setMaster("local[*]")

    System.setProperty("hadoop.home.dir", "c:\\winutil\\") // need an hadoop home dir path when using Spark on Windows : bug ?
    return new SparkContext(conf)
  }

  def initSparkStreaming(sc: SparkContext): StreamingContext = {
    new StreamingContext(sc, Seconds(5))
  }

  def closeSpark(sc: SparkContext) {
    sc.stop()
  }

  def startStreaming(ssc: StreamingContext) {
    ssc.start()
    ssc.awaitTermination()
  }
}