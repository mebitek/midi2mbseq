package com.mebitek;

import com.mebitek.midi.MIDILine;
import com.mebitek.ui.UIMid2Mbseq;
import com.mebitek.utils.Filler;
import com.mebitek.utils.MbseqFileWriter;
import org.apache.commons.cli.*;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mebitek.Constants.MICROBRUTE_MAX_SEQ_LINES;
import static com.mebitek.Constants.MICROBRUTE_SEQ_LENGTH;

/**
 * Arturia Microbrute utility
 * mid file to mbseq file converter
 * <p>
 * Arturia Microbrute page: https://www.arturia.com/products/hardware-synths/microbrute
 * com.mebitek page: http://music.mebitek.com/
 *
 * @author Claudio Melis
 */
class Main {


	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event dispatch thread.
	 */
	private static void createAndShowGUI() throws IOException {
		//Create and set up the window.
		JFrame frame = new JFrame("mid2mbseq "+Utils.getVersion());
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(1100, 600));

		//Add content to the window.
		frame.add(new UIMid2Mbseq());

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) throws InvalidMidiDataException, IOException {

		boolean isGui = true;
		for (String arg : args) {
			if (arg.equals("-b")) {
				isGui = false;
				break;
			}
		}

		if (isGui) {
			SwingUtilities.invokeLater(() -> {
				//Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				try {
					createAndShowGUI();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} else {

			System.out.println("mid2mbseq converter v" + Utils.getVersion());
			System.out.println();
			Options options = new Options();

			Option batch = new Option("b", " batch", false, "batch mode");
			batch.setRequired(false);

			Option input = new Option("i", "input", true, "input file");
			Option directory = new Option("d", "dir", true, "input directory");

			OptionGroup optgrpInput = new OptionGroup();
			optgrpInput.setRequired(true);
			optgrpInput.addOption(input);
			optgrpInput.addOption(directory);
			options.addOptionGroup(optgrpInput);


			Option fillerOption = new Option("m", "multiple-filler", false, "fill to the nearest step multiple");
			fillerOption.setRequired(false);
			Option customFillerOption = new Option("c", "custom-filler", true, "fill to custom step value");
			customFillerOption.setRequired(false);
			Option maxFillerOption = new Option("f", "full-filler", false, "fill to full sequence value (64)");
			customFillerOption.setRequired(false);

			OptionGroup optgrpFiller = new OptionGroup();
			optgrpFiller.setRequired(false);
			optgrpFiller.addOption(fillerOption);
			optgrpFiller.addOption(customFillerOption);
			optgrpFiller.addOption(maxFillerOption);
			options.addOptionGroup(optgrpFiller);

			Option maxOption = new Option("l", "length", true, "set maxium sequence length");
			maxOption.setRequired(false);
			options.addOption(maxOption);

			CommandLineParser parser = new DefaultParser();
			HelpFormatter formatter = new HelpFormatter();
			CommandLine cmd;

			try {
				cmd = parser.parse(options, args);
			} catch (ParseException e) {
				System.out.println(e.getMessage());
				formatter.printHelp("mid2mbseq [-b] [-d|-i] [-f|-m|-c step] [-l length]", options);

				System.exit(1);
				return;
			}

			List<File> files = new ArrayList<>();
			File inputFile;
			String path;
			String filename;
			String mbseqFileName = null;
			if (cmd.hasOption("input")) {
				String inputFilePath = cmd.getOptionValue("input");
				inputFile = new File(inputFilePath);
				files.add(inputFile);
				path = inputFile.getParent();
				filename = inputFile.getName();
				mbseqFileName = Paths.get(path, filename.replaceAll(".mid", ".mbseq")).toString();

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
				mbseqFileName = Paths.get(path, inputFile.getName() + ".mbseq").toString();
			}

			Filler filler = null;
			if (cmd.hasOption("multiple-filler")) {
				filler = new Filler(1);
			}

			if (cmd.hasOption(("custom-filler"))) {
				filler = new Filler(2, cmd.getOptionValue("custom-filler"));

			}

			if (cmd.hasOption(("full-filler"))) {
				filler = new Filler(3, Integer.toString(MICROBRUTE_SEQ_LENGTH));

			}

			int maxStepLength = MICROBRUTE_SEQ_LENGTH;
			if (cmd.hasOption("length")) {
				maxStepLength = Integer.parseInt(cmd.getOptionValue("length"));

			}

			MbseqFileWriter writer = new MbseqFileWriter(mbseqFileName);
			int totalSeqNumber = 1;
			for (File midiFile : files) {

				Sequence sequence = MidiSystem.getSequence(midiFile);

				for (Track track : sequence.getTracks()) {
					MIDILine line = new MIDILine(track, filler, maxStepLength);
					int seqLines = line.getSeqNumber();
					if (seqLines > 0) {
						System.out.println("* File " + midiFile.getName() + ": size = " + track.size());
						System.out.println("  Line Size: " + line.getSize());
						System.out.println("  Seq Lines: " + seqLines);

						int linesNeeded = MICROBRUTE_MAX_SEQ_LINES - (totalSeqNumber + seqLines - 1);
						if (linesNeeded <= 0) {
							seqLines = seqLines + linesNeeded;
							System.out.println(Math.abs(linesNeeded) + " lines skipped");
						}

						for (int seqNumber = 1; seqNumber <= seqLines; seqNumber++) {
							writer.initLine(totalSeqNumber);
							writer.print(line.getLine(seqNumber));
							System.out.println("  Seq [" + totalSeqNumber + "] size:" + line.getLineSize(seqNumber));
							writer.println();
							totalSeqNumber = totalSeqNumber + 1;
						}
						System.out.println();

					}
				}

			}
			writer.close();
			System.out.println("Output file: " + mbseqFileName);
		}
	}
}