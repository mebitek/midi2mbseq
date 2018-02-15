package com.mebitek.midi;

import com.mebitek.utils.Filler;
import com.mebitek.utils.Pageable;
import com.sun.deploy.util.StringUtils;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mebitek.Constants.MICROBRUTE_MAX_SEQ_LINES;
import static com.mebitek.Constants.MICROBRUTE_SEQ_LENGTH;

/*

options:
0 -> no filler
1 -> fill to the nearest step multiple
*/


public class MIDILine {

	private List<String> keys;
	private final Pageable<String> pageable;
	private final Filler option;

	public MIDILine(Track track, Filler option) {
		keys = new ArrayList<>();
		this.option = option;
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
		this.pageable.setPageSize(MICROBRUTE_SEQ_LENGTH);
	}

	private void optimizeKeys() {

		if (keys.size() > 0) {
			keys = new ArrayList<>(Arrays.asList(StringUtils.join(keys, " ").split(" ")));
			int size = keys.size();
			if (option != null) {
				int stepRes = option.getValue();
				if (stepRes<=0) {
					stepRes=1;
				}
				if (stepRes > MICROBRUTE_SEQ_LENGTH) {
					stepRes = MICROBRUTE_SEQ_LENGTH;
				}

				int stepSize = (keys.size() + stepRes - 1) / stepRes * stepRes;
				for (int i = 0; i < stepSize - size; i++) {
					keys.add("x");
				}
			}

		}
	}

	public int getSize() {
		return keys.size();
	}

	public String getLine(int seqNumber) {
		pageable.setPage(seqNumber);
		return StringUtils.join(pageable.getListForPage(), " ");
	}


	public int getSeqNumber() {
		int seqLines = pageable.getMaxPages();
		if (seqLines > MICROBRUTE_MAX_SEQ_LINES) {
			seqLines = MICROBRUTE_MAX_SEQ_LINES;
		}
		return seqLines;
	}

}
