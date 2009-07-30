package com.tzavellas.poolng;

class StopWatch {
	
	private long beginning = 0;
	private long end = 0;
	private String operation;
	
	public StopWatch() {
		operation = "operation";
	}
	
	public StopWatch(String operation) {
		this.operation = operation;
	}
	
	public void start() {
		if (beginning != 0) {
			throw new IllegalStateException("A Stopwatch cannot be re-started!");
		}
		beginning = System.nanoTime();
	}
	
	public void stop() {
		if (beginning == 0) {
			throw new IllegalStateException("You cannot stop a Stopwatch that has not been started!");
		}
		end = System.nanoTime();
	}

	public long duration() {
		if (beginning == 0 || end == 0) {
			throw new IllegalStateException("You must start and then stop a Stopwatch to get the duration!");
		}
		return end - beginning;
	}
	
	@Override
	public String toString() {
		return operation + " took (ns): " + duration();
	}
}
