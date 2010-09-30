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
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Repository of local objects that are either published and/or exported.
 * 
 */
public class ObjectRepository {

	private static final Log log = LogFactory.getLog(ObjectRepository.class);

	/** The list of ObjectHandles. Overridden to add the trace messages */
	private List<ObjectHandle> list = new ArrayList<ObjectHandle>() {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean add(ObjectHandle e) {
			log.trace("adding " + e);
			return super.add(e);
		};

		@Override
		public boolean remove(Object o) {
			log.trace("removing " + o);
			return super.remove(o);
		};
	};

	private Map<Class<?>, Object> publishedInterfaces = new HashMap<Class<?>, Object>();
	private List<Class<?>> exportedInterfaces = new ArrayList<Class<?>>();
	private String brokerURL;

	private String queueName;
	private int objectID = 0;

	/**
	 * Instantiates a new object repository.
	 * 
	 * @param messageBrokerURL
	 *            the message broker URL.
	 * @param queueName
	 *            the queue name
	 */
	public ObjectRepository(String messageBrokerURL, String queueName) {
		brokerURL = messageBrokerURL;
		this.queueName = queueName;
	}

	/**
	 * Creates or returns a new ObjectHandle for the given object. This
	 * ObjectHandle can be used to retrieve the object.
	 * 
	 * @param o
	 *            the object for which the handle is required.
	 * @param iface
	 *            the interface which is implemented by the Object
	 * @return the object handle
	 */
	public ObjectHandle createHandle(Object o, Class<?> iface) {
		for (int i = 0; i < list.size(); i++) {
			ObjectHandle objectHandle = list.get(i);
			if (objectHandle.getObject() == o) {
				objectHandle.retain();
				return objectHandle;
			}
		}
		int index;
		synchronized (this) {
			index = ++objectID;
		}
		ObjectHandle handle = new ObjectHandle(index, o, iface, brokerURL,
				queueName);
		handle.retain();
		list.add(handle);
		return handle;
	}

	/**
	 * Lookup a handle
	 * 
	 * Looks up a handle in the local repository. If it does not exist in the
	 * local repository (it is a remote handle), creates a remote proxy to
	 * handle the method invocations on the object.
	 * 
	 * @param handle
	 *            the object handle to be looked up
	 * @param client
	 *            the repository client
	 * @return the object
	 * @throws Exception
	 *             the exception
	 */
	public Object lookupHandle(ObjectHandle handle, RepositoryClient client)
			throws Exception {
		if (list.contains(handle)) {
			int index = list.indexOf(handle);
			return list.get(index).getObject();
		}
		RemoteInvocationHandler handler = new RemoteInvocationHandler(handle,
				client);
		return Proxy.newProxyInstance(handle.getIFace().getClassLoader(),
				new Class[] { handle.getIFace(), IProxy.class }, handler);
	}

	/**
	 * Encodes a method call into a JMSLite message.
	 * 
	 * @param message
	 *            the message into which the method should be encoded
	 * @param objectHandle
	 *            the object handle
	 * @param method
	 *            the method to be encoded
	 * @param args
	 *            the arguments passed to the method
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void encodeMethodCall(JMSLiteMessage message,
			ObjectHandle objectHandle, Method method, Object... args)
			throws Exception {
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (args != null && parameterTypes.length != args.length
				|| args == null && parameterTypes.length != 0)
			throw new IllegalArgumentException(
					"In encodeMethodCall number of parameters and arguments do not match");
		message.write(objectHandle);
		message.write(method.getName());

		for (int i = 0; i < parameterTypes.length; i++) {
			if (parameterTypes[i].isInterface()
					&& exportedInterfaces.contains(parameterTypes[i])) {
				args[i] = createHandle(args[i], parameterTypes[i]);
			}
		}
		message.write(args);
	}

	/**
	 * Export interfaces.
	 * 
	 * @param ifaces
	 *            the ifaces
	 */
	public void exportInterfaces(Class<?>... ifaces) {
		exportedInterfaces.addAll(Arrays.asList(ifaces));
	}

	/**
	 * Gets the object from the local repository.
	 * 
	 * @param handle
	 *            the handle
	 * @return the object
	 */
	public Object getObject(ObjectHandle handle) {
		if (list.contains(handle)) {
			int index = list.indexOf(handle);
			return list.get(index).getObject();
		}
		return null;
	}

	/**
	 * Invoke.
	 * 
	 * @param message
	 *            the message
	 * @return the object
	 * @throws Exception
	 *             the exception
	 */
	public Object invoke(JMSLiteMessage message) throws Exception {
		return invoke(message, null);
	}

