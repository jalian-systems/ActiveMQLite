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
import static org.junit.Assert.assertNull;

import org.apache.activemq.broker.BrokerService;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class ClientServerReturnTypeTest.
 */
public class ClientServerReturnTypeTest {

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
		 */
		public Bean() {
		}

		/**
		 * Instantiates a new bean.
		 *
		 * @param name the name
		 * @param age the age
		 * @param title the title
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

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerReturnTypeTest.IBean#noSetterGetter()
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
	 * The Class BeanWithoutDefaultConstructor.
	 */
	public static class BeanWithoutDefaultConstructor extends Bean {

		/**
		 * Instantiates a new bean without default constructor.
		 *
		 * @param name the name
		 */
		public BeanWithoutDefaultConstructor(String name) {
		}

	}

	/**
	 * The Class ClientServerReturnTest.
	 */
	public static class ClientServerReturnTest implements
			IClientServerReturnTypeTest {
		
		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerReturnTypeTest.IClientServerReturnTypeTest#beanReturn()
		 */
		public Bean beanReturn() {
			return new Bean("JMSLite", 2, "Software");
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerReturnTypeTest.IClientServerReturnTypeTest#booleanReturn()
		 */
		public boolean booleanReturn() {
			return true;
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerReturnTypeTest.IClientServerReturnTypeTest#doubleReturn()
		 */
		public double doubleReturn() {
			return 1810.1810;
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerReturnTypeTest.IClientServerReturnTypeTest#getBeanWithoutDefaultConstructor()
		 */
		public BeanWithoutDefaultConstructor getBeanWithoutDefaultConstructor() {
			return new BeanWithoutDefaultConstructor("SomeName");
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerReturnTypeTest.IClientServerReturnTypeTest#helloWorld()
		 */
		public String helloWorld() {
			return "Hello World";
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerReturnTypeTest.IClientServerReturnTypeTest#ibeanReturn()
		 */
		public IBean ibeanReturn() {
			return new Bean("JMSLite", 2, "Software");
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerReturnTypeTest.IClientServerReturnTypeTest#intReturn()
		 */
		public int intReturn() {
			return 1810;
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerReturnTypeTest.IClientServerReturnTypeTest#nullReturn()
		 */
		public Object nullReturn() {
			return null;
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerReturnTypeTest.IClientServerReturnTypeTest#stringReturn()
		 */
		public String stringReturn() {
			return "1810";
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerReturnTypeTest.IClientServerReturnTypeTest#throwsException()
		 */
		public Object throwsException() throws IllegalAccessException {
			throw new IllegalAccessException(
					"We do not expect this function to return a value");
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.ClientServerReturnTypeTest.IClientServerReturnTypeTest#voidReturn()
		 */
		public void voidReturn() {
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
		 * @param age the new age
		 */
		public abstract void setAge(int age);

		/**
		 * Sets the name.
		 *
		 * @param name the new name
		 */
		public abstract void setName(String name);

		/**
		 * Sets the title.
		 *
		 * @param title the new title
		 */
		public abstract void setTitle(String title);
	}

	/**
	 * The Interface IClientServerReturnTypeTest.
	 */
	public interface IClientServerReturnTypeTest {
		
		/**
		 * Bean return.
		 *
		 * @return the bean
		 */
		public Bean beanReturn();

		/**
		 * Boolean return.
		 *
		 * @return true, if successful
		 */
		public boolean booleanReturn();

		/**
		 * Double return.
		 *
		 * @return the double
		 */
		public double doubleReturn();

		/**
		 * Gets the bean without default constructor.
		 *
		 * @return the bean without default constructor
		 */
		public BeanWithoutDefaultConstructor getBeanWithoutDefaultConstructor();

		/**
		 * Hello world.
		 *
		 * @return the string
		 */
		public String helloWorld();

		/**
		 * Ibean return.
		 *
		 * @return the i bean
		 */
		public IBean ibeanReturn();

		/**
		 * Int return.
		 *
		 * @return the int
		 */
		public int intReturn();

		/**
		 * Null return.
		 *
		 * @return the object
		 */
		public Object nullReturn();

		/**
		 * String return.
		 *
		 * @return the string
		 */
		public String stringReturn();

		/**
		 * Throws exception.
		 *
		 * @return the object
		 * @throws IllegalAccessException the illegal access exception
		 */
		public Object throwsException() throws IllegalAccessException;

		/**
		 * Void return.
		 */
		public void voidReturn();
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
	 * @throws Exception the exception
	 */
	@BeforeClass
	public static void setUpClass() throws Exception {
		brokerService = JMSConnection.startBrokerService(61121);
		server = new JMSConnection("server", "tcp://localhost:61121");
		server.publish(new ClientServerReturnTest(),
				IClientServerReturnTypeTest.class);
		server.exportInterface(IBean.class);
		JMSConnection client = new JMSConnection("client", "tcp://localhost:61121");
		instance = client.lookup("server", IClientServerReturnTypeTest.class);
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
	 * An unpublished unserializable object throws exception.
	 */
	@Test(expected = Exception.class)
	public void anUnpublishedUnserializableObjectThrowsException() {
		instance.getBeanWithoutDefaultConstructor();
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
	 * Method returns null.
	 */
	@Test
	public void methodReturnsNull() {
		assertNull(instance.nullReturn());
	}

	/**
	 * Method returns object handle.
	 */
	@Test
	public void methodReturnsObjectHandle() {
		IBean ibeanReturn = instance.ibeanReturn();
		IBean ibean = ibeanReturn;
		assertEquals("JMSLite", ibean.getName());
		assertEquals(2, ibean.getAge());
		assertEquals("Software", ibean.getTitle());
		assertEquals("NoSetterGetter", ibean.noSetterGetter());
	}

	/**
	 * Method returns premitive.
	 */
	@Test
	public void methodReturnsPremitive() {
		assertEquals(1810, instance.intReturn());
		assertEquals(1810.1810, instance.doubleReturn(), 0.01);
		assertEquals(true, instance.booleanReturn());
		assertEquals("1810", instance.stringReturn());
		assertEquals("Hello World", instance.helloWorld());
	}

	/**
	 * Method returns serializable object.
	 */
	@Test
	public void methodReturnsSerializableObject() {
		IBean bean = instance.beanReturn();
		assertEquals("JMSLite", bean.getName());
		assertEquals(2, bean.getAge());
		assertEquals("Software", bean.getTitle());
		assertNull(bean.noSetterGetter());
	}

	/**
	 * Method returns void.
	 */
	@Test
	public void methodReturnsVoid() {
		instance.voidReturn();
	}

	/**
	 * Method throws exception.
	 *
	 * @throws IllegalAccessException the illegal access exception
	 */
	@Test(expected = Exception.class)
	public void methodThrowsException() throws IllegalAccessException {
		assertNull(instance.throwsException());
	}

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
	}
}
