package com.mebitek.ui;

import com.mebitek.midi.MIDILine;
import com.mebitek.utils.MbseqFileWriter;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mebitek.Constants.MICROBRUTE_MAX_SEQ_LINES;

public class UIMid2Mbseq extends JPanel
		implements ActionListener {
	static private final String newline = "\n";
	JButton openButton, convertButton;
	JTextArea log;
	JFileChooser fc;

	List<File> files;

	public UIMid2Mbseq() {
		super(new BorderLayout());
		files = new ArrayList<>();
		//Create the log first, because the action listeners
		//need to refer to it.
		log = new JTextArea(5, 20);
		log.setMargin(new Insets(5, 5, 5, 5));
		log.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(log);

		//Create a file chooser
		fc = new JFileChooser();

		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		//Create the open button.  We use the image from the JLF
		//Graphics Repository (but we extracted it from the jar).
		openButton = new JButton("Open a File...",
				createImageIcon("images/Open16.gif"));
		openButton.addActionListener(this);

		//Create the save button.  We use the image from the JLF
		//Graphics Repository (but we extracted it from the jar).
		convertButton = new JButton("Convert",
				createImageIcon("images/Convert16.gif"));
		convertButton.addActionListener(this);

		//For layout purposes, put the buttons in a separate panel
		JPanel buttonPanel = new JPanel(); //use FlowLayout
		buttonPanel.add(openButton);
		buttonPanel.add(convertButton);

		//Add the buttons and the log to this panel.
		add(buttonPanel, BorderLayout.PAGE_START);
		add(logScrollPane, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e) {

		//Handle open button action.
		if (e.getSource() == openButton) {

			fc.addChoosableFileFilter(new MidFilter());
			fc.setAcceptAllFileFilterUsed(false);

			int returnVal = fc.showOpenDialog(UIMid2Mbseq.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				//This is where a real application would open the file.
				log.append("Opening: " + file.getName() + "." + newline);
			} else {
				log.append("Open command cancelled by user." + newline);
			}
			log.setCaretPosition(log.getDocument().getLength());

			//Handle convert button action.
		} else if (e.getSource() == convertButton) {

			File inputFile = fc.getSelectedFile();
			String path;
			String filename;
			String mbseqFileName;
			if (inputFile.isDirectory()) {

				File[] directoryListing = inputFile.listFiles((dir, name) -> name.endsWith(".mid"));

				if (directoryListing != null) {
					files.addAll(Arrays.asList(directoryListing));
				} else {
					NotDirectoryException e1 = new NotDirectoryException(inputFile.getName());
					e1.printStackTrace();
					return;
				}
				path = inputFile.getPath();
				mbseqFileName = Paths.get(path, inputFile.getName() + ".mbseq").toString();
			} else {
				files.add(inputFile);
				log.append("Files: " + files.size() + newline);
				path = inputFile.getParent();
				filename = inputFile.getName();
				mbseqFileName = Paths.get(path, filename.replaceAll(".mid", ".mbseq")).toString();
			}
			
			MbseqFileWriter writer;
			try {
				writer = new MbseqFileWriter(mbseqFileName);

				int totalSeqNumber = 1;
				for (File midiFile : files) {

					Sequence sequence;
					try {
						sequence = MidiSystem.getSequence(midiFile);

						for (Track track : sequence.getTracks()) {
							MIDILine line = new MIDILine(track, null, 64);
							int seqLines = line.getSeqNumber();
							if (seqLines > 0) {
								log.append("* File " + midiFile.getName() + ": size = " + track.size() + newline);
								log.append("  Line Size: " + line.getSize() + newline);
								log.append("  Seq Lines: " + seqLines + newline);

								int linesNeeded = MICROBRUTE_MAX_SEQ_LINES - (totalSeqNumber + seqLines - 1);
								if (linesNeeded <= 0) {
									seqLines = seqLines + linesNeeded;
									System.out.println(Math.abs(linesNeeded) + " lines skipped");
								}

								for (int seqNumber = 1; seqNumber <= seqLines; seqNumber++) {
									writer.initLine(totalSeqNumber);
									writer.print(line.getLine(seqNumber));
									log.append("  Seq [" + totalSeqNumber + "] size:" + line.getLineSize(seqNumber) + newline);
									writer.println();
									totalSeqNumber = totalSeqNumber + 1;
								}
								log.append(newline);
							}
						}
					} catch (InvalidMidiDataException | IOException e1) {
						e1.printStackTrace();
					}

				}
				writer.close();
				log.append("Output file: " + mbseqFileName+newline);
			} catch (FileNotFoundException | UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			log.setCaretPosition(log.getDocument().getLength());
		}
	}

	/**
	 * Returns an ImageIcon, or null if the path was invalid.
	 */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = UIMid2Mbseq.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}


}
