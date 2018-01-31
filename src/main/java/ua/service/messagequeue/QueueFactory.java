package ua.service.messagequeue;

import javax.jms.JMSException;

import ua.core.data.HostSettings;

public interface QueueFactory {

	/**
	 * Returns a factory capable of producing messages without having to be tied to a queue client or server.
	 * 
	 * @param settings
	 * @return
	 * @throws JMSException
	 */
	MessageFactory newMessageFactory(HostSettings settings) throws JMSException;
	
	/** 
	 * Create a client request response queue with the given request queue name.
	 * A randomly named queue will be created for responses. 
	 */
	QueueClient newQueueClient(HostSettings settings, String requestQueueName, long timeoutMilliseconds) throws JMSException;

	/** 
	 * Create a client request response queue with the given request and response queue names.
	 */
	QueueClient newQueueClient(HostSettings settings, String requestQueueName, String responseQueueName, long timeoutMilliseconds) throws JMSException;

	/**
	 * Create a queue consumer for a given host and queue name.
	 */
	QueueConsumer newQueueConsumer(HostSettings settings, String queueName, long timeoutMilliseconds) throws JMSException;

	/**
	 * Create a queue producer for a given host and queue name.
	 */
	QueueProducer newQueueProducer(HostSettings settings, String queueName) throws JMSException;

	/**
	 * Create a queue request response server for a given host and queue name.
	 */
	QueueServer newQueueServer(HostSettings settings, String queueName, long timeoutMilliseconds) throws JMSException;

	/**
	 * Create a queue request response server for a given host and queue name.
	 */
	QueueServerListener newQueueServerListener(HostSettings settings, String queueName) throws JMSException;

	/**
	 * Create a topic consumer for a given host and topic name.
	 */
	QueueConsumer newTopicConsumer(HostSettings settings, String topicName, long timeoutMilliseconds) throws JMSException;

	/**
	 * Create a topic producer for a given host and topic name.
	 */
	QueueProducer newTopicProducer(HostSettings settings, String topicName) throws JMSException;

}