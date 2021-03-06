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
	public static final File iconsFolder = new File(parentFile, "icons-client/");
	public static final File zippedIcons = new File(iconsFolder, "icons-client.zip");
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		props = new Properties();
		try {
			props.load(new FileInputStream(properties));
		} catch (IOException e) {
			e.printStackTrace();
		}
		boolean isChanged = false;
		if (!props.containsKey(SERVER_ADDRESS_KEY)) {
			isChanged = true;
			props.put(SERVER_ADDRESS_KEY, DEFAULT_SERVER_ADDRESS);
		}
		if (!props.containsKey(SERVER_PORT_KEY)) {
			isChanged = true;
			props.put(SERVER_PORT_KEY, String.valueOf(DEFAULT_SERVER_PORT));
		}
		if (!props.containsKey(SERVER_DATA_PORT_KEY)) {
			isChanged = true;
			props.put(SERVER_DATA_PORT_KEY, String.valueOf(DEFAULT_SERVER_DATA_PORT));
		}
		if (isChanged)
			saveProperties();

	}

	public static Properties getProperties() {
		return props;
	}

	public static void saveProperties() {
		try (FileOutputStream writer = new FileOutputStream(properties)) {
			props.store(writer, "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int getDataPort() {
		return Integer.parseInt(props.getProperty(SERVER_DATA_PORT_KEY, String.valueOf(DEFAULT_SERVER_DATA_PORT)));
	}

	public static int getPort() {
		return Integer.parseInt(props.getProperty(SERVER_PORT_KEY, String.valueOf(DEFAULT_SERVER_PORT)));
	}

	public static String getAddress() {
		return props.getProperty(SERVER_ADDRESS_KEY, DEFAULT_SERVER_ADDRESS);
	}
}
