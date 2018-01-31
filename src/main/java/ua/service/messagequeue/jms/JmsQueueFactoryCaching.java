package ua.service.messagequeue.jms;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.jms.ConnectionFactory;
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
// import ua.service.messagequeue.activemq.JmsComponentFactoryActiveMQ;
// import ua.service.messagequeue.qpid.JmsComponentFactoryQpid;
import ua.core.data.HostSettings;

public class JmsQueueFactoryCaching implements QueueFactory {
	
	// private JmsComponentFactory componentFactory = new JmsComponentFactoryMeasure (new JmsComponentFactoryActiveMQ());
	private JmsComponentFactory componentFactory;
	private Map <HostSettings, ConnectionFactory> connectionFactoryMap = new HashMap<>();
	private Map <HostSettings, QueueConnection> queueConnectionMap = new HashMap<>();
	
	public JmsQueueFactoryCaching (JmsComponentFactory jmsComponentFactory) {
		
		this.componentFactory = jmsComponentFactory;
	}
	
	@Override
	public MessageFactory newMessageFactory(HostSettings settings) throws JMSException {

		QueueConnection		queueConnection;
		Session				session;
	
		queueConnection		= getOrCreateQueueConnect (settings);
		session				= componentFactory.newSession(queueConnection);
		
		return new JmsMessageFactory (session);
	}
	
	/** 
	 * Create a client request response queue with the given request queue name.
	 * A randomly named queue will be created for responses. 
	 */
	@Override
	public QueueClient newQueueClient (HostSettings settings, String requestQueueName, long timeoutMilliseconds) throws JMSException {

		return newQueueClient (settings, requestQueueName, UUID.randomUUID().toString(), timeoutMilliseconds);
	}
	
	/** 
	 * Create a client request response queue with the given request and response queue names.
	 */
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
	
		queueConnection			= getOrCreateQueueConnect (settings);
		
		requestSession			= componentFactory.newSession (queueConnection);		
		requestDestination		= componentFactory.newQueueDestination (requestSession, requestQueueName);
		requestProducer			= requestSession.createProducer (requestDestination);
		
		responseQueueSession	= componentFactory.newQueueSession (queueConnection);
		responseDestination		= componentFactory.newQueueDestination (responseQueueSession, responseQueueName);
		responseQueue			= responseQueueSession.createQueue (responseQueueName);

		queueClient	= new JmsQueueClient (requestProducer, requestSession, responseDestination, responseQueue, responseQueueSession, timeoutMilliseconds);
		
