package com.mebitek;

import com.mebitek.midi.MIDILine;
import org.apache.commons.cli.*;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.io.File;
import java.io.IOException;

import static com.mebitek.Constants.MICROBRUTE_MAX_SEQ_LINES;
import static com.mebitek.Constants.MICROBRUTE_SEQ_LENGTH;


public class Main {

	public static void main(String[] args) throws InvalidMidiDataException, IOException {

		int fillerType = 0;
		Options options = new Options();

		Option input = new Option("i", "input", true, "input file");
		input.setRequired(true);
		options.addOption(input);
		Option filler = new Option("f", "multiple-filler", false, "fill to the nearest step multiple");
		filler.setRequired(false);
		options.addOption(filler);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("mid2mbseq", options);

			System.exit(1);
			return;
		}

		String inputFilePath = cmd.getOptionValue("input");
		if (cmd.hasOption("multiple-filler")) {
			fillerType = 1;
		}
		File midiFile = new File(inputFilePath);

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

			MIDILine line = new MIDILine(track, fillerType);

			System.out.println("Line Size: " + line.getSize());
			System.out.println("Seq Size: " + MICROBRUTE_SEQ_LENGTH);
			int seqLines = line.getSeqNumber();

			if (seqLines > MICROBRUTE_MAX_SEQ_LINES) {
				seqLines = MICROBRUTE_MAX_SEQ_LINES;
			}
			System.out.println("Seq Lines: " + seqLines);

			for (int seqNumber = 1; seqNumber <= seqLines; seqNumber++) {

				if (line.isValid()) {
					writer.initLine(seqNumber);
					writer.print(line.getLine(seqNumber));
					writer.println();
				}
			}
		}
		writer.close();
	}
}
