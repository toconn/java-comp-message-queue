package ua.service.messagequeue;

import javax.jms.JMSException;
import javax.jms.Message;

public interface QueueConsumer extends AutoCloseable {

	void close();

	Message retrieve() throws JMSException;
	Message retrieve(long timeoutMilliseconds) throws JMSException;
	Message retrieveNoWait() throws JMSException;

}