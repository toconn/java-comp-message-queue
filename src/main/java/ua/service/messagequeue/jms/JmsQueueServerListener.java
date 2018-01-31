package ua.service.messagequeue.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import ua.service.messagequeue.MessageFactory;
import ua.service.messagequeue.QueueListenerConsumer;
import ua.service.messagequeue.QueueServerListener;

import static ua.core.utils.console.ConsoleUtils.*;

public class JmsQueueServerListener implements MessageListener, QueueServerListener {
	
	private QueueListenerConsumer listenerConsumer = null;
	private MessageConsumer requestConsumer;
	private Session requestSession;
	private MessageProducer responseProducer;
	private Session responseSession;
	
	public JmsQueueServerListener (MessageConsumer requestConsumer, Session requestSession, MessageProducer responseProducer, Session responseSession) {
		
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
		
		responseMessage = this.listenerConsumer.onMessage (requestMessage, (MessageFactory) this);
		
		// Send:
		
		try {
			responseMessage.setJMSCorrelationID (requestMessage.getJMSCorrelationID());
			responseProducer.send (requestMessage.getJMSReplyTo(), responseMessage);
		}
		catch (JMSException e) {
			
			print ("Error sending response message.", e);
		}
	}
	
	public void assignConsumer (QueueListenerConsumer listenerConsumer) throws JMSException {
		
		this.listenerConsumer = listenerConsumer;
		start();
	}
	
	public void start() throws JMSException {

		this.requestConsumer.setMessageListener(this);
	}
	
	public void stop() throws JMSException {
	
		this.requestConsumer.setMessageListener(null);
	}
	
	public void unassignConsumer() throws JMSException {
		
		stop();
		this.listenerConsumer = null;
	}

}
