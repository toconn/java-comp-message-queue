package ua.service.messagequeue.jms;

import static ua.service.messagequeue.MessageQueueConst.ID_FIELD;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import ua.service.messagequeue.QueueServer;

public class JmsQueueServer implements QueueServer {

    private final MessageConsumer requestConsumer;
    private final Session requestSession;
    private final MessageProducer responseProducer;
    private final Session responseSession;
    private final long timeoutMilliseconds;

    public JmsQueueServer (MessageConsumer requestConsumer, Session requestSession, MessageProducer responseProducer, Session responseSession, long timeoutMilliseconds) {

        this.requestConsumer = requestConsumer;
        this.requestSession = requestSession;
        this.responseProducer = responseProducer;
        this.responseSession = responseSession;
        this.timeoutMilliseconds = timeoutMilliseconds;
    }

    @Override
	public void close() {

    	JmsUtils.close (requestSession);
    	JmsUtils.close (responseSession);
    }

    @Override
	public TextMessage newTextMessage (int messageId, String messageText) throws JMSException {

        TextMessage message;
		
        message = responseSession.createTextMessage();
        message.setIntProperty (ID_FIELD, messageId);
        message.setText(messageText);

        return message;
    }

    @Override
	public TextMessage newTextMessage (String messageText) throws JMSException {

        TextMessage message;
		
        message = responseSession.createTextMessage();
        message.setText(messageText);

        return message;
    }

    @Override
	public Message retrieve() throws JMSException {

    	return requestConsumer.receive(this.timeoutMilliseconds);
    }

    @Override
	public Message retrieve (long timeoutMilliseconds) throws JMSException {

    	return requestConsumer.receive(timeoutMilliseconds);
    }

    @Override
	public Message retrieveNoWait() throws JMSException {

    	return requestConsumer.receiveNoWait();
    }

    @Override
	public void send (Message requestMessage, Message responseMessage) throws JMSException {

    	responseMessage.setJMSCorrelationID (requestMessage.getJMSCorrelationID());
		responseProducer.send (requestMessage.getJMSReplyTo(), responseMessage);
    }

}
