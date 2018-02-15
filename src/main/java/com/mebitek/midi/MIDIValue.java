package main.java.mebitek.midi;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import java.util.Collections;

/**
 * Helper class that implements the value of the midi message
 * in microbrute pauses char is 'x'. in this class pauses are grouped to fill
 * the microbrute time sequencer signature (1/16th)
 * only note on message are considered valid
 *
 * @author Claudio Melis
 */
class MIDIValue {

	private static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
	private static final int NOTE_ON = 0x90;
	private static final int NOTE_OFF = 0x80;

	private int key;
	private int octave;
	private int note;
	private String noteName;
	private boolean valid = false;
	private long tick;
	private String value;
	private int command;

	/**
	 * Creates a new instance with invalid tick.
	 */
	MIDIValue() {
		tick = -1;
	}

	/**
	 * Creates a new instance from MidiEvent and the prev midi value.
	 *
	 * @param event         a MidiEvent
	 * @param prevMIDIValue a MIDIValue
	 */
	MIDIValue(MidiEvent event, MIDIValue prevMIDIValue) {
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
				value = String.valueOf(key);
				valid = true;
				if (tick != prevMIDIValue.tick) {
					valid = true;
					long diff = tick - prevMIDIValue.tick;
					int pauses = (int) (diff / 240) - 1;
					if (prevMIDIValue.tick == -1) {
						pauses = pauses + 1;
					}
					String x = String.join("", Collections.nCopies(pauses, "x "));
					value = x + value;
				}
			} else if (sm.getCommand() == NOTE_OFF && key == 127) {
				value = "x";
				valid = true;
			}
		}
	}

	/**
	 * Gets the midi value
	 *
	 * @return the midi value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Gets the validity of the midi message
	 *
	 * @return if value is valid
	 */

	public boolean isValid() {
		return valid;
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