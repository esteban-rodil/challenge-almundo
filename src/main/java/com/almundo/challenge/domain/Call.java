package com.almundo.challenge.domain;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class representing the call.
 */
public class Call {

	private static final Logger logger = LoggerFactory.getLogger(Call.class);

	private static final int MIN_DURATION = 5000;

	private static final int MAX_DURATION = 10000;

	private static final Random random = new Random();

	private final String label;

	private CallState state = CallState.WAITING;

	public Call(String label) {
		this.label = label;
	}

	/**
	 * Waits a random amount of time between {@link #MIN_DURATION} and {@link #MAX_DURATION}.
	 */
	public void handle() {
		try {
			state = CallState.BEING_HANDLED;
			long duration = duration();
			logger.debug("Call {} - STARTED - Duration: {}", this, duration);
			Thread.sleep(duration);
			logger.debug("Call {} - FINISHED", this);
			state = CallState.FINISHED;
		} catch (InterruptedException e) {
			logger.error("There was an error waiting for call {} to finish.", this);
		}
	}

	@Override
	public String toString() {
		return label;
	}

	/**
	 * The state in which the call is.
	 *
	 * @return the state in which the call is.
	 */
	public CallState getState() {
		return state;
	}

	/**
	 * Calculates the duration of the call.
	 *
	 * @return the duration which will have the call.
	 */
	private long duration() {
		return random.nextInt(MAX_DURATION - MIN_DURATION) + MIN_DURATION;
	}

	public static enum CallState {
		WAITING, BEING_HANDLED, FINISHED
	}
}
