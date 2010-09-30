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
 * The Class ClientServerParamTypeTest.
 */
public class ClientServerParamTypeTest {

	/**
	 * The Class BeanServer.
	 */
	public static class BeanServer implements IBeanServer {
		
		/** The some field. */
		private String someField;

		/**
		 * Instantiates a new bean server.
		 */
		public BeanServer() {
		}

		/**
		 * Instantiates a new bean server.
		 *
		 * @param bean the bean
		 */
		public BeanServer(BeanServer bean) {
			someField = bean.getSomeField();
		}

		/**
		 * Instantiates a new bean server.
		 *
		 * @param bean the bean
		 */
		public BeanServer(IBeanServer bean) {
			someField = bean.getSomeField();
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerParamTypeTest.IBeanServer#beanParam(com.jaliansystems.jmslite.ClientServerParamTypeTest.BeanServer)
		 */
		public BeanServer beanParam(BeanServer bean) {
			return new BeanServer(bean);
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerParamTypeTest.IBeanServer#booleanParam(boolean)
		 */
		public String booleanParam(boolean p) {
			return "Boolean:" + p;
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerParamTypeTest.IBeanServer#doubleParam(double)
		 */
		public String doubleParam(double p) {
			return "Double:" + p;
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerParamTypeTest.IBeanServer#getSomeField()
		 */
		public String getSomeField() {
			return someField;
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerParamTypeTest.IBeanServer#ibeanParam(com.jaliansystems.jmslite.ClientServerParamTypeTest.IBeanServer)
		 */
		public IBeanServer ibeanParam(IBeanServer bean) {
			return new BeanServer(bean);
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerParamTypeTest.IBeanServer#integerParam(int)
		 */
		public String integerParam(int p) {
			return "Integer:" + p;
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerParamTypeTest.IBeanServer#nullParam(java.lang.String)
		 */
		public String nullParam(String p) {
			return p == null ? "Null Param" : "Not Null";
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerParamTypeTest.IBeanServer#setSomeField(java.lang.String)
		 */
		public void setSomeField(String someField) {
			this.someField = someField;
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerParamTypeTest.IBeanServer#stringParam(java.lang.String)
		 */
		public String stringParam(String p) {
			return "String:" + p;
		}
	}

	/**
	 * The Class ClientServerReturnTest.
	 */
	public static class ClientServerReturnTest implements
			IClientServerParamTypeTest {
		
		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerParamTypeTest.IClientServerParamTypeTest#getBeanServer()
		 */
		public BeanServer getBeanServer() {
			return new BeanServer();
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerParamTypeTest.IClientServerParamTypeTest#getIBeanServer()
		 */
		public IBeanServer getIBeanServer() {
			return new BeanServer();
		}
	}

	/**
	 * The Interface IBeanServer.
	 */
	public interface IBeanServer {
		
		/**
		 * Bean param.
		 *
		 * @param bean the bean
		 * @return the bean server
		 */
		public BeanServer beanParam(BeanServer bean);

		/**
		 * Boolean param.
		 *
		 * @param p the p
		 * @return the string
		 */
		public String booleanParam(boolean p);

		/**
		 * Double param.
		 *
		 * @param p the p
		 * @return the string
		 */
		public String doubleParam(double p);

		/**
		 * Gets the some field.
		 *
		 * @return the some field
		 */
		public String getSomeField();

		/**
		 * Ibean param.
		 *
		 * @param b the b
		 * @return the i bean server
		 */
		public IBeanServer ibeanParam(IBeanServer b);

		/**
		 * Integer param.
		 *
		 * @param p the p
		 * @return the string
		 */
		public String integerParam(int p);

		/**
		 * Null param.
		 *
		 * @param p the p
		 * @return the string
		 */
		public String nullParam(String p);

		/**
		 * Sets the some field.
		 *
		 * @param s the new some field
		 */
		public void setSomeField(String s);

		/**
		 * String param.
		 *
		 * @param p the p
		 * @return the string
		 */
		public String stringParam(String p);
	}

	/**
	 * The Interface IClientServerParamTypeTest.
	 */
	public interface IClientServerParamTypeTest {
		
		/**
		 * Gets the bean server.
		 *
		 * @return the bean server
		 */
		public BeanServer getBeanServer();

		/**
		 * Gets the i bean server.
		 *
		 * @return the i bean server
		 */
		public IBeanServer getIBeanServer();
	}

	/** The instance. */
	private static IClientServerParamTypeTest instance;

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
				IClientServerParamTypeTest.class);
		server.exportInterface(IBeanServer.class);
		JMSConnection client = new JMSConnection("client", "tcp://localhost:61121");
		instance = client.lookup("server", IClientServerParamTypeTest.class);
		client.exportInterface(IBeanServer.class);
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

	/** The bean server. */
	private BeanServer beanServer;
	
	/** The i bean server. */
	private IBeanServer iBeanServer;

	/**
	 * Can establish a connection.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void canEstablishAConnection() throws Exception {
		assertNotNull(instance);
		assertNotNull(beanServer);
		assertNotNull(iBeanServer);
	}

	/**
	 * Passes beans.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void passesBeans() throws Exception {
		BeanServer b = new BeanServer();
		b.setSomeField("SomeField");
		BeanServer b2 = beanServer.beanParam(b);
		assertEquals("SomeField", b2.getSomeField());
	}

	/**
	 * Passes null parameters.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void passesNullParameters() throws Exception {
		assertEquals("Null Param", iBeanServer.nullParam(null));
		assertEquals("Not Null", iBeanServer.nullParam(""));
	}

	/**
	 * Passes premitive parameters.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void passesPremitiveParameters() throws Exception {
		assertEquals("String:String Param",
				iBeanServer.stringParam("String Param"));
		assertEquals("Integer:1180", iBeanServer.integerParam(1180));
		assertEquals("Double:1180.101", iBeanServer.doubleParam(1180.101));
		assertEquals("Boolean:true", iBeanServer.booleanParam(true));
		assertEquals("Boolean:false", iBeanServer.booleanParam(false));
	}

	/**
	 * Passes published interfaces.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void passesPublishedInterfaces() throws Exception {
		f();
	}

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		beanServer = instance.getBeanServer();
		iBeanServer = instance.getIBeanServer();
	}

	/**
	 * F.
	 */
	private void f() {
		IBeanServer b = new BeanServer();
		b.setSomeField("SomeField");
		IBeanServer b2 = iBeanServer.ibeanParam(b);
		assertNotNull(b2);
		String someField = b2.getSomeField();
		assertEquals("SomeField", someField);
	}

}
