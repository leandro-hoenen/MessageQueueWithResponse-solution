package ch.fhnw.digi.demo;

import javax.management.ObjectName;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.BrokerView;

public class ActiveMqRunnerApplication {

	public static void main(String[] args) throws Exception {

		SimpleUi ui = new SimpleUi();
		ui.init();
		
		
		// the following four lines create and start the embedded ActiveMQ Broker
		BrokerService broker = new BrokerService();
		broker.addConnector("tcp://localhost:61616");
		broker.setPersistent(false);
		broker.start();

		
		
		// the following block prints a list of active queues to our simplistic UI
		BrokerView adm = broker.getAdminView();
		while (true) {
			
			Thread.sleep(1000);
			
			StringBuilder sb = new StringBuilder();
			ObjectName[] queues = adm.getQueues();
			if (queues != null) {
				for (ObjectName on : queues) {
					sb.append(on.getKeyProperty("destinationType") + ": " + on.getKeyProperty("destinationName"));
					sb.append("\n");
				}
			}
			
			sb.append("Total message count: "+ adm.getTotalMessageCount()+"\n");			
			ui.setMessage(sb.toString());
		}

	}

}
