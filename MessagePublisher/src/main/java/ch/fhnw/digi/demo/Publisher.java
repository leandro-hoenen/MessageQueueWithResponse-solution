package ch.fhnw.digi.demo;

import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.stereotype.Component;

@Component
public class Publisher {

	@Autowired
	private SimpleUi simpleUi;

	@Autowired
	private JmsTemplate jmsTemplate;

	@PostConstruct
	void run() {

		jmsTemplate.setMessageConverter(jacksonJmsMessageConverter());

		final String correlationID = UUID.randomUUID().toString();;

		// publish a new GreeterMessage to the channel "greetRequests"
		jmsTemplate.convertAndSend("greetRequests", new GreeterMessage("Hello!"), m -> {
			m.setStringProperty("someHeaderField", "someImportantValue");
			m.setJMSCorrelationID(correlationID);
			return m;
		});

		// just so we see something in our gui
		simpleUi.setMessage("published Message with id: " + correlationID);


		// wait for 2 seconds
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}

	}

	// used to convert our java message object into a JSON String that can be sent
	public MessageConverter jacksonJmsMessageConverter() {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		return converter;
	}
	
	
	class AckReceiver {

	    @JmsListener(destination = "ackQueue", containerFactory = "myFactory")
	    public void receiveConfirmation(String ackMessage) {
	    	simpleUi.setMessage("Ack: " + ackMessage);
	        System.out.println("Received confirmation: " + ackMessage);

	    }
	    
	    @Bean
		public JmsListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory,
				DefaultJmsListenerContainerFactoryConfigurer configurer) {
			DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
			// This provides all boot's default to this factory, including the message
			// converter
			configurer.configure(factory, connectionFactory);


			// You could still override some of Boot's default if necessary.
			return factory;
		}
	} 
}


