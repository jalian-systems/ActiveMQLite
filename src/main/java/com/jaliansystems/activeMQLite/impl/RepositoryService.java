/*
 *   Copyright 2010 Jalian Systems Pvt. Ltd.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.jaliansystems.activeMQLite.impl;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The Class RepositoryService.
 * 
 * Implements the protocol required to access object repository 
 */
public class RepositoryService implements MessageListener {

	/**
	 * An error response for MESSAGE_CALL
	 */
	private static final byte CALL_ERROR = 1;
	/**
	 * A success response for MESSAGE_CALL
	 */
	private static final byte CALL_SUCCESS = 0;

	/**
	 * Lookup Message
	 */
	public static final byte MESSAGE_LOOKUP = 1;
	/**
	 * Call Message
	 */
	public static final byte MESSAGE_CALL = 2;
	/**
	 * Remove Message
	 */
	public static final byte MESSAGE_REMOVE = 3;

	private final ObjectRepository objectRepository;
	private Session sessionService;
	private MessageProducer responseProducer;
	
	private final RepositoryClient client;

	private static final Log log = LogFactory.getLog(RepositoryService.class);
	
	/**
	 * Instantiates a new repository service.
	 *
	 * The queueNamePrefix is used to create a JMS queue with the name appending "-request" to it.
	 * 
	 * @param connection the connection
	 * @param brokerURL the broker url
	 * @param queueNamePrefix the queue name prefix
	 * @param objectRepository the object repository
	 * @param client the client
	 * @throws Exception the exception
	 */
	public RepositoryService(Connection connection, String brokerURL, String queueNamePrefix,
			ObjectRepository objectRepository, RepositoryClient client)
			throws Exception {
		this.objectRepository = objectRepository;
		this.client = client;

		sessionService = connection.createSession(false,
				Session.AUTO_ACKNOWLEDGE);
		Destination requestQueue = sessionService.createQueue(queueNamePrefix
				+ "-request");
		MessageConsumer requestConsumer = sessionService
				.createConsumer(requestQueue);
		requestConsumer.setMessageListener(this);

		responseProducer = sessionService.createProducer(null);
		responseProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
	}

	/**
	 * Export interfaces.
	 *
	 * @param exportedInterfaces the exported interfaces
	 */
	public void exportInterfaces(Class<?>... exportedInterfaces) {
		objectRepository.exportInterfaces(exportedInterfaces);
	}

	/**
	 * Handles message requests for this object repository.
	 * 
	 * All messages are JMSLiteMessage the protocol of which is explained in Protocol.txt
	 */
	public void onMessage(Message message) {
		if (message instanceof ActiveMQBytesMessage) {
			try {
				JMSLiteMessage jmsMessage = new JMSLiteMessage(
						(ActiveMQBytesMessage) message);
				byte message_type = (Byte) jmsMessage.read();
				if (message_type == MESSAGE_LOOKUP) {
					String className = (String) jmsMessage.read();
					handleLookup(className, message.getJMSReplyTo(), message
							.getJMSCorrelationID());
				} else if (message_type == MESSAGE_CALL) {
					JMSLiteMessage rmessage = new JMSLiteMessage();
					rmessage.write(MESSAGE_CALL);
					try {
						Object returnVal = objectRepository.invoke(jmsMessage,
								client);
						rmessage.write(CALL_SUCCESS);
						rmessage.write(returnVal);
					} catch (Throwable t) {
						if (log.isDebugEnabled())
							t.printStackTrace();
						rmessage.write(CALL_ERROR);
						rmessage.write(t.getClass().getName());
						rmessage.write(t.getMessage());
					}
					rmessage.setJMSCorrelationID(message.getJMSCorrelationID());
					rmessage.setJMSDestination(message.getJMSReplyTo());
					responseProducer.send(message.getJMSReplyTo(), rmessage);
				} else if (message_type == MESSAGE_REMOVE) {
					ObjectHandle handle = (ObjectHandle) jmsMessage.read();
					boolean b = objectRepository.removeObject(handle);
					JMSLiteMessage rmessage = new JMSLiteMessage();
					rmessage.write(MESSAGE_REMOVE);
					rmessage.write(b);
					rmessage.setJMSCorrelationID(message.getJMSCorrelationID());
					rmessage.setJMSDestination(message.getJMSReplyTo());
					responseProducer.send(message.getJMSReplyTo(), rmessage);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Publish an object.
	 *
	 * @param impl the implementation of the interface
	 * @param iface the interface
	 */
	public void publish(Object impl, Class<?> iface) {
		objectRepository.publish(impl, iface);
	}

	/**
	 * Handle lookup.
	 *
	 * @param className the class name
	 * @param dest the dest
	 * @param cID the c id
	 * @throws Exception the exception
	 */
	private void handleLookup(String className, Destination dest, String cID)
			throws Exception {
		Class<?> iface = Class.forName(className);
		ObjectHandle handle = objectRepository.lookup(iface);
		JMSLiteMessage message = new JMSLiteMessage();
		message.setJMSCorrelationID(cID);
		message.setJMSDestination(dest);
		message.write(MESSAGE_LOOKUP);
		message.write(handle);
		responseProducer.send(dest, message);
	}
}
