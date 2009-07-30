package com.tzavellas.poolng;

import static org.junit.Assert.*;

import org.junit.Test;


public class ObjectsTest {

	@Test
	public void nullSafeEquals() {
		assertTrue(Objects.nullSafeEquals(null, null));
		assertFalse(Objects.nullSafeEquals(null, "a string"));
		assertFalse(Objects.nullSafeEquals("a string", null));
		assertTrue(Objects.nullSafeEquals("a string", "a string"));
	}
	
	@Test
	public void equalsUsingFields() {
		TestClass t1 = new TestClass(5, "hello");
		assertTrue(Objects.equalsUsingFields(t1, t1));
		
		TestClass t2 = new TestClass(5, "hello");
		assertTrue(Objects.equalsUsingFields(t1, t2));
		assertTrue(Objects.equalsUsingFields(t2, t1));
		
		TestClass t3 = new TestClass(6, "goodbye");
		assertFalse(Objects.equalsUsingFields(t1, t3));
		assertFalse(Objects.equalsUsingFields(t3, t1));
		
		TestClass2 t4 = new TestClass2(3L);
		assertFalse(Objects.equalsUsingFields(t1, t4));
	}
}

class TestClass {
	@SuppressWarnings("unused")
	private int i;
	String s;
	
	TestClass(int i, String s) { this.i = i; this.s = s; }	
}

class TestClass2 {
	long l;
	TestClass2(long l) { this.l = l; }
}