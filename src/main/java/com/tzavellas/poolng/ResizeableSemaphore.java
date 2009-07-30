package com.tzavellas.poolng;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

class ResizeableSemaphore extends Semaphore {
	private static final long serialVersionUID = 1L;
	
	private volatile int size;

	public ResizeableSemaphore(int permits) {
		this(permits, false);
	}
	
	public ResizeableSemaphore(int permits, boolean fair) {
		super(permits, fair);
		size = permits;
	}
	
	public int size() {
		return size;
	}

	public synchronized int resize(int newSize) {
		if (newSize < 0) return size;
		if (newSize > size) {
			release(newSize - size);
			size = newSize;
		} else if (newSize < size) {
			try {
				int acquired = size - newSize;
				for (; acquired != 0 && ! tryAcquire(acquired, 20, TimeUnit.MILLISECONDS); acquired--);
				size = size - acquired;
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		return size;
	}
}
