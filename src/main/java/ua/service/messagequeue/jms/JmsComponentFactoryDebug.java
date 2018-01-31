package ua.service.messagequeue.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.Session;

import ua.core.data.HostSettings;
import ua.core.utils.Timer;

import static ua.core.utils.console.ConsoleUtils.*;

public class JmsComponentFactoryDebug implements JmsComponentFactory {

    private JmsComponentFactory jmsComponentFactoryMeasure;

    public JmsComponentFactoryDebug(JmsComponentFactory jmsComponentFactoryMeasure) {

    	Timer timer = new Timer();
        this.jmsComponentFactoryMeasure = jmsComponentFactoryMeasure;
        timer.stop();
        
        printTime ("JmsComponentFactoryMeasure", timer);
    }   

    @Override
    public Connection newConnection(HostSettings settings) throws JMSException {

    	Timer timer = new Timer();
    	try {
    		return this.jmsComponentFactoryMeasure.newConnection(settings);
    	}
    	finally {
	        timer.stop();
	        printTime ("newConnection", timer);
    	}
    }

    @Override
    public Connection newConnection(HostSettings settings, ConnectionFactory connectionFactory) throws JMSException {

    	Timer timer = new Timer();
    	try {
    		return this.jmsComponentFactoryMeasure.newConnection(settings, connectionFactory);
    	}
    	finally {
	        timer.stop();
	        printTime ("newConnection", timer);
    	}
    }

    @Override
    public ConnectionFactory newConnectionFactory(HostSettings settings) {

    	Timer timer = new Timer();
    	try {
    		return this.jmsComponentFactoryMeasure.newConnectionFactory(settings);
    	}
    	finally {
	        timer.stop();
	        printTime ("newJmsConnectionFactory", timer);
    	}
    }

    @Override
    public QueueConnection newQueueConnection(HostSettings settings) throws JMSException {

    	Timer timer = new Timer();
    	try {
    		return this.jmsComponentFactoryMeasure.newQueueConnection(settings);
    	}
    	finally {
	        timer.stop();
	        printTime ("newQueueConnection", timer);
    	}
    }

    @Override
    public QueueConnection newQueueConnection(HostSettings settings, ConnectionFactory connectionFactory) throws JMSException {

    	Timer timer = new Timer();
    	try {
    		return this.jmsComponentFactoryMeasure.newQueueConnection(settings, connectionFactory);
    	}
    	finally {
	        timer.stop();
	        printTime ("newQueueConnection", timer);
    	}
    }

    @Override
    public Destination newQueueDestination(Session session, String queueName) throws JMSException {

		Timer timer = new Timer();
    	try {
    		return this.jmsComponentFactoryMeasure.newQueueDestination(session, queueName);
    	}
    	finally {
	        timer.stop();
	        printTime ("newQueueDestination", timer);
    	}
    }

    @Override
    public QueueSession newQueueSession(QueueConnection queueConnection) throws JMSException {

    	Timer timer = new Timer();
    	try {
    		return this.jmsComponentFactoryMeasure.newQueueSession(queueConnection);
    	}
    	finally {
	        timer.stop();
	        printTime ("newQueueSession", timer);
    	}
    }

    @Override
    public Session newSession(Connection connection) throws JMSException {

    	Timer timer = new Timer();
    	try {
    		return this.jmsComponentFactoryMeasure.newSession(connection);
    	}
    	finally {
	        timer.stop();
	        printTime ("newSession", timer);
    	}
    }

    @Override
    public Destination newTopicDestination(Session session, String topicName) throws JMSException {

    	Timer timer = new Timer();
    	try {
    		return this.jmsComponentFactoryMeasure.newTopicDestination(session, topicName);
    	}
    	finally {
	        timer.stop();
	        printTime ("newTopicDestination", timer);
    	}
    }
    
    private void printTime (String methodName, Timer timer) {
    	
    	print ("JmsComponentFactory." + methodName + "()", timer.getElapsedTimeMilliSec() + "ms");
    }
}
