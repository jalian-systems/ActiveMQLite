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

// TODO: Auto-generated Javadoc
/**
 * The Interface IObjectRepositoryTest.
 */
public interface IObjectRepositoryTest {

	/**
	 * Gets the greeting.
	 *
	 * @return the greeting
	 */
	public abstract String getGreeting();

	/**
	 * Gets the greeting.
	 *
	 * @param i the i
	 * @return the greeting
	 */
	public abstract String getGreeting(int i);

	/**
	 * Take this.
	 *
	 * @param localObjectRepositoryTest the local object repository test
	 * @return the object
	 */
	public abstract Object takeThis(
			IObjectRepositoryTest localObjectRepositoryTest);

	/**
	 * Take this.
	 *
	 * @param localObjectRepository the local object repository
	 * @return the object
	 */
	public abstract Object takeThis(ObjectRepository localObjectRepository);

}
