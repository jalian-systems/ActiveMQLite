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

/**
 * Object handle represents a exported object on wire
 */
/**
 * @author marathon
 *
 */
public class ObjectHandle {

	private int id;
	private Object object;
	private String brokerURL;
	private Class<?> iface;
	private String queueName;
	private boolean local = false;
	private int retainCount;

	/**
	 * Instantiates a new object handle.
	 */
	public ObjectHandle() {
	}

	/**
	 * Instantiates a new object handle.
	 *
	 * @param id the unique identifier that identifies this object
	 * @param object the object
	 * @param iface the interface implemented by the object
	 * @param brokerURL the broker url
	 * @param queueName the name of the queue
	 */
	public ObjectHandle(int id, Object object, Class<?> iface,
			String brokerURL, String queueName) {
		this.id = id;
		this.object = object;
		this.brokerURL = brokerURL;
		this.queueName = queueName;
		local = true;
		this.iface = iface;
	}

	/**
	 * Decode an on the wire object handle
	 *
	 * @param message the message
	 * @throws Exception the exception
	 */
	public void decode(JMSLiteMessage message) throws Exception {
		id = (Integer) message.read();
		brokerURL = (String) message.read();
		queueName = (String) message.read();
		iface = Class.forName((String) message.read());
	}

	/**
	 * Encode an object handle into a message
	 *
	 * @param message the message
	 * @throws Exception the exception
	 */
	public void encode(JMSLiteMessage message) throws Exception {
		message.write(id);
		message.write(brokerURL);
		message.write(queueName);
		message.write(iface.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ObjectHandle other = (ObjectHandle) obj;
		if (brokerURL == null) {
			if (other.brokerURL != null)
				return false;
		} else if (!brokerURL.equals(other.brokerURL))
			return false;
		if (id != other.id)
			return false;
		if (iface == null) {
			if (other.iface != null)
				return false;
		} else if (!iface.getName().equals(other.iface.getName()))
			return false;
		if (queueName == null) {
			if (other.queueName != null)
				return false;
		} else if (!queueName.equals(other.queueName))
			return false;
		return true;
	}

	/**
	 * Get the ID
	 *
	 * @return the iD
	 */
	public int getID() {
		return id;
	}

	/**
	 * Gets the interface
	 *
	 * @return the interface
	 */
	public Class<?> getIFace() {
		return iface;
	}

	/**
	 * Gets the object.
	 *
	 * @return the object
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * Gets the queue name.
	 *
	 * @return the queue name
	 */
	public String getQueueName() {
		return queueName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (brokerURL == null ? 0 : brokerURL.hashCode());
		result = prime * result + id;
		result = prime * result + (iface == null ? 0 : iface.hashCode());
		result = prime * result
				+ (queueName == null ? 0 : queueName.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ObjectHandle [id=" + id + ", brokerURL=" + brokerURL
				+ ", iface=" + iface.getName() + ", queueName=" + queueName
				+ ", local=" + local + "]";
	}

	/**
	 * Increase the retain count
	 */
	public void retain() {
		this.retainCount++ ;
	}

	/**
	 * Decrease the retain count.
	 * 
	 * @return true if the handle should be released
	 */
	public boolean release() {
		return --retainCount == 0 ;
	}
}
