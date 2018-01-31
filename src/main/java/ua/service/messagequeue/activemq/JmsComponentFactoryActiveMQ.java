package ua.service.messagequeue.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;

import ua.service.messagequeue.jms.JmsComponentFactory;
import ua.core.data.HostSettings;
import ua.core.utils.StringUtils;

public class JmsComponentFactoryActiveMQ implements JmsComponentFactory {

	/* (non-Javadoc)
	 * @see ua.comp.jms.JmsComponentFactory#newConnection(ua.shared.data.HostSettings, org.apache.qpid.jms.JmsConnectionFactory)
	 */
	@Override
	public Connection newConnection (HostSettings settings, ConnectionFactory connectionFactory) throws JMSException {
		
		Connection connection;
		
		if (StringUtils.isNotBlank (settings.getUser())) {
			connection = connectionFactory.createConnection (settings.getUser(), settings.getPassword());
		}
		else {
			connection = connectionFactory.createConnection ();
		}
		
		return connection;
	}

	/* (non-Javadoc)
	 * @see ua.comp.jms.JmsComponentFactory#newConnection(ua.shared.data.HostSettings)
	 */
	@Override
	public Connection newConnection (HostSettings settings) throws JMSException {
		
		return newConnection (settings, newConnectionFactory (settings));
	}
	
	/* (non-Javadoc)
	 * @see ua.comp.jms.JmsComponentFactory#newJmsConnectionFactory(ua.shared.data.HostSettings)
	 */
	@Override
	public ConnectionFactory newConnectionFactory (HostSettings settings) {

		String						connectionString;
		ActiveMQConnectionFactory	connectionFactory;
		ActiveMQPrefetchPolicy		policy;

		connectionString = asActiveMQConnectionString (settings);
		connectionFactory = new ActiveMQConnectionFactory (connectionString);
		
		policy = new ActiveMQPrefetchPolicy();
		policy.setAll(0);
		connectionFactory.setPrefetchPolicy(policy);
		connectionFactory.setCloseTimeout(5000);
		
		return connectionFactory;
	}
	
	/* (non-Javadoc)
	 * @see ua.comp.jms.JmsComponentFactory#newQueueConnection(ua.shared.data.HostSettings, org.apache.qpid.jms.JmsConnectionFactory)
	 */
	@Override
	public QueueConnection newQueueConnection (HostSettings settings, ConnectionFactory connectionFactory) throws JMSException {
		
		QueueConnection queueConnection;
		ActiveMQConnectionFactory activeMQConnectionFactory;
		
		assert (connectionFactory instanceof ActiveMQConnectionFactory);
		activeMQConnectionFactory = (ActiveMQConnectionFactory) connectionFactory;
		
		if (StringUtils.isNotBlank (settings.getUser())) {
			queueConnection = activeMQConnectionFactory.createQueueConnection (settings.getUser(), settings.getPassword());
		}
		else {
			queueConnection = activeMQConnectionFactory.createQueueConnection();
		}
		
		queueConnection.start();queueConnection.start();
		
		return queueConnection;
	}
	
	/* (non-Javadoc)
	 * @see ua.comp.jms.JmsComponentFactory#newQueueConnection(ua.shared.data.HostSettings)
	 */
	@Override
	public QueueConnection newQueueConnection (HostSettings settings) throws JMSException {
		
		return newQueueConnection (settings, newConnectionFactory (settings));
	}
	
	/* (non-Javadoc)
	 * @see ua.comp.jms.JmsComponentFactory#newQueueDestination(javax.jms.Session, java.lang.String)
	 */
	@Override
	public Destination newQueueDestination (Session session, String queueName) throws JMSException {
		
		return session.createQueue (queueName);
	}
	
	/* (non-Javadoc)
	 * @see ua.comp.jms.JmsComponentFactory#newQueueSession(javax.jms.QueueConnection)
	 */
	@Override
	public QueueSession newQueueSession (QueueConnection queueConnection) throws JMSException {
		
		return queueConnection.createQueueSession (false, Session.AUTO_ACKNOWLEDGE);
	}
	
	/* (non-Javadoc)
	 * @see ua.comp.jms.JmsComponentFactory#newTopicDestination(javax.jms.Session, java.lang.String)
	 */
	@Override
	public Destination newTopicDestination (Session session, String topicName) throws JMSException {
		
		return session.createTopic (topicName);
	}
	
	/* (non-Javadoc)
	 * @see ua.comp.jms.JmsComponentFactory#newSession(javax.jms.Connection)
	 */
	@Override
	public Session newSession (Connection connection) throws JMSException {
		
		return connection.createSession (false, Session.AUTO_ACKNOWLEDGE);
	}
	
	private static String asActiveMQConnectionString (HostSettings settings) {
		
		return "tcp://" + settings.getHost() + ":" + settings.getPort();
	}

}
