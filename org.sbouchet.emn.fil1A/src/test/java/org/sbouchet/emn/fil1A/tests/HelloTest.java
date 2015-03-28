package org.sbouchet.emn.fil1A.tests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.sbouchet.emn.fil1A.Hello;

public class HelloTest {

	@Test
	public void testGreetings() {
		assertEquals("Hello, World !", new Hello().greetings());
	}

}
