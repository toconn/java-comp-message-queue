package ua.service.messagequeue;

import javax.jms.Message;

public interface QueueListenerConsumer {

	public Message onMessage (Message message, MessageFactory messageFactory);	// Passing a message factory allows the consuming class to create messages of its own.
}
