package spark

import org.apache.kafka.clients.producer.{ ProducerConfig, KafkaProducer, ProducerRecord }
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{ Seconds, StreamingContext }
import org.apache.spark.streaming.dstream._
import java.util.HashMap
import org.apache.commons.pool._

object KafkaSpark {

  /**
    * Initialise a Kafka Producer
    */
  def initProducer(kafkaBroker: String): KafkaProducer[String, String] = {
    val kafkaProducerProps = new HashMap[String, Object]()
    kafkaProducerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker)
    kafkaProducerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    kafkaProducerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    new KafkaProducer[String, String](kafkaProducerProps)
  }

  /**
    *  Get Stream data from kafka topic
    */
  def getKafkaStream(ssc: StreamingContext, zookeeperHost: String, consumerGroup: String, topic: String): DStream[String] = {
    KafkaUtils.createStream(ssc, zookeeperHost, consumerGroup, Map(topic -> 1), StorageLevel.MEMORY_AND_DISK).map { _._2 }
  }

  /**
    *  Get Stream data from kafka topic with tile window
    */
  def getKafkaStreamWithWindow(ssc: StreamingContext, zookeeperHost: String, consumerGroup: String, topic: String, windowLenght: Int, windowSlide: Int): DStream[String] = {
    return KafkaUtils.createStream(ssc, zookeeperHost, consumerGroup, Map(topic -> 1), StorageLevel.MEMORY_AND_DISK).map { _._2 } // Get data stream from Kafka
      .window(Seconds(windowLenght), Seconds(windowSlide)) // Set the sliding window
  }

  /**
    *  Get Stream data from directory
    */
  def createStreamFromDirectory(ssc: StreamingContext, directory: String): DStream[String] = {
    return ssc.textFileStream(directory)
  }

  /**
    * Send Stream data to kafka topic
    */
  def sendStreamToKafka(stream: DStream[String], kafkaBroker: String, topic: String) {
    stream.foreachRDD { RDDJson =>
      RDDJson.foreachPartition { iter =>
        val producer = initProducer(kafkaBroker)
        iter.foreach { value =>
          producer.send(new ProducerRecord(topic, value))
        }
        producer.close()
      }
    }

  }

}
