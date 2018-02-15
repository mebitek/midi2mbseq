package main.java.mebitek.midi;

import main.java.mebitek.utils.Filler;
import main.java.mebitek.utils.Pageable;
import com.sun.deploy.util.StringUtils;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static main.java.mebitek.Constants.MICROBRUTE_MAX_SEQ_LINES;


/**
 * Helper class that implements a single microbrute sequence
 *
 * @author Claudio Melis
 */
public class MIDILine {

	private List<String> keys;
	private final Pageable<String> pageable;
	private final Filler option;
	private int maxSeqLength;

	/**
	 * Creates a new instance from Midi Track.
	 *
	 * @param track  a Midi Track
	 * @param option a Filler option
	 */
	public MIDILine(Track track, Filler option, int maxSeqLength) {
		keys = new ArrayList<>();
		this.option = option;
		this.maxSeqLength = maxSeqLength;
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
		this.pageable.setPageSize(this.maxSeqLength);
	}

	/**
	 * Optimize the step list and apply the filler if needed
	 */
	private void optimizeKeys() {

		if (keys.size() > 0) {
			keys = new ArrayList<>(Arrays.asList(StringUtils.join(keys, " ").split(" ")));
			int size = keys.size();
			if (option != null) {
				int stepRes = option.getValue();
				if (stepRes <= 0) {
					stepRes = 1;
				}
				if (stepRes > this.maxSeqLength) {
					stepRes = this.maxSeqLength;
				}

				int stepSize = (keys.size() + stepRes - 1) / stepRes * stepRes;
				for (int i = 0; i < stepSize - size; i++) {
					keys.add("x");
				}
			}

		}
	}

	/**
	 * Return the size of the whole step sequence
	 *
	 * @return total steps size
	 */
	public int getSize() {
		return keys.size();
	}

	/**
	 * Return a step sublist at specific sequence number
	 *
	 * @param seqNumber the sequence number
	 * @return sublist steps
	 */
	public String getLine(int seqNumber) {
		pageable.setPage(seqNumber);
		return StringUtils.join(pageable.getListForPage(), " ");
	}

	/**
	 * Return a step sublist at specific sequence number size
	 *
	 * @param seqNumber the sequence number
	 * @return sublist steps size
	 */
	public int getLineSize(int seqNumber) {
		pageable.setPage(seqNumber);
		return pageable.getListForPage().size();
	}

	/**
	 * Return the number of the sequence, max 8
	 *
	 * @return the number of sequences
	 */
	public int getSeqNumber() {
		int seqLines = pageable.getMaxPages();
		if (seqLines > MICROBRUTE_MAX_SEQ_LINES) {
			seqLines = MICROBRUTE_MAX_SEQ_LINES;
		}
		return seqLines;
	}
}