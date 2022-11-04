package config

import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}

import java.time.Duration
import java.util.{Collections, Properties}

class ConsumerConfiguration {

  val consumerProps: Properties = new Properties()
  consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
  consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer")


  val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](consumerProps)
  consumer.subscribe(Collections.singletonList("t.trading.selection"))

  while (true) {
    val records = consumer.poll(Duration.ofMillis(1000))
   val it=records.iterator()
    while (it.hasNext){
      val msg=it.next()
      println(s"key: ${msg.key()}, value:${msg.value()}")
    }
  }

}
