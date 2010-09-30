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

import javax.jms.JMSException;

import org.apache.activemq.command.ActiveMQBytesMessage;

/**
 * JMSLiteMessage is on the wire representation of Java objects.
 * 
 * Each message contains a byte representing the type of Object and the value of
 * the object. ObjectHandles represent remote(or local) objects that implemented
 * exported interfaces.
 */
public class JMSLiteMessage extends ActiveMQBytesMessage {

	/** The Constant INTEGER. */
	private static final byte INTEGER = 1;

	/** The Constant BOOLEAN. */
	private static final byte BOOLEAN = 2;

	/** The Constant DOUBLE. */
	private static final byte DOUBLE = 3;

	/** The Constant STRING. */
	private static final byte STRING = 4;

	/** The Constant ARRAY. */
	private static final byte ARRAY = 5;

	/** The Constant FLOAT. */
	private static final byte FLOAT = 6;

	/** The Constant LONG. */
	private static final byte LONG = 7;

	/** The Constant SHORT. */
	private static final byte SHORT = 8;

	/** The Constant BYTE. */
	private static final byte BYTE = 9;

	/** The Constant OBJECT. */
	private static final byte OBJECT = 10;

	/** The Constant NULL. */
	private static final byte NULL = 11;

	/** The Constant OBJECTHANDLE. */
	private static final byte OBJECTHANDLE = 12;

	/**
	 * Instantiates a new jMS lite message.
	 */
	public JMSLiteMessage() {
	}

	/**
	 * Instantiates a new JMS lite message.
	 * 
	 * @param message
	 *            the message
	 * @throws Exception
	 *             the exception
	 */
	public JMSLiteMessage(ActiveMQBytesMessage message) throws Exception {
		setContent(message.getContent());
		setJMSCorrelationID(message.getJMSCorrelationID());
		setJMSReplyTo(message.getJMSReplyTo());
		reset();
	}

	/**
	 * Make readable. For testing purposes.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void makeReadable() throws Exception {
		reset();
	}

	/**
	 * Read.
	 * 
	 * @return the object
	 * @throws Exception
	 *             the exception
	 */
	public Object read() throws Exception {
		byte type = readByte();
		switch (type) {
		case NULL:
			return null;
		case INTEGER:
			return readInt();
		case BYTE:
			return readByte();
		case LONG:
			return readLong();
		case SHORT:
			return readShort();
		case BOOLEAN:
			return readBoolean();
		case DOUBLE:
			return readDouble();
		case FLOAT:
			return readFloat();
		case STRING:
			return readUTF();
		case OBJECTHANDLE:
			ObjectHandle handle = new ObjectHandle();
			handle.decode(this);
			return handle;
		case OBJECT:
			return decodeObject();
		case ARRAY:
			return decodeArray();
		default:
			throw new IllegalArgumentException(
					"Unknown type while reading message: " + type);
		}
	}

	/**
	 * Write.
	 * 
	 * @param value
	 *            the value
	 * @throws Exception
	 *             the exception
	 */
	public void write(Object value) throws Exception {
		if (value == null) {
			writeByte(NULL);
		} else if (value instanceof Integer) {
			writeByte(INTEGER);
			writeInt(((Integer) value).intValue());
		} else if (value instanceof Byte) {
			writeByte(BYTE);
			writeByte(((Byte) value).byteValue());
		} else if (value instanceof Long) {
			writeByte(LONG);
			writeLong(((Long) value).longValue());
		} else if (value instanceof Short) {
			writeByte(SHORT);
			writeShort(((Short) value).shortValue());
		} else if (value instanceof Boolean) {
			writeByte(BOOLEAN);
			writeBoolean(((Boolean) value).booleanValue());
		} else if (value instanceof Double) {
			writeByte(DOUBLE);
			writeDouble(((Double) value).doubleValue());
		} else if (value instanceof Float) {
			writeByte(FLOAT);
			writeFloat(((Float) value).floatValue());
		} else if (value instanceof String) {
			writeByte(STRING);
			writeUTF((String) value);
		} else if (value instanceof ObjectHandle) {
			writeByte(OBJECTHANDLE);
			((ObjectHandle) value).encode(this);
		} else if (value instanceof IProxy) {
			writeByte(OBJECTHANDLE);
			(((IProxy) value).getHandle()).encode(this);
		} else if (value.getClass().isArray()) {
			writeByte(ARRAY);
			Object[] a = (Object[]) value;
			writeInt(a.length);
			for (Object object : a) {
				write(object);
			}
		} else {
			encodeObject(value);
		}
	}

	/**
	 * Decode array.
	 * 
	 * @return the object
	 * @throws JMSException
	 *             the jMS exception
	 * @throws Exception
	 *             the exception
	 */
	private Object decodeArray() throws JMSException, Exception {
		int len = readInt();
		Object[] a = new Object[len];
		for (int i = 0; i < len; i++)
			a[i] = read();
		return a;
	}

	/**
	 * Decode object.
	 * 
	 * @return the object
	 * @throws Exception
	 *             the exception
	 */
	private Object decodeObject() throws Exception {
		String className = readUTF();
		Class<?> klass = Class.forName(className);
		Object instance = klass.newInstance();
		int nGetters = readInt();
		for (int i = 0; i < nGetters; i++) {
			String methodName = readUTF();
			Object value = read();
			Method method = findSetter(klass, methodName);
			if (method == null)
				continue;
			method.invoke(instance, value);
		}
		return instance;
	}

	/**
	 * Encode getter value.
	 * 
	 * @param getValue
	 *            the get value
	 * @param name
	 *            the name
	 * @throws Exception
	 *             the exception
	 */
	private void encodeGetterValue(Object getValue, String name)
			throws Exception {
		writeUTF(name.startsWith("get") ? name.substring(3) : name.substring(2));
		write(getValue);
	}

	/**
	 * Encode object.
	 * 
	 * @param value
	 *            the value
	 * @throws Exception
	 *             the exception
	 */
	private void encodeObject(Object value) throws Exception {
		writeByte(OBJECT);
		writeUTF(value.getClass().getName());
		Method[] methods = value.getClass().getDeclaredMethods();
		int nGetters = 0;
		for (Method method : methods) {
			if (!method.getName().startsWith("get")
					&& !method.getName().startsWith("is")
					|| method.getParameterTypes().length != 0)
				continue;
			nGetters++;
		}
		writeInt(nGetters);
		for (Method method : methods) {
			if (!method.getName().startsWith("get")
					&& !method.getName().startsWith("is")
					|| method.getParameterTypes().length != 0)
				continue;
			Object getValue = method.invoke(value);
			encodeGetterValue(getValue, method.getName());
		}
	}

	/**
	 * Find setter.
	 * 
	 * @param klass
	 *            the klass
	 * @param methodName
	 *            the method name
	 * @return the method
	 */
	private Method findSetter(Class<?> klass, String methodName) {
		Method[] methods = klass.getMethods();
		for (Method method : methods) {
			if (method.getName().equals("set" + methodName)
					&& method.getParameterTypes().length == 1)
				return method;
		}
		return null;
	}
}
