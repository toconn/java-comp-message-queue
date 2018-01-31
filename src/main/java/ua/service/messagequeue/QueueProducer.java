package ua.service.messagequeue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

public interface QueueProducer extends AutoCloseable, MessageFactory {

	void close();

	TextMessage newTextMessage(int messageId, String messageText) throws JMSException;

	TextMessage newTextMessage(String messageText) throws JMSException;

	void send(Message message) throws JMSException;

}