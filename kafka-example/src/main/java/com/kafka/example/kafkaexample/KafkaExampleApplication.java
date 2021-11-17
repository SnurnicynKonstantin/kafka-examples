package com.kafka.example.kafkaexample;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.Properties;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

@SpringBootApplication
public class KafkaExampleApplication {

	private static final String TOPIC_NAME = "test";

	public static void main(String[] args) {
		SpringApplication.run(KafkaExampleApplication.class, args);

		String topicName = TOPIC_NAME;

		Properties props = new Properties();

		props.put("bootstrap.servers", "127.0.0.1:29092");

		//Set acknowledgements for producer requests. ???
		props.put("acks", "all");

				//If the request fails, the producer can automatically retry,
				props.put("retries", 0);

		//Specify buffer size in config
		props.put("batch.size", 16384);

		//Reduce the no of requests less than 0
		props.put("linger.ms", 1);

		//The buffer.memory controls the total amount of memory available to the producer for buffering.
		props.put("buffer.memory", 33554432);

		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		Producer<String, String> producer = new KafkaProducer<>(props);

		for(int i = 0; i < 10; i++)
			producer.send(new ProducerRecord<>(topicName,
					Integer.toString(i), Integer.toString(i)));
		System.out.println("Message sent successfully");
		producer.close();

//		----------------------------------------------------------------------------

		Properties consumeProps = new Properties();

		consumeProps.put("bootstrap.servers", "127.0.0.1:29092");
		consumeProps.put("group.id", "test");
		consumeProps.put("enable.auto.commit", "true");
		consumeProps.put("auto.commit.interval.ms", "1000");
		consumeProps.put("session.timeout.ms", "30000");
		consumeProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		consumeProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumeProps);

		//Kafka Consumer subscribes list of topics here.
		consumer.subscribe(Arrays.asList(topicName));

		//print the topic name
		System.out.println("Subscribed to topic " + topicName);
		int i = 0;

		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(100);
			for (ConsumerRecord<String, String> record : records)
				// print the offset,key and value for the consumer records.
				System.out.printf("offset = %d, key = %s, value = %s\n",
						record.offset(), record.key(), record.value());
		}
	}

}
