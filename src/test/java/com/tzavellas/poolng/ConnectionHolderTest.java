package com.tzavellas.poolng;

import static org.junit.Assert.*;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;


public class ConnectionHolderTest {
	
	@Test
	public void idle_connection_calculation() throws InterruptedException {
		ConnectionHolder holder = new ConnectionHolder(null);
		assertFalse("Cannot be idle since idle timeout is 10mins!",
					holder.hasIdleConnection(Duration.minutes(10)));
		
		Thread.sleep(2);
		assertTrue("Must be idle since idle timeout is ", holder.hasIdleConnection(Duration.micros((1))));
	}

	@Test
	public void when_a_connection_is_borrowed_is_never_idle() throws InterruptedException {
		ConnectionHolder holder = new ConnectionHolder(null);
		holder.setBorrowed();
		Thread.sleep(2);
		assertFalse(holder.hasIdleConnection(Duration.millis(1)));
		
		holder.setReturned();
		Thread.sleep(2);
		assertTrue(holder.hasIdleConnection(Duration.millis(1)));
	}
	
	@Test
	public void comparator_sorts_connections_with_older_first() throws InterruptedException {
		ConnectionHolder h1 = new ConnectionHolder(null);
		Thread.sleep(2);
		ConnectionHolder h2 = new ConnectionHolder(null);
		assertEquals(-1, ConnectionHolder.CREATION_TIME_COMPARATOR.compare(h1, h2));
		assertEquals(1, ConnectionHolder.CREATION_TIME_COMPARATOR.compare(h2, h1));
		assertEquals(0, ConnectionHolder.CREATION_TIME_COMPARATOR.compare(h1, h1));
		
		Set<ConnectionHolder> olderFirst = new TreeSet<ConnectionHolder>(ConnectionHolder.CREATION_TIME_COMPARATOR);
		olderFirst.add(h1);
		olderFirst.add(h2);
		assertEquals(h1, olderFirst.iterator().next());
	}
}
