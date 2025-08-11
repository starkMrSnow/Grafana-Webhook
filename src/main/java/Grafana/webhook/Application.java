package Grafana.webhook;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;

@SpringBootApplication(exclude = RabbitAutoConfiguration.class)
@EnableRabbit
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
