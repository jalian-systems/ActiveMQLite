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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.activemq.broker.BrokerService;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class ClientServerNParamTest.
 */
public class ClientServerNParamTest {

	/**
	 * The Class ClientServerReturnTest.
	 */
	public static class ClientServerReturnTest implements
			IClientServerNParamTest {

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerNParamTest.IClientServerNParamTest#methodWithOneParams(java.lang.String)
		 */
		public String methodWithOneParams(String p1) {
			return "methodWithOneParams:" + p1;
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerNParamTest.IClientServerNParamTest#methodWithThreeParams(java.lang.String, java.lang.String, java.lang.String)
		 */
		public String methodWithThreeParams(String p1, String p2, String p3) {
			return "methodWithThreeParams:" + p1 + ":" + p2 + ":" + p3;
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerNParamTest.IClientServerNParamTest#methodWithZeroParams()
		 */
		public String methodWithZeroParams() {
			return "methodWithZeroParams";
		}

	}

	/**
	 * The Interface IClientServerNParamTest.
	 */
	public interface IClientServerNParamTest {
		
		/**
		 * Method with one params.
		 *
		 * @param p1 the p1
		 * @return the string
		 */
		public String methodWithOneParams(String p1);

		/**
		 * Method with three params.
		 *
		 * @param p1 the p1
		 * @param p2 the p2
		 * @param p3 the p3
		 * @return the string
		 */
		public String methodWithThreeParams(String p1, String p2, String p3);

		/**
		 * Method with zero params.
		 *
		 * @return the string
		 */
		public String methodWithZeroParams();
	}

	/** The instance. */
	private static IClientServerNParamTest instance;

	/** The server. */
	private static JMSConnection server;

	/** The broker service. */
	private static BrokerService brokerService;

	/**
	 * Sets the up class.
	 *
	 * @throws Exception the exception
	 */
	@BeforeClass
	public static void setUpClass() throws Exception {
		brokerService = JMSConnection.startBrokerService(61121);
		server = new JMSConnection("server", "tcp://localhost:61121");
		server.publish(new ClientServerReturnTest(),
				IClientServerNParamTest.class);
		JMSConnection client = new JMSConnection("client", "tcp://localhost:61121");
		instance = client.lookup("server", IClientServerNParamTest.class);
	}

	/**
	 * Teardown class.
	 *
	 * @throws Exception the exception
	 */
	@AfterClass
	public static void teardownClass() throws Exception {
		JMSConnection.stopBrokerService(brokerService);
	}

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
	}

	/**
	 * Can establish a connection.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void canEstablishAConnection() throws Exception {
		assertNotNull(instance);
	}

	/**
	 * Method with one params.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void methodWithOneParams() throws Exception {
		assertEquals("methodWithOneParams:First", instance
				.methodWithOneParams("First"));
	}

	/**
	 * Method with three params.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void methodWithThreeParams() throws Exception {
		assertEquals("methodWithThreeParams:First:Second:Third", instance
				.methodWithThreeParams("First", "Second", "Third"));
	}

	/**
	 * Method with zero params.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void methodWithZeroParams() throws Exception {
		assertEquals("methodWithZeroParams", instance.methodWithZeroParams());
	}
}
