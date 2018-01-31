package ua.service.messagequeue.jms;
 
import static ua.service.messagequeue.MessageQueueConst.*;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import ua.service.messagequeue.QueueProducer;

/**
 * 
 * Convenience facade around JMS Consumer and Queue classes.
 *
 */
public class JmsQueueProducer implements AutoCloseable, QueueProducer {

    private final MessageProducer messageProducer;
    private final Session session;

    public JmsQueueProducer (MessageProducer messageProducer, Session session) {

        this.messageProducer = messageProducer;
        this.session = session;
    }


    /* (non-Javadoc)
	 * @see ua.service.messagequeue.QueueProducer#close()
	 */
    @Override
	public void close() {

    	JmsUtils.close (session);
    }

    /* (non-Javadoc)
	 * @see ua.service.messagequeue.QueueProducer#newTextMessage(int, java.lang.String)
	 */
    @Override
	public TextMessage newTextMessage (int messageId, String messageText) throws JMSException {

        TextMessage message;
		
        message = session.createTextMessage();
        message.setIntProperty (ID_FIELD, messageId);
        message.setText(messageText);

        return message;
    }

    /* (non-Javadoc)
	 * @see ua.service.messagequeue.QueueProducer#newTextMessage(java.lang.String)
	 */
    @Override
	public TextMessage newTextMessage (String messageText) throws JMSException {

        TextMessage message;
		
        message = session.createTextMessage();
        message.setText(messageText);

        return message;
    }

    /* (non-Javadoc)
	 * @see ua.service.messagequeue.QueueProducer#send(javax.jms.Message)
	 */
    @Override
	public void send (Message message) throws JMSException {

    	messageProducer.send (message);
    }

}
