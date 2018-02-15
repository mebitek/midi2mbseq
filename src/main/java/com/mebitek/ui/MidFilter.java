package com.mebitek.ui;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class MidFilter extends FileFilter {
	//Accept all directories and all gif, jpg, tiff, or png files.
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		String extension = UIUtils.getExtension(f);
		return extension != null && extension.equals(UIUtils.mid);
	}

	//The description of this filter
	public String getDescription() {
		return "*.mid";
	}
}
