package com.mebitek;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utils {

	public static String getVersion() throws IOException {
		InputStream resourceAsStream =
				Main.class.getClass().getResourceAsStream(
						"/version.properties"
				);
		Properties prop = new Properties();

		prop.load(resourceAsStream);

		return prop.getProperty("version");

	}
}
