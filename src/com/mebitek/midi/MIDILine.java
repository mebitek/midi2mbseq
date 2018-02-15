package com.mebitek.midi;

import com.mebitek.Pageable;
import com.sun.deploy.util.StringUtils;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mebitek.Constants.MICROBRUTE_SEQ_LENGTH;
import static com.mebitek.Constants.STEP_RES;

/*

options:
0 -> no filler
1 -> fill to the nearest step multiple
*/


public class MIDILine {

	private List<String> keys;
	private Pageable<String> pageable;

	public MIDILine(Track track, int option) {
		keys = new ArrayList<>();

		MIDIValue prevValue = new MIDIValue();
		for (int i = 0; i < track.size(); i++) {
			MidiEvent event = track.get(i);

			MIDIValue midiValue = new MIDIValue(event, prevValue);
			if (midiValue.isValid()) {
				prevValue = midiValue;
				keys.add(midiValue.getValue());
			}
		}

		optimizeKeys();
		this.pageable = new Pageable<>(keys);

		if (option == 1) {
			int size = keys.size();
			if (size<MICROBRUTE_SEQ_LENGTH) {
				int stepSize = (size + STEP_RES - 1) / STEP_RES * STEP_RES;
				for (int i = 0; i < stepSize - size; i++) {
					keys.add("x");
				}
			}
		}
	}

	private void optimizeKeys() {
		if (keys.size()>0) {
			keys = new ArrayList<>(Arrays.asList(StringUtils.join(keys, " ").split(" ")));
		}
	}

	public int getSize() {
		return keys.size();
	}

	public String getLine(int seqNumber) {

		pageable.setPageSize(MICROBRUTE_SEQ_LENGTH);
		pageable.setPage(seqNumber);

		return StringUtils.join(pageable.getListForPage(), " ");
	}


	public int getSeqNumber() {
		return pageable.getMaxPages();
	}

	public boolean isValid() {
		return keys.size() > 0;
	}
}
