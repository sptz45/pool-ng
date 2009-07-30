package com.tzavellas.poolng;

import static org.junit.Assert.*;

import java.util.concurrent.Semaphore;

import org.junit.Test;

public class ResizeableSemaphoreTest {
	
	@Test
	public void size_equals_available_plus_acquired_permits() throws Exception {
		ResizeableSemaphore sem = new ResizeableSemaphore(10);
		assertEquals(10, sem.size());
		sem.acquire();
		assertEquals(9, sem.availablePermits());
		assertEquals(10, sem.size());
	}
	
	@Test
	public void a_semaphore_always_can_grow() throws Exception {
		ResizeableSemaphore sem = new ResizeableSemaphore(10);
		assertEquals(10, sem.size());
		sem.acquire();
		sem.resize(20);
		assertEquals(20, sem.size());
	}
	
	@Test
	public void a_semaphore_can_shrink() throws Exception {
		ResizeableSemaphore sem = new ResizeableSemaphore(10);
		assertEquals(10, sem.size());
		sem.acquire();
		sem.resize(5);
		assertEquals(5, sem.size());
	}
	
	@Test
	public void the_semaphore_size_cannot_be_less_than_the_acquired_permits() throws Exception {
		ResizeableSemaphore sem = new ResizeableSemaphore(4);
		assertEquals(4, sem.size());
		sem.acquire();
		sem.acquire();
		sem.resize(1);
		assertEquals(2, sem.size());
	}
	
	@Test
	public void a_semaphore_cannot_get_shrunk_when_all_permits_are_acquired() throws Exception {
		ResizeableSemaphore sem = new ResizeableSemaphore(2);
		assertEquals(2, sem.size());
		sem.acquire();
		sem.acquire();
		sem.resize(1);
		assertEquals(2, sem.size());
	}
	
	@Test
	public void resizing_at_current_size_does_nothing() throws Exception {
		ResizeableSemaphore sem = new ResizeableSemaphore(4);
		assertEquals(4, sem.size());
		sem.resize(4);
		assertEquals(4, sem.size());
	}
	
	@Test
	public void negative_size_does_nothing() throws Exception {
		ResizeableSemaphore sem = new ResizeableSemaphore(4);
		sem.resize(-4);
		assertEquals(4, sem.size());
	}
	
	
	
	// -----------------------------------------------------------------------
	
	/*
	 * Verify that we can increase the number of permits above the
	 * number specified in the constructor, by calling release(int).
	 */
	@Test
	public void verify_expected_jdk_Semaphore_behaviour() {
		Semaphore sem = new Semaphore(10);
		assertEquals(10, sem.availablePermits());
		sem.release(5);
		assertEquals(15, sem.availablePermits());
	}
}
