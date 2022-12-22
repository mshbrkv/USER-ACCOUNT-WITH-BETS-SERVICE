package config

import db.buckets.bets.BetBucket
import entity.Selection
import io.circe
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord, KafkaConsumer}

import java.time.Duration
import java.util.{Collections, Properties}

class ConsumerConfiguration(bucket: BetBucket) {

  val consumerProps: Properties = new Properties()
  consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
  //  consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
  consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer")
  //  consumerProps.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG,true)
  consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "selection")
  consumerProps.put("schema.registry.url", "http://localhost:8081")
  val topic = "t.trading.selection"


  val consumer: KafkaConsumer[String, Object] = new KafkaConsumer[String, Object](consumerProps)
  consumer.subscribe(Collections.singletonList(topic))

val thread: Thread =new Thread{
  override def run(){
    while (true) {
      val records = consumer.poll(Duration.ofMillis(1000))
      val it = records.iterator()
      while (it.hasNext) {
        val msg = it.next()

        val selection = jsonParse(msg)
        bucket.updateBetToDB(selection)
        println(s"key: ${msg.key()}, value:${msg.value()}")
      }
    }
  }
}
  thread.start()
  Thread.sleep(50)



  def jsonParse(msg: ConsumerRecord[String, Object]): Selection = {
    val jsonString = msg.value().toString
    val parseRes = circe.parser.parse(jsonString)
    val json = parseRes.getOrElse(null)
    val jsonAsSelection = json.as[Selection]
    jsonAsSelection.toOption.orNull
  }


}
