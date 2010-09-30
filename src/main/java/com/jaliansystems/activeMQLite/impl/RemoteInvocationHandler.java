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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Proxy invocation handler that wraps a remote object.
 * 
 * When a remote object is accessed a Proxy object is returned with an instance
 * of RemoteInvocationHandler. All the method invocations on the proxy instance
 * are seamlessly transferred to the remote connection.
 */
public class RemoteInvocationHandler implements InvocationHandler, IProxy {

	private static final Log log = LogFactory
			.getLog(RemoteInvocationHandler.class);

	private final ObjectHandle handle;
	private final RepositoryClient client;

	/**
	 * Instantiates a new remote invocation handler.
	 * 
	 * @param handle
	 *            the handle
	 * @param client
	 *            the client
	 */
	public RemoteInvocationHandler(ObjectHandle handle, RepositoryClient client) {
		this.handle = handle;
		this.client = client;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaliansystems.jmslite.impl.IProxy#getHandle()
	 */
	public ObjectHandle getHandle() {
		return handle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
	 * java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if (method.getName().equals("getHandle"))
			return getHandle();
		log.trace("RemoteInvocationHandler: invoking '" + method.getName()
				+ "' on " + handle);
		return client.invoke(handle, method, args);
	}

	/**
	 * When the object instance goes out of scope, at sometime JVM GC's this
	 * object. We send a remove message to the remote connection. 
	 */
	@Override
	protected void finalize() throws Throwable {
		client.remove(handle);
		super.finalize();
	}
}
