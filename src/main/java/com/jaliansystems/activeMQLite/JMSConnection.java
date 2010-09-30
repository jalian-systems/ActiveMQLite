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
package com.jaliansystems.activeMQLite;

import javax.jms.Connection;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

import com.jaliansystems.activeMQLite.impl.ObjectRepository;
import com.jaliansystems.activeMQLite.impl.RepositoryClient;
import com.jaliansystems.activeMQLite.impl.RepositoryService;

/**
 * A connection to JMS broker service that provides a facade of remote objects.
 * 
 * <p>
 * A JMSConnection facilitates publishing objects that are accessible to remote
 * clients. Each JMSConnection should be started with a unique ID. The id is
 * used in naming the JMS queues.
 * 
 * <p>
 * Once a JMSConnection is created, {@link JMSConnection#publish(Object, Class)}
 * is used to make a local object available to remote clients. Clients can use
 * {@link JMSConnection#lookup(String, Class)} to retrieve an handle to a
 * published handle.
 * 
 * <p>
 * A JMS broker service should have been available to use JMSConnection. The
 * helper methods {@link JMSConnection#startBrokerService(int)} and
 * {@link JMSConnection#stopBrokerService(BrokerService)} can be used to start
 * and stop the broker service or you can use an existing broker service.
 * 
 * <p>
 * A typical invocation sequence is as follows:
 * 
 * <blockquote>
 * 
 * <pre>
 * // Start broker service if needed
 * JMSConnection.startBrokerService(7121);
 * 
 * // On the server side
 * JMSConnection c = new JMSConnection(&quot;server&quot;, &quot;tcp://localhost:7121&quot;);
 * c.publish(myObject, MyClass.class);
 * 
 * // On the client side
 * JMSConnection c = new JMSConnection(&quot;client&quot;, &quot;tcp://localhost:7121&quot;);
 * MyClass myObject = c.lookup(&quot;server&quot;, MyClass.class);
 * // use myObject as if it is a local object here
 * </pre>
 * 
 * </blockquote>
 */
public class JMSConnection {

	/**
	 * Start broker service.
	 * 
	 * @param port
	 *            the port
	 * @return the broker service
	 * @throws Exception
	 *             the exception
	 */
	public static BrokerService startBrokerService(int port) throws Exception {
		String messageBrokerURL = "tcp://localhost:" + port;
		BrokerService broker = new BrokerService();
		broker.setPersistent(false);
		broker.setUseJmx(false);
		broker.addConnector(messageBrokerURL);
		broker.start();
		broker.waitUntilStarted();
		return broker;
	}

	/**
	 * Stop broker service.
	 * 
	 * @param broker
	 *            the broker
	 * @throws Exception
	 *             the exception
	 */
	public static void stopBrokerService(BrokerService broker) throws Exception {
		broker.stop();
		broker.waitUntilStopped();
	}

	private RepositoryService repositoryService;
	private RepositoryClient client;
	private ObjectRepository objectRepository;

	/**
	 * Instantiates a new JMS connection.
	 * 
	 * @param id
	 *            the identifier with which this connection is referred from
	 *            other connections. The identifier is used to name the JMS
	 *            queue names.
	 * @param messageBrokerURL
	 *            The URL where the JMS broker is running.
	 * @throws Exception
	 */
	public JMSConnection(String id, String messageBrokerURL) throws Exception {
		objectRepository = new ObjectRepository(messageBrokerURL, id
				+ "-request");
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				messageBrokerURL);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		client = new RepositoryClient(connection, messageBrokerURL,
				objectRepository);
		repositoryService = new RepositoryService(connection, messageBrokerURL,
				id, objectRepository, client);
	}

	/**
	 * Export interface.
	 * 
	 * When objects belonging to the type of an exported interface are used as
	 * either parameters are return values of a method, the object handle is
	 * transferred between the connections. In all other cases the object is
	 * serialized on the wire using the set/get/is methods available in the
	 * object.
	 * 
	 * @param ifaces
	 *            the interfaces to be exported.
	 */
	public void exportInterface(Class<?>... ifaces) {
		repositoryService.exportInterfaces(ifaces);
	}

	/**
	 * Looks up a remote object. The object must have been published by a remote
	 * connection referred by id.
	 * 
	 * Any method invocations on the returned object are seamlessly passed to
	 * the remote connection and the results are returned to the caller.
	 * 
	 * @param id
	 *            the id of the remote connection
	 * @param iface
	 *            the interface of the object being looked up
	 * @return the facade of the object.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("unchecked")
	public <T> T lookup(String id, Class<?> iface) throws Exception {
		return (T) client.lookup(id, iface);
	}

	/**
	 * Publish an object. The object will be available from now onwards to the
	 * remote connections, which can retrieve the handle using
	 * {@link JMSConnection#lookup(String, Class)} method.
	 * 
	 * @param impl
	 *            the implemented object
	 * @param iface
	 *            the interface the object implements
	 */
	public void publish(Object impl, Class<?> iface) {
		repositoryService.publish(impl, iface);
	}
}
