package ua.service.messagequeue.jms;

import java.util.UUID;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.Session;

import ua.service.messagequeue.MessageFactory;
import ua.service.messagequeue.QueueClient;
import ua.service.messagequeue.QueueConsumer;
import ua.service.messagequeue.QueueFactory;
import ua.service.messagequeue.QueueProducer;
import ua.service.messagequeue.QueueServer;
import ua.service.messagequeue.QueueServerListener;
import ua.core.data.HostSettings;

public class JmsQueueFactorySimple implements QueueFactory {
	
	private JmsComponentFactory componentFactory;
	
	public JmsQueueFactorySimple (JmsComponentFactory jmsComponentFactory) {
		
		this.componentFactory = jmsComponentFactory;
	}
	
	@Override
	public MessageFactory newMessageFactory(HostSettings settings) throws JMSException {

		Connection	connection;
		Session		session;
	
		connection	= componentFactory.newConnection(settings);
		session		= componentFactory.newSession(connection);
		
		return new JmsMessageFactory (session);
	}

	@Override
	public QueueClient newQueueClient (HostSettings settings, String requestQueueName, long timeoutMilliseconds) throws JMSException {
		
		return newQueueClient (settings, requestQueueName, UUID.randomUUID().toString(), timeoutMilliseconds);
	}
	
	@Override
	public QueueClient newQueueClient (HostSettings settings, String requestQueueName, String responseQueueName, long timeoutMilliseconds) throws JMSException {
		
		QueueConnection			queueConnection;
		
		Destination				requestDestination;
		MessageProducer	 		requestProducer;
		Session					requestSession;
		
		Destination				responseDestination;
		Queue					responseQueue;
		QueueSession			responseQueueSession;
		QueueClient				queueClient;

		queueConnection			= componentFactory.newQueueConnection (settings);
		
		requestSession			= componentFactory.newSession (queueConnection);		
		requestDestination		= componentFactory.newQueueDestination (requestSession, requestQueueName);
		requestProducer			= requestSession.createProducer (requestDestination);
		
		responseQueueSession	= componentFactory.newQueueSession (queueConnection);
		responseDestination		= componentFactory.newQueueDestination (responseQueueSession, responseQueueName);
		responseQueue			= responseQueueSession.createQueue (responseQueueName);

		queueClient	= new JmsQueueClient (requestProducer, requestSession, responseDestination, responseQueue, responseQueueSession, timeoutMilliseconds);
		
		return queueClient;
	}
	
	@Override
	public QueueConsumer newQueueConsumer (HostSettings settings, String queueName, long timeoutMilliseconds) throws JMSException {
		
		Connection			connection;
		QueueConsumer		queueConsumer;
		Destination			destination;
		MessageConsumer		messageConsumer;
		Session				session;
		
		connection			= componentFactory.newConnection (settings);
		session				= componentFactory.newSession (connection);
		destination			= componentFactory.newQueueDestination (session, queueName);
		messageConsumer		= session.createConsumer (destination);
		
		queueConsumer		= new JmsQueueConsumer (messageConsumer, session, timeoutMilliseconds);
		
		return queueConsumer;
	}
	
	@Override
	public QueueProducer newQueueProducer (HostSettings settings, String queueName) throws JMSException {
		
		Connection			connection;
		Destination			destination;
		MessageProducer 	messageProducer;
		QueueProducer		queueProducer;
		Session				session;
	
		connection			= componentFactory.newConnection (settings);
		session				= componentFactory.newSession (connection);
		destination			= componentFactory.newQueueDestination (session, queueName);
		messageProducer 	= session.createProducer (destination);
		
		queueProducer		= new JmsQueueProducer (messageProducer, session);
		
		return queueProducer;
	}
	
	@Override
	public QueueServer newQueueServer (HostSettings settings, String queueName, long timeoutMilliseconds) throws JMSException {
		
		QueueConnection		queueConnection;
		Destination			requestDestination;
		MessageConsumer		requestMessageConsumer;
		Session				requestSession;

		MessageProducer	 	responseProducer;
		Session				responseSession;
		JmsQueueServer		queueServer;
	
		queueConnection			= componentFactory.newQueueConnection (settings);
		requestSession			= componentFactory.newSession (queueConnection);
		requestDestination		= componentFactory.newQueueDestination (requestSession, queueName);
		requestMessageConsumer	= requestSession.createConsumer (requestDestination);

		responseSession			= componentFactory.newSession (queueConnection);
		responseProducer		= responseSession.createProducer (null);

		queueServer				= new JmsQueueServer (requestMessageConsumer, requestSession, responseProducer, responseSession, timeoutMilliseconds);
		
		return queueServer;
	}
	
	@Override
	public QueueServerListener newQueueServerListener (HostSettings settings, String queueName) throws JMSException {
		
		QueueConnection			queueConnection;
		Destination				requestDestination;
		MessageConsumer			requestMessageConsumer;
		Session					requestSession;

		MessageProducer	 		responseProducer;
		Session					responseSession;
		QueueServerListener		queueServerListener;
	
		queueConnection			= componentFactory.newQueueConnection (settings);
		requestSession			= componentFactory.newSession (queueConnection);
		requestDestination		= componentFactory.newQueueDestination (requestSession, queueName);
		requestMessageConsumer	= requestSession.createConsumer (requestDestination);

		responseSession			= componentFactory.newSession (queueConnection);
		responseProducer		= responseSession.createProducer (null);

		queueServerListener		= new JmsQueueServerListener (requestMessageConsumer, requestSession, responseProducer, responseSession);
		
		return queueServerListener;
	}
	
	@Override
	public QueueConsumer newTopicConsumer (HostSettings settings, String topicName, long timeoutMilliseconds) throws JMSException {
		
		Connection			connection;
		QueueConsumer		queueConsumer;
		Destination			destination;
		MessageConsumer		messageConsumer;
		Session				session;
		
		connection			= componentFactory.newConnection (settings);
		session				= componentFactory.newSession (connection);
		destination			= componentFactory.newTopicDestination (session, topicName);
		messageConsumer		= session.createConsumer (destination);
		
		queueConsumer		= new JmsQueueConsumer (messageConsumer, session, timeoutMilliseconds);
		
		return queueConsumer;
	}
	
	@Override
	public QueueProducer newTopicProducer (HostSettings settings, String topicName) throws JMSException {
		
		Connection		connection;
		Destination		destination;
		MessageProducer messageProducer;
		QueueProducer	topicProducer;
		Session			session;
	
		connection		= componentFactory.newConnection (settings);
		session			= componentFactory.newSession (connection);
		destination		= componentFactory.newTopicDestination (session, topicName);
		messageProducer = session.createProducer (destination);
		
		topicProducer	= new JmsQueueProducer (messageProducer, session);
		
		return topicProducer;
	}

}
