package ua.service.messagequeue;

import javax.jms.JMSException;
import javax.jms.Message;

public interface QueueServerListener {
	
	/**
	 * Register a class as a consumer of messages received by this.
	 * 
	 * Note: the server will start listening as soon as a consumer has been assigned.
	 */
	public void assignConsumer(QueueListenerConsumer listenerConsumer) throws JMSException;
	
	public void close();
	
	public void onMessage(Message requestMessage);
	
	/**
	 * Start listening for messages.
	 */
	public void start() throws JMSException;
	
	/**
	 * Stop listening for messages.
	 */
	public void stop() throws JMSException;
	
	/**
	 * Unregister a class from being a consumer of messages.
	 * 
	 * Note: the server will start listening as soon as a consumer has been assigned.
	 */
	public void unassignConsumer() throws JMSException;
}
