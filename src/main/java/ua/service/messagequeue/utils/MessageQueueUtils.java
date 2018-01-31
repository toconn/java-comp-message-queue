package ua.service.messagequeue.utils;

import ua.service.messagequeue.QueueFactory;
import ua.service.messagequeue.activemq.JmsComponentFactoryActiveMQ;
import ua.service.messagequeue.jms.JmsComponentFactory;
import ua.service.messagequeue.jms.JmsComponentFactoryDebug;
import ua.service.messagequeue.jms.JmsQueueFactoryCaching;
import ua.service.messagequeue.jms.JmsQueueFactorySimple;
import ua.service.messagequeue.qpid.JmsComponentFactoryQpid;

public class MessageQueueUtils {

	public static QueueFactory newCachedActiveMQQueueFactory (boolean includeDebugging) {
		
		return new JmsQueueFactoryCaching (newActiveMQComponentFactory(includeDebugging));
	}
	
	public static QueueFactory newCachedQpidQueueFactory (boolean includeDebugging) {
		
		return new JmsQueueFactoryCaching (newQpidComponentFactory (includeDebugging));
	}
	
	public static QueueFactory newSimpleQpidQueueFactory (boolean includeDebugging) {
		
		return new JmsQueueFactorySimple (newQpidComponentFactory (includeDebugging));
	}
	
	public static JmsComponentFactory newActiveMQComponentFactory (boolean includeDebugging)	 {
		
		JmsComponentFactory componentFactory = new JmsComponentFactoryActiveMQ();
		
		if (includeDebugging) {
			componentFactory = new JmsComponentFactoryDebug (componentFactory);
		}
		
		return componentFactory;
	}
	
	public static JmsComponentFactory newQpidComponentFactory (boolean includeDebugging)	 {
		
		JmsComponentFactory componentFactory = new JmsComponentFactoryQpid();
		
		if (includeDebugging) {
			componentFactory = new JmsComponentFactoryDebug (componentFactory);
		}
		
		return componentFactory;
	}
}
