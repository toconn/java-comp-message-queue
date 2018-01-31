package ua.service.messagequeue.jms;

import static ua.service.messagequeue.MessageQueueConst.ID_FIELD;

import java.util.UUID;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import ua.service.messagequeue.QueueClient;
import ua.core.exceptions.OperationTimedOut;

public class JmsQueueClient implements QueueClient {
	
    private final MessageProducer requestProducer;
    private final Session requestSession;
    private final Destination responseDestination;
    private final Queue responseQueue;
    private final QueueSession responseQueueSession;
    private final long timeoutMilliseconds;
    
    public JmsQueueClient (MessageProducer requestProducer, Session requestSession, Destination responseDestination, Queue responseQueue, QueueSession responseQueueSession, long timeoutMilliseconds) {

        this.requestProducer = requestProducer;
        this.requestSession = requestSession;
        
        this.responseDestination = responseDestination;
        this.responseQueue = responseQueue;
        this.responseQueueSession = responseQueueSession;
        this.timeoutMilliseconds = timeoutMilliseconds;
    }


    /* (non-Javadoc)
	 * @see ua.service.messagequeue.clientserver.QueueClient#close()
	 */
    @Override
	public void close() {

    	JmsUtils.close (requestSession);
    	JmsUtils.close (responseQueueSession);
    }

    /* (non-Javadoc)
	 * @see ua.service.messagequeue.clientserver.QueueClient#newTextMessage(int, java.lang.String)
	 */
    @Override
	public TextMessage newTextMessage (int messageId, String messageText) throws JMSException {

        TextMessage message;
		
        message = requestSession.createTextMessage();
        message.setIntProperty (ID_FIELD, messageId);
        message.setText(messageText);

        return message;
    }

    /* (non-Javadoc)
	 * @see ua.service.messagequeue.clientserver.QueueClient#newTextMessage(java.lang.String)
	 */
    @Override
	public TextMessage newTextMessage (String messageText) throws JMSException {

        TextMessage message;
		
        message = requestSession.createTextMessage();
        message.setText(messageText);

        return message;
    }
    
    /* (non-Javadoc)
	 * @see ua.service.messagequeue.clientserver.QueueClient#send(javax.jms.Message)
	 */
    @Override
	public Message send (Message requestMessage) throws JMSException, OperationTimedOut {
		
		String correlationId;
		Message responseMessage = null;
		QueueReceiver responseQueueReceiver;
		
		correlationId = UUID.randomUUID().toString();

		requestMessage.setJMSCorrelationID (correlationId);
		requestMessage.setJMSReplyTo (this.responseDestination);
		
		requestProducer.send (requestMessage);
		
		responseQueueReceiver = responseQueueSession.createReceiver (responseQueue, "JMSCorrelationID='" + correlationId + "'");
		try {
			responseMessage = responseQueueReceiver.receive (this.timeoutMilliseconds);
		}
		finally {
		    JmsUtils.close(responseQueueReceiver);
		}
		
		if (responseMessage == null) {
			throw new OperationTimedOut("Queue didn't return response within time limit (" + this.timeoutMilliseconds + " ms).");
		}
		
		return responseMessage;
	}
}
