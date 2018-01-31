package ua.service.messagequeue.jms;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.QueueReceiver;
import javax.jms.Session;
import javax.jms.TextMessage;

import static ua.service.messagequeue.MessageQueueConst.*;

import static ua.core.utils.console.ConsoleUtils.*;

public class JmsUtils {
	
	public static void close (Connection connection) {
		
		try {
			if (connection != null) {
				connection.close();
			}
		}
		catch (JMSException e) {
			print ("Error closing connection. Ignoring.");
		}
	}
	
	public static void close(QueueReceiver responseQueueReceiver) {
	    try {
            if(responseQueueReceiver != null) {
                responseQueueReceiver.close();
            }
        }
        catch(JMSException e) {
            print("Error closing connection. Ignoring.");
        }
	}
	
	public static void close (Session session) {
		
		try {
			if (session != null) {
				session.close();
			}
		}
		catch (JMSException e) {
			print ("Error closing session. Ignoring.");
		}
	}

	public static TextMessage newTextMessage (Session session, String messageText) throws JMSException {
		
		TextMessage textMessage;
		
		textMessage = session.createTextMessage();
		textMessage.setText(messageText);
		
		return textMessage;
	}

	public static TextMessage newTextMessage (Session session, int messageId, String messageText) throws JMSException {
		
		TextMessage textMessage;
		
		textMessage = session.createTextMessage();
		textMessage.setIntProperty(ID_FIELD, messageId);
		textMessage.setText(messageText);
		
		return textMessage;
	}
}
