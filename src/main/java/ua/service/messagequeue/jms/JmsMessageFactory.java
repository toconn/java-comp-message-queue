package ua.service.messagequeue.jms;

import static ua.service.messagequeue.MessageQueueConst.ID_FIELD;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import ua.service.messagequeue.MessageFactory;

public class JmsMessageFactory implements MessageFactory {
	
	private Session session;
	
	public JmsMessageFactory (Session session) {
		
		this.session = session;
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
	
}
