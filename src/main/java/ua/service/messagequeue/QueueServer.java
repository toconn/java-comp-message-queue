package ua.service.messagequeue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 * A queue server that receives a request, processes it and then returns a response.
 */
public interface QueueServer extends AutoCloseable, MessageFactory {

	void close();

	TextMessage newTextMessage(int messageId, String messageText) throws JMSException;

	TextMessage newTextMessage(String messageText) throws JMSException;

	Message retrieve() throws JMSException;
	
	Message retrieve(long timeoutMilliseconds) throws JMSException;
	
	Message retrieveNoWait() throws JMSException;

	void send(Message requestMessage, Message responseMessage) throws JMSException;

}