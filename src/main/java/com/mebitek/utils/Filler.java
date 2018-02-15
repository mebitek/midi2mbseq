package com.mebitek.utils;

import static com.mebitek.Constants.STEP_RES;

/**
 * Helper class that implements filler option
 *
 * @author Claudio Melis
 */
public class Filler {

	/**
	 * Creates a new instance
	 *
	 * @param type  the filler type
	 * @param value the step resolution
	 */
	public Filler(int type, String value) {
		this(type);
		this.value = Integer.parseInt(value);
	}

	public Filler(int type, int value) {
		this(type);
		this.value = value;
	}


	/**
	 * Creates a new instance with default step resolution
	 *
	 * @param type the filler type
	 */
	public Filler(int type) {
		this.type = type;
		this.value = STEP_RES;
	}

	private final int type;
	private int value;

	@SuppressWarnings("unused")
	public int getType() {
		return type;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Filler{" +
				"type=" + type +
				", value=" + value +
				'}';
	}
}