package com.mebitek.ui;

import java.awt.*;
import java.io.File;

/* Utils.java is used by FileChooserDemo2.java. */
public class UIUtils {
	public final static String mid = "mid";

	/*
	 * Get the extension of a file.
	 */
	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 &&  i < s.length() - 1) {
			ext = s.substring(i+1).toLowerCase();
		}
		return ext;
	}

	public static void setVisible(Component component, boolean visible) {
		component.setVisible(visible);
		component.revalidate();
		component.repaint();
	}
}

