package ua.service.messagequeue.qpid;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.apache.qpid.jms.JmsConnectionFactory;

import ua.service.messagequeue.jms.JmsComponentFactory;
import ua.core.data.HostSettings;
import ua.core.utils.StringUtils;

public class JmsComponentFactoryQpid implements JmsComponentFactory {

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

		String					connectionString;
		JmsConnectionFactory	jmsConnectionFactory;
		
		connectionString = asAmpqConnectionString (settings);
		jmsConnectionFactory = new JmsConnectionFactory (connectionString);
		
		return jmsConnectionFactory;
	}
	
	/* (non-Javadoc)
	 * @see ua.comp.jms.JmsComponentFactory#newQueueConnection(ua.shared.data.HostSettings, org.apache.qpid.jms.JmsConnectionFactory)
	 */
	@Override
	public QueueConnection newQueueConnection (HostSettings settings, ConnectionFactory connectionFactory) throws JMSException {
		
		QueueConnection queueConnection;
		JmsConnectionFactory jmsConnectionFactory;
		
		assert (connectionFactory instanceof JmsConnectionFactory);
		jmsConnectionFactory = (JmsConnectionFactory) connectionFactory;
		
		if (StringUtils.isNotBlank (settings.getUser())) {
			queueConnection = jmsConnectionFactory.createQueueConnection (settings.getUser(), settings.getPassword());
		}
		else {
			queueConnection = jmsConnectionFactory.createQueueConnection();
		}
		
		queueConnection.start();
		
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
	
	private static String asAmpqConnectionString (HostSettings settings) {
		
		return "amqp://" + settings.getHost() + ":" + settings.getPort();
	}

}
