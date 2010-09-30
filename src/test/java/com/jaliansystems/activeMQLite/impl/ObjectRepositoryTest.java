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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.event.MouseListener;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.Before;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class ObjectRepositoryTest.
 */
public class ObjectRepositoryTest {

	/**
	 * The Class ImplObject.
	 */
	public static class ImplObject implements IObjectRepositoryTest {

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.impl.IObjectRepositoryTest#getGreeting()
		 */
		public String getGreeting() {
			return "Hello World";
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.impl.IObjectRepositoryTest#getGreeting(int)
		 */
		public String getGreeting(int i) {
			return "Hello Universe";
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.impl.IObjectRepositoryTest#takeThis(com.jaliansystems.jmslite.impl.IObjectRepositoryTest)
		 */
		public Object takeThis(IObjectRepositoryTest localObjectRepositoryTest) {
			return "TakeThisWithLORT";
		}

		/* (non-Javadoc)
		 * @see com.jaliansystems.jmslite.impl.IObjectRepositoryTest#takeThis(com.jaliansystems.jmslite.impl.ObjectRepository)
		 */
		public Object takeThis(ObjectRepository localObjectRepository) {
			return "TakeThisWithLOR";
		}
	}

	/** The finalize called. */
	protected boolean finalizeCalled;
	
	/** The destroy called. */
	private boolean destroyCalled;
	
	/** The repo. */
	private ObjectRepository repo;
	
	/** The message. */
	private JMSLiteMessage message;

	/** The impl. */
	private ImplObject impl;

	/**
	 * Destroy with proxies.
	 *
	 * @throws InterruptedException the interrupted exception
	 */
	@Test
	public void destroyWithProxies() throws InterruptedException {
		Object lock = new Object();
		createProxy(lock);
		System.gc();
		synchronized (lock) {
			if (!destroyCalled)
				lock.wait();
		}
		assertTrue(destroyCalled);

	}

	/**
	 * Encode method call.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void encodeMethodCall() throws Exception {
		ObjectHandle id = repo.publish(impl, IObjectRepositoryTest.class);
		Method method = impl.getClass()
				.getMethod("getGreeting", new Class[] {});

		repo.encodeMethodCall(message, repo.createHandle(impl,
				IObjectRepositoryTest.class), method);

		message.makeReadable();

		assertEquals(id, (message.read()));
		assertEquals("getGreeting", message.read());
		Object[] args = (Object[]) message.read();

		assertEquals(0, args.length);
	}

	/**
	 * Encode method call with parameters.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void encodeMethodCallWithParameters() throws Exception {
		ObjectHandle id = repo.publish(impl, IObjectRepositoryTest.class);
		Method method = impl.getClass().getMethod("takeThis",
				new Class[] { IObjectRepositoryTest.class });
		repo.encodeMethodCall(message, repo.createHandle(impl,
				IObjectRepositoryTest.class), method, impl);

		message.makeReadable();

		assertEquals(id, (message.read()));
		assertEquals("takeThis", message.read());
		Object[] args = (Object[]) message.read();

		assertEquals(1, args.length);
		assertTrue(args[0] instanceof ObjectHandle);
		assertEquals(id, (args[0]));
	}

	/**
	 * Invoke a method.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void invokeAMethod() throws Exception {
		repo.publish(impl, IObjectRepositoryTest.class);
		ObjectHandle handle = repo.createHandle(impl,
				IObjectRepositoryTest.class);
		message.write(handle);
		message.write("getGreeting");
		message.write(new Object[] {});

		message.makeReadable();

		Object retval = repo.invoke(message);

		assertEquals(impl.getGreeting(), retval);

	}

	/**
	 * Invoke a method on a object that is not available.
	 *
	 * @throws Exception the exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void invokeAMethodOnAObjectThatIsNotAvailable() throws Exception {
		ObjectHandle handle = repo.createHandle(impl,
				IObjectRepositoryTest.class);
		repo.removeObject(handle);
		message.write(handle);
		message.write("getGreeting");
		message.write(new Object[] {});

		message.makeReadable();

		Object retval = repo.invoke(message);

		assertEquals(impl.getGreeting(), retval);

	}

	/**
	 * Invoke methods with object parameters.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void invokeMethodsWithObjectParameters() throws Exception {
		repo.publish(impl, IObjectRepositoryTest.class);
		ObjectHandle handle = repo.createHandle(impl,
				IObjectRepositoryTest.class);
		message.write(handle);
		message.write("takeThis");
		message.write(new Object[] { impl });

		message.makeReadable();

		Object retval = repo.invoke(message);

		assertEquals(impl.takeThis(impl), retval);

	}

	/**
	 * Invoke methods with same name.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void invokeMethodsWithSameName() throws Exception {
		repo.publish(impl, IObjectRepositoryTest.class);
		ObjectHandle handle = repo.createHandle(impl,
				IObjectRepositoryTest.class);
		message.write(handle);
		message.write("getGreeting");
		message.write(new Object[] { 0 });

		message.makeReadable();

		Object retval = repo.invoke(message);

		assertEquals(impl.getGreeting(0), retval);

	}

	/**
	 * Invokes method with parameters with published interfaces.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void invokesMethodWithParametersWithPublishedInterfaces()
			throws Exception {
		repo.publish(impl, IObjectRepositoryTest.class);
		Method method = impl.getClass().getMethod("takeThis",
				new Class[] { IObjectRepositoryTest.class });
		repo.encodeMethodCall(message, repo.createHandle(impl,
				IObjectRepositoryTest.class), method, impl);

		message.makeReadable();

		Object ret = repo.invoke(message);
		assertEquals("TakeThisWithLORT", ret);
	}

	/**
	 * Looking up an unpublished interface returns null.
	 */
	@Test
	public void lookingUpAnUnpublishedInterfaceReturnsNull() {

		repo.publish(impl, IObjectRepositoryTest.class);

		Object o = repo.lookup(MouseListener.class);

		assertNull(o);
	}

	/**
	 * Lookup returns object handle.
	 */
	@Test
	public void lookupReturnsObjectHandle() {
		repo.publish(impl, IObjectRepositoryTest.class);

		ObjectHandle o = repo.lookup(IObjectRepositoryTest.class);

		assertNotNull(o);
		assertTrue(o.getObject() == impl);
	}

	/**
	 * Publishing interface throws an error if object does not implement it.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void publishingInterfaceThrowsAnErrorIfObjectDoesNotImplementIt() {
		repo.publish(impl, MouseListener.class);
	}

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		repo = new ObjectRepository("testing", "queue");
		message = new JMSLiteMessage();
		impl = new ImplObject();
	}

	/**
	 * Creates the proxy.
	 *
	 * @param lock the lock
	 */
	private void createProxy(final Object lock) {
		InvocationHandler handler = new InvocationHandler() {

			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				return null;
			}

			@Override
			protected void finalize() throws Throwable {
				super.finalize();
				synchronized (lock) {
					destroyCalled = true;
					lock.notify();
				}
			}
		};
		Proxy.newProxyInstance(this.getClass().getClassLoader(),
				new Class[] { IObjectRepositoryTest.class }, handler);
	}

}
