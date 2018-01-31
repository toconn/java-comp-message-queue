package ua.service.messagequeue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import ua.core.exceptions.OperationTimedOut;

/**
 * A queue client that sends a request and then receives a response.
 */
public interface QueueClient extends AutoCloseable {

	void close();

	TextMessage newTextMessage(int messageId, String messageText) throws JMSException;

	TextMessage newTextMessage(String messageText) throws JMSException;

	Message send(Message requestMessage) throws JMSException, OperationTimedOut;

}