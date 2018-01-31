package ua.service.messagequeue;

import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * Allows classes to create queue messages without knowing anything about the queues themselves.
 *
 */
public interface MessageFactory {
	
	TextMessage newTextMessage(int messageId, String messageText) throws JMSException;
	TextMessage newTextMessage(String messageText) throws JMSException;
}
