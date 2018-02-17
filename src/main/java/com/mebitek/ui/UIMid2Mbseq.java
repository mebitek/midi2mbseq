package com.mebitek.ui;

import com.mebitek.Utils;
import com.mebitek.midi.MIDILine;
import com.mebitek.utils.Filler;
import com.mebitek.utils.MbseqFileWriter;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mebitek.Constants.*;

public class UIMid2Mbseq extends JPanel
		implements ActionListener {
	static private final String newline = "\n";
	JButton openButton, convertButton;
	JPanel buttonPanel;
	JTextArea log;
	JFileChooser fc;
	Filler filler;
	int stepLength;

	private List<File> files;
	private String mbseqFileName;
	TextField stepResField;
	TextField lengthField;

	public UIMid2Mbseq() throws IOException {
		super(new BorderLayout());
		files = new ArrayList<>();
		stepLength = MICROBRUTE_SEQ_LENGTH;

		//Create the log first, because the action listeners
		//need to refer to it.
		log = new JTextArea(5, 20);
		log.setMargin(new Insets(5, 5, 5, 5));
		log.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(log);
		log.append("mid2mbseq converter v" + Utils.getVersion() + newline);
		System.out.println();
		//Create a file chooser
		fc = new JFileChooser();

		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		openButton = new JButton("Open a File...",
				createImageIcon("images/Open16.gif"));
		openButton.addActionListener(this);

		JRadioButton none = new JRadioButton("No Filler");
		none.setMnemonic(KeyEvent.VK_M);
		none.setSelected(true);
		none.setActionCommand("none");

		JRadioButton multiple = new JRadioButton("16 Multiple");
		multiple.setMnemonic(KeyEvent.VK_M);
		multiple.setActionCommand("multiple");

		JRadioButton full = new JRadioButton("Full");
		full.setMnemonic(KeyEvent.VK_F);
		full.setActionCommand("full");

		JRadioButton custom = new JRadioButton("custom");
		custom.setMnemonic(KeyEvent.VK_C);
		custom.setActionCommand("custom");

		//Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(none);
		group.add(multiple);
		group.add(full);
		group.add(custom);

		JCheckBox lengthCheckbox = new JCheckBox("Length");

		lengthCheckbox.addItemListener(e -> {
					if (e.getStateChange() == ItemEvent.DESELECTED) {
						UIUtils.setVisible(lengthField, false);
						stepLength = MICROBRUTE_SEQ_LENGTH;
					} else if (e.getStateChange() == ItemEvent.SELECTED) {
						UIUtils.setVisible(lengthField, true);
						stepLength = MICROBRUTE_SEQ_LENGTH;
						lengthField.setText(Integer.toString(MICROBRUTE_SEQ_LENGTH));
					}
				}
		);

		//Create the save button.  We use the image from the JLF
		//Graphics Repository (but we extracted it from the jar).
		convertButton = new JButton("Convert",
				createImageIcon("images/Convert16.gif"));
		convertButton.addActionListener(this);

		//For layout purposes, put the buttons in a separate panel
		buttonPanel = new JPanel();
		buttonPanel.add(openButton);
		buttonPanel.add(convertButton);
		buttonPanel.add(none);
		buttonPanel.add(full);
		buttonPanel.add(multiple);
		buttonPanel.add(custom);

		//Add the buttons and the log to this panel.
		add(buttonPanel, BorderLayout.PAGE_START);
		add(logScrollPane, BorderLayout.CENTER);

		lengthField = new TextField(Integer.toString(MICROBRUTE_SEQ_LENGTH));
		lengthField.addActionListener(e -> {
			stepLength = Integer.valueOf(e.getActionCommand());
		});
		stepResField = new TextField(Integer.toString(STEP_RES));
		stepResField.addActionListener(e -> {
			filler = new Filler(2, e.getActionCommand());
		});

		UIUtils.setVisible(stepResField, false);
		buttonPanel.add(stepResField);
		buttonPanel.add(lengthCheckbox);
		UIUtils.setVisible(lengthField, false);
		buttonPanel.add(lengthField);

		custom.addActionListener(e -> {
			UIUtils.setVisible(stepResField, true);
			filler = new Filler(2, STEP_RES);

		});
		full.addActionListener(e -> {
			UIUtils.setVisible(stepResField, false);
			filler = new Filler(3, MICROBRUTE_SEQ_LENGTH);
		});

		multiple.addActionListener(e -> {
			UIUtils.setVisible(stepResField, false);
			filler = new Filler(1);
		});

		none.addActionListener(e -> {
			UIUtils.setVisible(stepResField, false);
			filler = null;

		});


	}

	public void actionPerformed(ActionEvent e) {

		//Handle open button action.
		if (e.getSource() == openButton) {
			files = new ArrayList<>();
			fc.addChoosableFileFilter(new MidFilter());
			fc.setAcceptAllFileFilterUsed(false);

			int returnVal = fc.showOpenDialog(UIMid2Mbseq.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File inputFile = fc.getSelectedFile();
				//This is where a real application would open the file.
				log.append("Opening: " + inputFile.getName() + "." + newline);
				String path;
				String filename;
				if (inputFile.isDirectory()) {

					File[] directoryListing = inputFile.listFiles((dir, name) -> name.endsWith(".mid"));

					if (directoryListing != null) {
						files.addAll(Arrays.asList(directoryListing));
					} else {
						NotDirectoryException e1 = new NotDirectoryException(inputFile.getName());
						log.append(e1.getLocalizedMessage());
						return;
					}
					path = inputFile.getPath();
					mbseqFileName = Paths.get(path, inputFile.getName() + ".mbseq").toString();
				} else {
					files.add(inputFile);
					path = inputFile.getParent();
					filename = inputFile.getName();
					mbseqFileName = Paths.get(path, filename.replaceAll(".mid", ".mbseq")).toString();
				}
				log.append("Files: " + files.size() + newline);

			} else {
				log.append("Open command cancelled by user." + newline);
			}
			log.setCaretPosition(log.getDocument().getLength());

			//Handle convert button action.
		} else if (e.getSource() == convertButton) {
			if (mbseqFileName == null) {
				log.append("Select a file");
				return;
			}

			stepLength = Integer.valueOf(lengthField.getText());
			if (stepResField.isVisible()) {
				filler = new Filler(2, stepResField.getText());
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
							MIDILine line = new MIDILine(track, filler, stepLength);
							int seqLines = line.getSeqNumber();
							if (seqLines > 0) {
								log.append("* File " + midiFile.getName() + ": size = " + track.size() + newline);
								log.append("  Line Size: " + line.getSize() + newline);
								log.append("  Seq Lines: " + seqLines + newline);

								int linesNeeded = MICROBRUTE_MAX_SEQ_LINES - (totalSeqNumber + seqLines - 1);
								if (linesNeeded <= 0) {
									seqLines = seqLines + linesNeeded;
									log.append(Math.abs(linesNeeded) + " lines skipped");
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
						log.append(e1.getLocalizedMessage());
					}

				}
				writer.close();
				File outFile = new File(mbseqFileName);
				log.append("Output file: " + outFile.getName() + newline);

				String content = new String(Files.readAllBytes(Paths.get(mbseqFileName)));
				log.append(content + newline);

			} catch (IOException e1) {
				log.append(e1.getLocalizedMessage());
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