	/**
	 * Invoke a method on a local object.
	 * 
	 * @param message
	 *            the message received.
	 * @param client
	 *            the repository client.
	 * @return the return value from the method invocation.
	 * @throws Exception
	 *             the exception
	 */
	public Object invoke(JMSLiteMessage message, RepositoryClient client)
			throws Exception {
		ObjectHandle handle = (ObjectHandle) message.read();
		String methodName = (String) message.read();
		Object[] args = (Object[]) message.read();

		Object object = getObject(handle);
		if (object == null)
			throw new IllegalArgumentException("Could not find local object: "
					+ handle);
		if (client != null && args != null)
			for (int i = 0; i < args.length; i++) {
				if (args[i] != null && args[i] instanceof ObjectHandle)
					args[i] = lookupHandle((ObjectHandle) args[i], client);
			}
		Method method = findMethod(object, methodName, args);
		if (method == null)
			throw new NoSuchMethodError("Could not find method: " + methodName
					+ " on Object of type " + object.getClass().getName());
		Object rval = method.invoke(object, args);
		Class<?> rtype = method.getReturnType();
		if (rtype.isInterface() && exportedInterfaces.contains(rtype)
				&& rval != null) {
			rval = createHandle(rval, rtype);
		}
		return rval;
	}

	/**
	 * Lookup an object from the published interfaces.
	 * 
	 * @param iface
	 *            the interface to looked up.
	 * @return the object handle
	 */
	public ObjectHandle lookup(Class<?> iface) {
		Object o = publishedInterfaces.get(iface);
		if (o == null)
			return null;
		return createHandle(o, iface);
	}

	/**
	 * Publish an object.
	 * 
	 * @param o
	 *            the object
	 * @param iface
	 *            the interface implemented by the object.
	 * @return the object handle
	 */
	public ObjectHandle publish(Object o, Class<?> iface) {
		if (!iface.isInterface())
			throw new IllegalAccessError(
					"Only objects that implement interfaces can be exported");
		if (!iface.isInstance(o))
			throw new IllegalArgumentException(
					"The object should implement the interface");
		publishedInterfaces.put(iface, o);
		exportedInterfaces.add(iface);
		return createHandle(o, iface);
	}

	/**
	 * Removes the object from the local object repository.
	 * 
	 * When a remote object goes out of scope, a remove request is sent and
	 * handled by this method. If the object is not held by any other object in
	 * the application, the removal from the list should allow the object to be
	 * GC'ed.
	 * 
	 * @param handle
	 *            the handle
	 * @return true, if successful
	 */
	public boolean removeObject(ObjectHandle handle) {
		if (list.contains(handle)) {
			int index = list.indexOf(handle);
			handle = list.get(index);
		} else {
			log.error("A remove request on non existant ObjectHandle" + handle);
			return false ;
		}
		if (handle.release())
			return list.remove(handle);
		return true ;
	}

	private Method findMethod(Object object, String methodName, Object[] args) {
		Class<? extends Object> klass = object.getClass();
		Method[] methods = klass.getMethods();
		for (Method method : methods) {
			if (methodName.equals(method.getName())
					&& paramMatches(method, args))
				return method;
		}
		return null;
	}

	private boolean paramMatches(Method method, Object[] params) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (params != null && parameterTypes.length != params.length
				|| params == null && parameterTypes.length != 0)
			return false;
		for (int i = 0; i < parameterTypes.length; i++) {
			Class<?> class1 = parameterTypes[i];
			if (!class1.isPrimitive() && params[i] == null)
				continue;
			if (params[i] instanceof ObjectHandle) {
				params[i] = getObject(((ObjectHandle) params[i]));
			}
			if (class1.isPrimitive()) {
				if (params[i] instanceof Boolean && class1 != Boolean.TYPE)
					return false;
				if (params[i] instanceof Integer && class1 != Integer.TYPE)
					return false;
				if (params[i] instanceof Long && class1 != Long.TYPE)
					return false;
				if (params[i] instanceof Short && class1 != Short.TYPE)
					return false;
				if (params[i] instanceof Float && class1 != Float.TYPE)
					return false;
				if (params[i] instanceof Double && class1 != Double.TYPE)
					return false;
				if (params[i] instanceof Byte && class1 != Byte.TYPE)
					return false;
			} else if (!class1.isInstance(params[i]))
				return false;
		}
		return true;
	}

}
