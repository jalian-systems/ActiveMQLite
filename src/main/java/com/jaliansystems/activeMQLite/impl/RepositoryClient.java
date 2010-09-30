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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TemporaryQueue;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * RepositoryClient provides methods to look up published objects and invoke
 * methods on remote object handles.
 */
public class RepositoryClient implements MessageListener {

	/**
	 * The Class Response.
	 */
	private static class Response {
		public Object response;
	}

	/**
	 * The Class CallResponse.
	 */
	private static class CallResponse extends Response {
		public boolean error;
		public String exception;
		public String message;
	}

	private final static Log log = LogFactory.getLog(RepositoryClient.class);

	private MessageProducer requestProducer;
	private TemporaryQueue responseQueue;
	private MessageConsumer responseConsumer;
	private int correlationID = 0;
	private Map<String, Queue> queues = new HashMap<String, Queue>();
	private Map<Integer, Response> responseMap = new HashMap<Integer, Response>();
	private final ObjectRepository objectRepository;
	private Session session;

	/**
	 * Instantiates a new repository client.
	 * 
	 * @param connection
	 *            the connection
	 * @param brokerURL
	 *            the broker url
	 * @param objectRepository
	 *            the object repository
	 * @throws Exception
	 *             the exception
	 */
	public RepositoryClient(Connection connection, String brokerURL,
			ObjectRepository objectRepository) throws Exception {
		this.objectRepository = objectRepository;

		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		requestProducer = session.createProducer(null);
		requestProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		responseQueue = session.createTemporaryQueue();
		responseConsumer = session.createConsumer(responseQueue);
		responseConsumer.setMessageListener(this);
	}

	/**
	 * Invoke a method on remote object referred by ObjectHandle.
	 * 
	 * @param handle
	 *            the handle
	 * @param method
	 *            the method
	 * @param args
	 *            the args
	 * @return the object
	 * @throws Exception
	 *             the exception
	 */
	public Object invoke(ObjectHandle handle, Method method, Object[] args)
			throws Exception {
		JMSLiteMessage message = new JMSLiteMessage();
		message.write(RepositoryService.MESSAGE_CALL);
		objectRepository.encodeMethodCall(message, handle, method, args);
		message.setJMSReplyTo(responseQueue);
		int cID;
		synchronized (this) {
			cID = ++correlationID;
		}
		message.setJMSCorrelationID(cID + "");
		CallResponse response = new CallResponse();
		responseMap.put(cID, response);
		Queue queue = findQueue(handle.getQueueName());

		synchronized (response) {
			requestProducer.send(queue, message);
			response.wait();
		}
		responseMap.remove(cID);
		if (response.error) {
			throw new Exception("RemoteError: " + response.exception + ":"
					+ response.message);
		} else {
			Object r = response.response;
			if (r instanceof ObjectHandle) {
				return objectRepository.lookupHandle((ObjectHandle) r, this);
			}
			return r;
		}
	}

	/**
	 * Lookup a published remote object.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param id
	 *            the id
	 * @param iface
	 *            the iface
	 * @return the t
	 * @throws Exception
	 *             the exception
	 */
	public <T> T lookup(String id, Class<?> iface) throws Exception {
		JMSLiteMessage message = new JMSLiteMessage();
		message.write(RepositoryService.MESSAGE_LOOKUP);
		message.write(iface.getName());
		message.setJMSReplyTo(responseQueue);
		int cID;
		synchronized (this) {
			cID = ++correlationID;
		}
		message.setJMSCorrelationID(cID + "");
		Response response = new Response();
		responseMap.put(cID, response);

		Queue queue = findQueue(id + "-request");
		synchronized (response) {
			requestProducer.send(queue, message);
			response.wait();
		}
		responseMap.remove(cID);
		return (T) response.response;
	}

	/**
	 * Handle responses for requests sent through this client.
	 */
	public void onMessage(Message m) {
		try {
			JMSLiteMessage message = new JMSLiteMessage(
					(ActiveMQBytesMessage) m);
			byte type = (Byte) message.read();
			if (type == RepositoryService.MESSAGE_LOOKUP) {
				handleLookupResponse(message);
			} else if (type == RepositoryService.MESSAGE_CALL) {
				handleCallResponse(message);
			} else if (type == RepositoryService.MESSAGE_REMOVE) {
				handleMessageRemoveResponse(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Remove a remote object mapping.
	 * 
	 * @param handle
	 *            the handle
	 * @throws Exception
	 *             the exception
	 */
	public void remove(ObjectHandle handle) throws Exception {
		JMSLiteMessage message = new JMSLiteMessage();
		message.write(RepositoryService.MESSAGE_REMOVE);
		message.write(handle);
		message.setJMSReplyTo(responseQueue);
		int cID;
		synchronized (this) {
			cID = ++correlationID;
		}
		message.setJMSCorrelationID(cID + "");
		Response response = new Response();
		responseMap.put(cID, response);

		Queue queue = findQueue(handle.getQueueName());
		synchronized (response) {
			requestProducer.send(queue, message);
			response.wait();
		}
		responseMap.remove(cID);
		if (!(Boolean) response.response) {
			log.warn("RepositoryClient.remove(): failed");
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T castIt(Object object) {
		return (T) object;
	}

	private Queue findQueue(String queueName) throws JMSException {
		Queue queue = queues.get(queueName);
		if (queue != null)
			return queue;
		queue = session.createQueue(queueName);
		queues.put(queueName, queue);
		return queue;
	}

	private void handleCallResponse(JMSLiteMessage message) {
		int cID = Integer.parseInt(message.getJMSCorrelationID());
		CallResponse r = (CallResponse) responseMap.get(cID);
		byte error;
		synchronized (r) {
			try {
				error = (Byte) message.read();
				if (error == 0) {
					r.response = message.read();
				} else {
					r.error = true;
					r.exception = (String) message.read();
					r.message = (String) message.read();
				}
			} catch (Exception e) {
				if (log.isDebugEnabled())
					e.printStackTrace();
				r.error = true;
				r.exception = "ReadError";
				r.message = "An error occured while decoding the message";
			}
			r.notify();
		}
	}

	private void handleLookupResponse(JMSLiteMessage message) throws Exception {
		ObjectHandle handle = (ObjectHandle) message.read();
		int cID = Integer.parseInt(message.getJMSCorrelationID());
		Response r = responseMap.get(cID);
		synchronized (r) {
			r.response = objectRepository.lookupHandle(handle, this);
			r.notify();
		}
	}

	private void handleMessageRemoveResponse(JMSLiteMessage message) throws Exception {
		int cID = Integer.parseInt(message.getJMSCorrelationID());
		Response r = responseMap.get(cID);
		boolean b = (Boolean) message.read();
		synchronized (r) {
			r.response = b;
			r.notify();
		}
	}

}
