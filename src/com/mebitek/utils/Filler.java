package com.mebitek.utils;

import static com.mebitek.Constants.STEP_RES;

public class Filler {

	public Filler(int type, String value) {
		this(type);
		this.value = Integer.parseInt(value);
	}

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
