package ua.service.messagequeue;

import javax.jms.Message;

import ua.core.exceptions.Break;

public interface QueueListenerConsumer {

	public Message onMessage (Message message, MessageFactory messageFactory) throws Break ;	// Passing a message factory allows the consuming class to create messages of its own.
}
