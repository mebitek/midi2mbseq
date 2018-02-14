package com.mebitek.midi;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.List;

/*

options:
0 -> no filler
1 -> fill to the nearest step multiple
*/


public class MIDILine {

	private static final int STEP_RES = 16;

	private List<String> keys;

	public MIDILine(Track track) {
		this(track, 0);
	}


	public MIDILine(Track track, int option) {
		boolean valid = false;
		keys = new ArrayList<String>();

		MIDIValue prevValue = new MIDIValue();
		for (int i = 0; i < track.size(); i++) {
			MidiEvent event = track.get(i);

			MIDIValue midiValue = new MIDIValue(event);
			if (midiValue.isValid()) {
				if (midiValue.getTick() != prevValue.getTick()) {
					valid = true;
					long diff = midiValue.getTick() - prevValue.getTick();
					int pauses = (int) (diff / 240) - 1;
					if (i == 1) {
						pauses = pauses + 1;
					}

					for (int j = 0; j < pauses; j++) {
						keys.add("x");
					}

					keys.add(midiValue.getValue());
					prevValue = midiValue;
				}
			}
		}

		if (option == 1) {
			if (valid) {
				int size = keys.size();
				int stepSize = (size + STEP_RES - 1) / STEP_RES * STEP_RES;

				for (int i = 0; i < stepSize - size; i++) {
					keys.add("x");
				}
			}
		}
	}

	public int getSize() {
		return keys.size();
	}

	public String getLine(int seqNumber, int items) {
		StringBuilder builder = new StringBuilder();

		int end = (keys.size() * seqNumber);
		int start = end - items;
		for (String key : keys.subList(start, end)) {
			builder.append(key);
			builder.append(" ");
		}
		return builder.toString();
	}

	public boolean isValid() {
		return keys.size() > 0;
	}
}
