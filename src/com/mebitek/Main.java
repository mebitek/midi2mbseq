package com.mebitek;

import com.mebitek.midi.MIDILine;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.io.File;
import java.io.IOException;


public class Main {

	private final static int MICROBRUTE_SEQ_LENGTH = 64;
	private final static int MICROBRUTE_MAX_SEQ_LINES = 8;

	public static void main(String[] args) throws InvalidMidiDataException, IOException {

		File midiFile = new File("/home/claudio/Scaricati/midi_maschine.mid");
//		File midiFile = new File("/home/claudio/Scaricati/aeon01.mbseq_1.mid");

		String path = midiFile.getParent();
		String filename = midiFile.getName();
		String mseqFileName = filename.replaceAll(".mid",".mbseq");

		Sequence sequence = MidiSystem.getSequence(midiFile);

		int trackNumber = 0;

		MseqFileWriter writer = new MseqFileWriter(path+"/"+mseqFileName);

		for (Track track : sequence.getTracks()) {
			trackNumber++;
			System.out.println("Track " + trackNumber + ": size = " + track.size());
			System.out.println();


			MIDILine line = new MIDILine(track, 1);
			System.out.println("Line Size: " + line.getSize());
			System.out.println("Seq Size: " + MICROBRUTE_SEQ_LENGTH);
			int seqLines = 1;
			int items = line.getSize();
			if (line.getSize()> MICROBRUTE_SEQ_LENGTH) {
				seqLines = line.getSize() / MICROBRUTE_SEQ_LENGTH;
				items = MICROBRUTE_SEQ_LENGTH;
			}
			System.out.println("Seq Lines: " + seqLines);

			if (seqLines > MICROBRUTE_MAX_SEQ_LINES) {
				seqLines = MICROBRUTE_MAX_SEQ_LINES;
			}

			for (int seqNumber = 1; seqNumber <= seqLines; seqNumber++) {

				if (line.isValid()) {
					writer.initLine(seqNumber);
					writer.print(line.getLine(seqNumber, items));
					writer.println();
				}
			}
		}
		writer.close();
	}
}
