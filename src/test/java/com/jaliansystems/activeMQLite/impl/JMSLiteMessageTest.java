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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class JMSLiteMessageTest.
 */
public class JMSLiteMessageTest {

	/**
	 * The Class JavaBean.
	 */
	public static class JavaBean {
		
		/** The name. */
		private String name;
		
		/** The age. */
		private int age;
		
		/** The male. */
		private boolean male;

		/**
		 * Instantiates a new java bean.
		 */
		public JavaBean() {
		}

		/**
		 * Instantiates a new java bean.
		 *
		 * @param name the name
		 * @param age the age
		 * @param male the male
		 */
		public JavaBean(String name, int age, boolean male) {
			this.name = name;
			this.age = age;
			this.male = male;
		}

		/* (non-Javadoc)
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
			JavaBean other = (JavaBean) obj;
			if (age != other.age)
				return false;
			if (male != other.male)
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		/**
		 * Gets the age.
		 *
		 * @return the age
		 */
		public int getAge() {
			return age;
		}

		/**
		 * Gets the name.
		 *
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + age;
			result = prime * result + (male ? 1231 : 1237);
			result = prime * result + (name == null ? 0 : name.hashCode());
			return result;
		}

		/**
		 * Checks if is male.
		 *
		 * @return true, if is male
		 */
		public boolean isMale() {
			return male;
		}

		/**
		 * Sets the age.
		 *
		 * @param age the new age
		 */
		public void setAge(int age) {
			this.age = age;
		}

		/**
		 * Sets the male.
		 *
		 * @param male the new male
		 */
		public void setMale(boolean male) {
			this.male = male;
		}

		/**
		 * Sets the name.
		 *
		 * @param name the new name
		 */
		public void setName(String name) {
			this.name = name;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "JavaBean [name=" + name + ", age=" + age + ", male=" + male
					+ "]";
		}

	}

	/**
	 * Beans support null values.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void beansSupportNullValues() throws Exception {
		JMSLiteMessage message = new JMSLiteMessage();

		Object expected = new JavaBean(null, 46, true);
		message.write(expected);

		message.makeReadable();

		Object actual = message.read();

		assertEquals(expected, actual);
	}

	/**
	 * Writes and reads arrays of values.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void writesAndReadsArraysOfValues() throws Exception {
		JMSLiteMessage message = new JMSLiteMessage();

		Object[] expected = new Object[] { "Hello", "World", 1, 2, 21.12, true };
		message.write(expected);

		message.makeReadable();

		Object[] actual = (Object[]) message.read();

		assertArrayEquals(expected, actual);
	}

	/**
	 * Writes and reads beans.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void writesAndReadsBeans() throws Exception {
		JMSLiteMessage message = new JMSLiteMessage();

		Object expected = new JavaBean("Dakshinamurthy Karra", 46, true);
		message.write(expected);

		message.makeReadable();

		Object actual = message.read();

		assertEquals(expected, actual);
	}

	/**
	 * Writes and reads boolean.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void writesAndReadsBoolean() throws Exception {
		JMSLiteMessage message = new JMSLiteMessage();
		message.write(true);
		message.write(false);

		message.makeReadable();
		assertEquals("Should read written boolean", true, message.read());
		assertEquals("Should read written boolean", false, message.read());
	}

	/**
	 * Writes and reads double.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void writesAndReadsDouble() throws Exception {
		JMSLiteMessage message = new JMSLiteMessage();
		message.write(1.0);
		message.write(2.0);

		message.makeReadable();
		assertEquals("Should read written double", 1.0, message.read());
		assertEquals("Should read written double", 2.0, message.read());
	}

	/**
	 * Writes and reads floats longs bytes and shorts.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void writesAndReadsFloatsLongsBytesAndShorts() throws Exception {
		JMSLiteMessage message = new JMSLiteMessage();

		Object[] expected = new Object[] { (byte) 65, (float) 122.10,
				(long) 123121, (short) 12 };
		message.write(expected);

		message.makeReadable();

		Object[] actual = (Object[]) message.read();

		assertArrayEquals(expected, actual);
	}

	/**
	 * Writes and reads integers.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void writesAndReadsIntegers() throws Exception {
		JMSLiteMessage message = new JMSLiteMessage();
		message.write(1);
		message.write(2);

		message.makeReadable();
		assertEquals("Should read written integer", 1, message.read());
		assertEquals("Should read written integer", 2, message.read());
	}

	/**
	 * Writes and reads string.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void writesAndReadsString() throws Exception {
		JMSLiteMessage message = new JMSLiteMessage();
		message.write("Hello");
		message.write("World");

		message.makeReadable();
		assertEquals("Should read written string", "Hello", message.read());
		assertEquals("Should read written string", "World", message.read());
	}
}
