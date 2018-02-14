package com.mebitek.midi;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

public class MIDIValue {

	private static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
	private static final int NOTE_ON = 0x90;
	private static final int NOTE_OFF = 0x80;

	private int key;
	private int octave;
	private int note;
	private String noteName ;
	private boolean valid = false;
	private long tick;
	private String value;
	private int command;

	MIDIValue() {
		tick = -1;
	}

	MIDIValue(MidiEvent event) {
		tick = event.getTick();
		MidiMessage message = event.getMessage();
		key = -1;
		octave = 0;
		note = -1;
		noteName = "";
		ShortMessage sm;

		if (message instanceof ShortMessage) {
			sm = (ShortMessage) message;
			this.tick = event.getTick();
			key = sm.getData1();
			octave = (key / 12) - 1;
			note = key % 12;
			noteName = NOTE_NAMES[note];
			command = sm.getCommand();
			if (sm.getCommand() == NOTE_ON) {
				value = String.valueOf(" "+key);
				valid = true;
			} else if (sm.getCommand() == NOTE_OFF && key == 127) {
				value = " x";
				valid = true;
			}
		}
	}

	public String getValue() {
		return value;
	}

	public boolean isValid() {
		return valid;
	}

	public long getTick() {
		return tick;
	}

	@Override
	public String toString() {
		return "MIDIValue{" +
				"key=" + key +
				", octave=" + octave +
				", note=" + note +
				", noteName='" + noteName + '\'' +
				", valid=" + valid +
				", tick=" + tick +
				", value='" + value + '\'' +
				", command=" + command +
				'}';
	}
}