		return queueClient;
	}
	
	/* (non-Javadoc)
	 * @see ua.comp.jms.JmsQueueFactory#newQueueConsumer(ua.shared.data.HostSettings, java.lang.String)
	 */
	@Override
	public QueueConsumer newQueueConsumer (HostSettings settings, String queueName, long timeoutMilliseconds) throws JMSException {
		
		QueueConnection		queueConnection;
		QueueConsumer		queueConsumer;
		Destination			destination;
		MessageConsumer		messageConsumer;
		Session				session;
		
		queueConnection		= getOrCreateQueueConnect (settings);
		session				= componentFactory.newSession (queueConnection);
		destination			= componentFactory.newQueueDestination (session, queueName);
		messageConsumer		= session.createConsumer (destination);
		
		queueConsumer		= new JmsQueueConsumer (messageConsumer, session, timeoutMilliseconds);
		
		return queueConsumer;
	}
	
	/* (non-Javadoc)
	 * @see ua.comp.jms.JmsQueueFactory#newQueueProducer(ua.shared.data.HostSettings, java.lang.String)
	 */
	@Override
	public QueueProducer newQueueProducer (HostSettings settings, String queueName) throws JMSException {
		
		QueueConnection		queueConnection;
		Destination			destination;
		MessageProducer		messageProducer;
		QueueProducer		queueProducer;
		Session				session;
	
		queueConnection		= getOrCreateQueueConnect (settings);
		
		session				= componentFactory.newSession (queueConnection);
		destination			= componentFactory.newQueueDestination (session, queueName);
		messageProducer		= session.createProducer (destination);
		
		queueProducer		= new JmsQueueProducer (messageProducer, session);
		
		return queueProducer;
	}
	
	/* (non-Javadoc)
	 * @see ua.comp.jms.JmsQueueFactory#newQueueServer(ua.shared.data.HostSettings, java.lang.String)
	 */
	@Override
	public QueueServer newQueueServer (HostSettings settings, String queueName, long timeoutMilliseconds) throws JMSException {
		
		QueueConnection			queueConnection;
		
		Destination				requestDestination;
		MessageConsumer			requestMessageConsumer;
		Session					requestSession;

		MessageProducer	 		responseProducer;
		Session					responseSession;

		QueueServer				queueServer;
	
		queueConnection			= getOrCreateQueueConnect (settings);
		
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
		
		MessageFactory			messageFactory;
		QueueConnection			queueConnection;
		Destination				requestDestination;
		MessageConsumer			requestMessageConsumer;
		Session					requestSession;

		MessageProducer	 		responseProducer;
		Session					responseSession;
		QueueServerListener		queueServerListener;
	
		queueConnection			= getOrCreateQueueConnect (settings);
		
		requestSession			= componentFactory.newSession (queueConnection);
		requestDestination		= componentFactory.newQueueDestination (requestSession, queueName);
		requestMessageConsumer	= requestSession.createConsumer (requestDestination);

		responseSession			= componentFactory.newSession (queueConnection);
		responseProducer		= responseSession.createProducer (null);
		
		messageFactory			= new JmsMessageFactory (responseSession);

		queueServerListener		= new JmsQueueServerListener (messageFactory, requestMessageConsumer, requestSession, responseProducer, responseSession);
		
		return queueServerListener;
	}
	
	/* (non-Javadoc)
	 * @see ua.comp.jms.JmsQueueFactory#newTopicConsumer(ua.shared.data.HostSettings, java.lang.String)
	 */
	@Override
	public QueueConsumer newTopicConsumer (HostSettings settings, String topicName, long timeoutMilliseconds) throws JMSException {
		
		QueueConnection		queueConnection;
		QueueConsumer		queueConsumer;
		Destination			destination;
		MessageConsumer		messageConsumer;
		Session				session;
		
		queueConnection		= getOrCreateQueueConnect (settings);

		session				= componentFactory.newQueueSession (queueConnection);
		destination			= componentFactory.newTopicDestination (session, topicName);
		messageConsumer		= session.createConsumer (destination);
		queueConsumer		= new JmsQueueConsumer (messageConsumer, session, timeoutMilliseconds);
		
		return queueConsumer;
	}
	
	/* (non-Javadoc)
	 * @see ua.comp.jms.JmsQueueFactory#newTopicProducer(ua.shared.data.HostSettings, java.lang.String)
	 */
	@Override
	public QueueProducer newTopicProducer (HostSettings settings, String topicName) throws JMSException {
		
		QueueConnection		queueConnection;
		Destination			destination;
		MessageProducer 	messageProducer;
		QueueProducer		topicProducer;
		Session				session;
	
		queueConnection		= getOrCreateQueueConnect (settings);

		session			= componentFactory.newSession (queueConnection);
		destination		= componentFactory.newTopicDestination (session, topicName);
		messageProducer = session.createProducer (destination);
		
		topicProducer	= new JmsQueueProducer (messageProducer, session);
		
		return topicProducer;
	}
	
	private ConnectionFactory getOrCreateJmsConnectionFactory (HostSettings settings) throws JMSException {
		
		ConnectionFactory connectionFactory;
		
		if (this.connectionFactoryMap.containsKey(settings)) {
			
			connectionFactory = this.connectionFactoryMap.get(settings);
		}
		else {
			
			synchronized (this.connectionFactoryMap) {

				if (this.connectionFactoryMap.containsKey(settings)) {
					
					connectionFactory = this.connectionFactoryMap.get(settings);
				}
				else {
					connectionFactory = componentFactory.newConnectionFactory (settings);
					this.connectionFactoryMap.put (settings, connectionFactory);
				}
			}
		}
		
		return connectionFactory;
	}
	
	private QueueConnection getOrCreateQueueConnect (HostSettings settings) throws JMSException {
		
		ConnectionFactory connectionFactory;
		QueueConnection queueConnection;
		
		connectionFactory = getOrCreateJmsConnectionFactory (settings);
		
		if (this.queueConnectionMap.containsKey(settings)) {
			
			queueConnection = this.queueConnectionMap.get(settings);
		}
		else {
			
			synchronized (this.queueConnectionMap) {

				if (this.queueConnectionMap.containsKey(settings)) {
					
					queueConnection = this.queueConnectionMap.get(settings);
				}
				else {
					queueConnection = componentFactory.newQueueConnection (settings, connectionFactory);
					this.queueConnectionMap.put (settings, queueConnection);
				}
			}

		}
		
		return queueConnection;
	}

}
