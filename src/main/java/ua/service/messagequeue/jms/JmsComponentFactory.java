package ua.service.messagequeue.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.Session;

import ua.core.data.HostSettings;

public interface JmsComponentFactory {

	Connection newConnection(HostSettings settings, ConnectionFactory connectionFactory) throws JMSException;
	Connection newConnection(HostSettings settings) throws JMSException;
	ConnectionFactory newConnectionFactory(HostSettings settings);
	QueueConnection newQueueConnection(HostSettings settings, ConnectionFactory connectionFactory) throws JMSException;
	QueueConnection newQueueConnection(HostSettings settings) throws JMSException;
	Destination newQueueDestination(Session session, String queueName) throws JMSException;
	QueueSession newQueueSession(QueueConnection queueConnection) throws JMSException;
	Destination newTopicDestination(Session session, String topicName) throws JMSException;
	Session newSession(Connection connection) throws JMSException;

}