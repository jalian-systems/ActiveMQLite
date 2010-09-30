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

// TODO: Auto-generated Javadoc
/**
 * The Class Client.
 */
public class Client extends JMSConnection {

	/**
	 * Instantiates a new client.
	 *
	 * @param host the host
	 * @param port the port
	 * @throws Exception the exception
	 */
	public Client(String host, int port) throws Exception {
		super("client", "tcp://" + host + ":" + port);
	}

	/**
	 * Lookup.
	 *
	 * @param iface the iface
	 * @return the object
	 * @throws Exception the exception
	 */
	public Object lookup(Class<?> iface) throws Exception {
		return lookup("server", iface);
	}

}
