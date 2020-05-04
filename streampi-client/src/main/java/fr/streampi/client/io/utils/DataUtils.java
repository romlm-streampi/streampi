package fr.streampi.client.io.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public final class DataUtils {

	private DataUtils() {
	}

	public static final File parentFile = new File(
			"/" + System.getProperty("user.home") + File.separator + ".streampi/");
	public static final File iconsFolder = new File(parentFile, "icons/");
	public static final File zippedIcons = new File(iconsFolder, "icons.zip");
	private static final File properties = new File(parentFile, "streampi-client.properties");

	private static final String DEFAULT_SERVER_ADDRESS = "localhost";
	private static final int DEFAULT_SERVER_PORT = 9293;
	private static final int DEFAULT_SERVER_DATA_PORT = 9393;

	public static final String SERVER_ADDRESS_KEY = "server.ip";
	public static final String SERVER_PORT_KEY = "server.port";
	public static final String SERVER_DATA_PORT_KEY = "server.data.port";

	private static Properties props;

	static {
		if (!parentFile.exists())
			parentFile.mkdir();
		if (!iconsFolder.exists())
			iconsFolder.mkdir();
		if (!properties.exists()) {
			try {
				properties.createNewFile();
				props = new Properties();
				props.load(new FileInputStream(properties));
				boolean isChanged = false;
				if (!props.containsKey(SERVER_ADDRESS_KEY)) {
					isChanged = true;
					props.put(SERVER_ADDRESS_KEY, DEFAULT_SERVER_ADDRESS);
				}
				if (!props.containsKey(SERVER_PORT_KEY)) {
					isChanged = true;
					props.put(SERVER_PORT_KEY, DEFAULT_SERVER_PORT);
				}
				if (!props.containsKey(SERVER_DATA_PORT_KEY)) {
					isChanged = true;
					props.put(SERVER_DATA_PORT_KEY, DEFAULT_SERVER_DATA_PORT);
				}
				if (isChanged)
					saveProperties();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static Properties getProperties() {
		return props;
	}

	public static void saveProperties() {
		try (FileOutputStream writer = new FileOutputStream(properties);) {
			props.store(writer, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
