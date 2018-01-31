package ua.service.messagequeue;

import javax.jms.JMSException;
import javax.jms.Message;

public interface QueueServerListener {
		
	public void close();
	
	public void onMessage(Message requestMessage);
	
	/**
	 * Register a class as a consumer of messages received by this.
	 * 
	 * Note: the server will start listening as soon as a consumer has been assigned.
	 */
	public void start (QueueListenerConsumer listenerConsumer) throws JMSException;
	
	/**
	 * Stop listening for messages.
	 */
	public void stop() throws JMSException;
}
