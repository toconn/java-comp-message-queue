package ua.service.messagequeue.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import ua.service.messagequeue.QueueConsumer;


/**
 * Created with CodeCrank.io
 * 
 * Convenience facade around JMS Consumer and Queue classes.
 */
public class JmsQueueConsumer implements AutoCloseable, QueueConsumer {

    private final MessageConsumer messaegeConsumer;
    private final Session session;
    private final long timeoutMilliseconds;

    public JmsQueueConsumer (MessageConsumer messaegeConsumer, Session session, long timeoutMilliseconds) {

        this.messaegeConsumer = messaegeConsumer;
        this.session = session;
        this.timeoutMilliseconds = timeoutMilliseconds;
    }


    @Override
	public void close() {

    	JmsUtils.close (session);
    }

    @Override
	public Message retrieve() throws JMSException {

    	return messaegeConsumer.receive(this.timeoutMilliseconds);
    }

    @Override
	public Message retrieve (long timeoutMilliseconds) throws JMSException {

    	return messaegeConsumer.receive(timeoutMilliseconds);
    }

    @Override
	public Message retrieveNoWait() throws JMSException {

    	return messaegeConsumer.receiveNoWait();
    }

}