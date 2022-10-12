package ch.fhnw.digi.demo;

import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

	@Autowired
	private SimpleUi simpleui;
	
	@Autowired
	JmsTemplate jmsTemplate;
	
	@Autowired
	AckSender ackSender;


	@JmsListener(destination = "greetRequests", containerFactory = "myFactory")
	public void receiveMessage(GreeterMessage c, @Header(JmsHeaders.CORRELATION_ID) String correlationId) {

		simpleui.setMessage("received greetingRequest " + c.getName() + " with id: " + correlationId);

		try {
			Thread.sleep(1000);
			ackSender.sendMessage(new String("Acknowledgement from Receiver for id:" + correlationId));
		} catch (InterruptedException e) {
		}

	}
	
	
	@Bean
	public JmsListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory,
			DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		// This provides all boot's default to this factory, including the message
		// converter
		configurer.configure(factory, connectionFactory);

		simpleui.setMessage("connection factory created");

		// You could still override some of Boot's default if necessary.
		return factory;
	}
	

	class AckSender {

		public void sendMessage(String message) {
			// publish ack to the channel "ackQueue"
			jmsTemplate.convertAndSend("ackQueue", message);
		}

		@Bean // Serialize message content to json/from using TextMessage
		public MessageConverter jacksonJmsMessageConverter() {
			MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
			converter.setTargetType(MessageType.TEXT);
			converter.setTypeIdPropertyName("_type");
			return converter;
		}
	
	}

}