package com.mebitek;

import com.mebitek.midi.MIDILine;
import com.mebitek.utils.Filler;
import org.apache.commons.cli.*;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.io.File;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mebitek.Constants.MICROBRUTE_SEQ_LENGTH;


class Main {

	public static void main(String[] args) throws InvalidMidiDataException, IOException {

		Options options = new Options();

		Option input = new Option("i", "input", true, "input file");
		Option directory = new Option("d", "dir", true, "input directory");

		OptionGroup optgrpInput = new OptionGroup();
		optgrpInput.setRequired(true);
		optgrpInput.addOption(input);
		optgrpInput.addOption(directory);
		options.addOptionGroup(optgrpInput);


		Option fillerOption = new Option("f", "multiple-filler", false, "fill to the nearest step multiple");
		fillerOption.setRequired(false);
		Option customFillerOption = new Option("c", "custom-filler", true, "fill to custom step value");
		customFillerOption.setRequired(false);

		OptionGroup optgrpFiller = new OptionGroup();
		optgrpFiller.setRequired(false);
		optgrpFiller.addOption(fillerOption);
		optgrpFiller.addOption(customFillerOption);
		options.addOptionGroup(optgrpFiller);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("mid2mbseq [-d|-i] [-f|-c step]", options);

			System.exit(1);
			return;
		}

		List<File> files = new ArrayList<>();
		File inputFile;
		String path;
		String filename;
		String mseqFileName = null;
		if (cmd.hasOption("input")) {
			String inputFilePath = cmd.getOptionValue("input");
			inputFile = new File(inputFilePath);
			files.add(inputFile);
			path = inputFile.getParent();
			filename = inputFile.getName();
			mseqFileName = Paths.get(path, filename.replaceAll(".mid", ".mbseq")).toString();

		} else if (cmd.hasOption("dir")) {
			String inputDirectoryPath = cmd.getOptionValue("dir");
			inputFile = new File(inputDirectoryPath);

			File[] directoryListing = inputFile.listFiles((dir, name) -> name.endsWith(".mid"));

			if (directoryListing != null) {
				files.addAll(Arrays.asList(directoryListing));
			} else {
				throw new NotDirectoryException(inputFile.getName());
			}
			path = inputFile.getPath();
			mseqFileName = Paths.get(path, inputFile.getName() + ".mbseq").toString();
		}


		Filler filler = null;
		if (cmd.hasOption("multiple-filler")) {
			filler = new Filler(1);
		}

		if (cmd.hasOption(("custom-filler"))) {
			filler = new Filler(2, cmd.getOptionValue("custom-filler"));

		}

		System.out.println("Files: " + files.size());

		MseqFileWriter writer = new MseqFileWriter(mseqFileName);
		int totalSeqNumber = 1;
		for (File midiFile : files) {

			Sequence sequence = MidiSystem.getSequence(midiFile);

			int trackNumber = 0;

			for (Track track : sequence.getTracks()) {
				trackNumber++;
				MIDILine line = new MIDILine(track, filler);
				int seqLines = line.getSeqNumber();
				if (seqLines > 0) {
					System.out.println("Track " + trackNumber + ": size = " + track.size());
					System.out.println();
					System.out.println("Line Size: " + line.getSize());
					System.out.println("Seq Lines: " + seqLines);
					System.out.println("Seq Size: " + MICROBRUTE_SEQ_LENGTH);

					for (int seqNumber = 1; seqNumber <= seqLines; seqNumber++) {
						writer.initLine(totalSeqNumber);
						writer.print(line.getLine(seqNumber));
						writer.println();
						totalSeqNumber = totalSeqNumber + 1;
					}
				}
			}

		}
		writer.close();
	}
}
