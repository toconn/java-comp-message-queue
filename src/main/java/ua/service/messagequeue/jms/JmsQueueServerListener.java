package ua.service.messagequeue.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import ua.core.exceptions.Break;
import ua.service.messagequeue.MessageFactory;
import ua.service.messagequeue.QueueListenerConsumer;
import ua.service.messagequeue.QueueServerListener;

import static ua.core.utils.console.ConsoleUtils.*;

public class JmsQueueServerListener implements MessageListener, QueueServerListener {
	
	private QueueListenerConsumer listenerConsumer = null;
	private MessageFactory messageFactory;
	private MessageConsumer requestConsumer;
	private Session requestSession;
	private MessageProducer responseProducer;
	private Session responseSession;
	
	public JmsQueueServerListener (MessageFactory messageFactory, MessageConsumer requestConsumer, Session requestSession, MessageProducer responseProducer, Session responseSession) {
		
		this.messageFactory = messageFactory;
		this.requestConsumer = requestConsumer;
		this.requestSession = requestSession;
		this.responseProducer = responseProducer;
		this.responseSession = responseSession;
	}
	
	@Override
	public void close() {
		
		try {
			stop();
		}
		catch (JMSException e) {
			print ("Exception while stopping queue listener.", e);
			// Ignoring.
		}
		
		JmsUtils.close (requestSession);
		JmsUtils.close (responseSession);
	}

	@Override
	public void onMessage(Message requestMessage) {
		
		Message responseMessage;
		
		// Process
		
		try {
			responseMessage = this.listenerConsumer.onMessage (requestMessage, messageFactory);
		}
		catch (Break e) {
			
			close();
			return;
		}

		// Send:
		
		try {
			responseMessage.setJMSCorrelationID (requestMessage.getJMSCorrelationID());
			responseProducer.send (requestMessage.getJMSReplyTo(), responseMessage);
		}
		catch (JMSException e) {
			
			print ("Error sending response message.", e);
		}
	}
	
	public void start (QueueListenerConsumer listenerConsumer) throws JMSException {
		
		this.listenerConsumer = listenerConsumer;
		this.requestConsumer.setMessageListener(this);
	}
	
	public void stop() throws JMSException {
	
		this.requestConsumer.setMessageListener(null);
		this.listenerConsumer = null;
	}

}
