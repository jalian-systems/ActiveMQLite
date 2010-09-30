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
 * The Class ClientServerRemoteObjectsUsedAsRemoteObjectsTest.
 */
public class ClientServerRemoteObjectsUsedAsRemoteObjectsTest {

	/**
	 * The Class Bean.
	 */
	public static class Bean implements IBean {

		/** The name. */
		private String name;

		/** The age. */
		private int age;

		/** The title. */
		private String title;

		/** The no setter getter. */
		private String noSetterGetter;

		/**
		 * Instantiates a new bean.
		 * 
		 * @param name
		 *            the name
		 * @param age
		 *            the age
		 * @param title
		 *            the title
		 */
		public Bean(String name, int age, String title) {
			this.name = name;
			this.age = age;
			this.title = title;
			noSetterGetter = "NoSetterGetter";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.jaliansystems.jmslite.IBean#getAge()
		 */
		public int getAge() {
			return age;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.jaliansystems.jmslite.IBean#getName()
		 */
		public String getName() {
			return name;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.jaliansystems.jmslite.IBean#getTitle()
		 */
		public String getTitle() {
			return title;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seecom.jaliansystems.jmslite.
		 * ClientServerRemoteObjectsUsedAsRemoteObjectsTest
		 * .IBean#noSetterGetter()
		 */
		public String noSetterGetter() {
			return noSetterGetter;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.jaliansystems.jmslite.IBean#setAge(int)
		 */
		public void setAge(int age) {
			this.age = age;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.jaliansystems.jmslite.IBean#setName(java.lang.String)
		 */
		public void setName(String name) {
			this.name = name;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.jaliansystems.jmslite.IBean#setTitle(java.lang.String)
		 */
		public void setTitle(String title) {
			this.title = title;
		}
	}

	/**
	 * The Class ClientServerReturnTest.
	 */
	public static class ClientServerReturnTest implements
			IClientServerReturnTypeTest {

		private Bean ibean = new Bean("JMSLite", 2, "Software");

		/*
		 * (non-Javadoc)
		 * 
		 * @seecom.jaliansystems.jmslite.
		 * ClientServerRemoteObjectsUsedAsRemoteObjectsTest
		 * .IClientServerReturnTypeTest
		 * #getMeBackTheRemote(com.jaliansystems.jmslite
		 * .ClientServerRemoteObjectsUsedAsRemoteObjectsTest.IBean)
		 */
		public IBean getMeBackTheRemote(IBean bean) {
			return bean;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seecom.jaliansystems.jmslite.
		 * ClientServerRemoteObjectsUsedAsRemoteObjectsTest
		 * .IClientServerReturnTypeTest#ibeanReturn()
		 */
		public IBean ibeanReturn() {
			return ibean;
		}

	}

	/**
	 * The Interface IBean.
	 */
	public interface IBean {

		/**
		 * Gets the age.
		 * 
		 * @return the age
		 */
		public abstract int getAge();

		/**
		 * Gets the name.
		 * 
		 * @return the name
		 */
		public abstract String getName();

		/**
		 * Gets the title.
		 * 
		 * @return the title
		 */
		public abstract String getTitle();

		/**
		 * No setter getter.
		 * 
		 * @return the string
		 */
		public abstract String noSetterGetter();

		/**
		 * Sets the age.
		 * 
		 * @param age
		 *            the new age
		 */
		public abstract void setAge(int age);

		/**
		 * Sets the name.
		 * 
		 * @param name
		 *            the new name
		 */
		public abstract void setName(String name);

		/**
		 * Sets the title.
		 * 
		 * @param title
		 *            the new title
		 */
		public abstract void setTitle(String title);
	}

	/**
	 * The Interface IClientServerReturnTypeTest.
	 */
	public interface IClientServerReturnTypeTest {

		/**
		 * Gets the me back the remote.
		 * 
		 * @param bean
		 *            the bean
		 * @return the me back the remote
		 */
		public IBean getMeBackTheRemote(IBean bean);

		/**
		 * Ibean return.
		 * 
		 * @return the i bean
		 */
		public IBean ibeanReturn();
	}

	/** The instance. */
	private static IClientServerReturnTypeTest instance;

	/** The server. */
	private static JMSConnection server;

	/** The broker service. */
	private static BrokerService brokerService;

	/**
	 * Sets the up class.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@BeforeClass
	public static void setUpClass() throws Exception {
		brokerService = JMSConnection.startBrokerService(61121);
		server = new JMSConnection("server", "tcp://localhost:61121");
		server.publish(new ClientServerReturnTest(),
				IClientServerReturnTypeTest.class);
		server.exportInterface(IBean.class);
		JMSConnection client = new JMSConnection("client",
				"tcp://localhost:61121");
		instance = client.lookup("server", IClientServerReturnTypeTest.class);
	}

	/**
	 * Teardown class.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@AfterClass
	public static void teardownClass() throws Exception {
		JMSConnection.stopBrokerService(brokerService);
	}

	/**
	 * Can establish a connection.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void canEstablishAConnection() throws Exception {
		assertNotNull(instance);
	}

	/**
	 * Method returns object handle.
	 */
	@Test
	public void methodReturnsObjectHandle() {
		IBean ibean = instance.ibeanReturn();
		assertEquals("JMSLite", ibean.getName());
		assertEquals(2, ibean.getAge());
		assertEquals("Software", ibean.getTitle());
		assertEquals("NoSetterGetter", ibean.noSetterGetter());
		instance.getMeBackTheRemote(ibean);
	}

	/**
	 * Unreferred removed is g ced.
	 */
	@Test
	public void unreferredRemovedIsGCed() {
		f();
		System.gc();
	}

	/**
	 * F.
	 */
	private void f() {
		instance.ibeanReturn();
	}

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
	}

	/**
	 * Handling multiple references
	 * @throws InterruptedException 
	 */
	@Test
	public void handleMultipleReferences() throws InterruptedException {
		IBean ibeanReturn = instance.ibeanReturn();
		f();
		System.gc();
		Thread.sleep(5);
		ibeanReturn.getName();
	}

}
