package fr.streampi.server.io.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;

import fr.streampi.librairy.model.Layout;
import fr.streampi.server.io.DataServer;

public final class DataUtils {

	private DataUtils() {
	}

	public static final File parentFile = new File(System.getProperty("user.home") + "/.streampi/");
	public static final File layoutFile = new File(parentFile, ".layout");
	public static final File iconsFolder = new File(parentFile, "icons");
	public static final File iconsZipFile = new File(iconsFolder, "zippedIcons.zip");
	public static final File propertiesFile = new File(parentFile, "streampi-server.properties");

	private static final String SERVER_IP_KEY = "host.ip";
	private static final String SERVER_PORT_KEY = "host.port";
	private static final String SERVER_DATA_PORT_KEY = "host.data.port";

	private static Properties props;

	static {
		if (!parentFile.exists() || !parentFile.isDirectory())
			parentFile.mkdirs();
		if (!layoutFile.exists()) {
			try {
				layoutFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (!iconsFolder.exists())
			iconsFolder.mkdir();

		if (!propertiesFile.exists())
			try {
				propertiesFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

		try {
			loadProperties();
			if (!props.containsKey(SERVER_IP_KEY))
				props.put(SERVER_IP_KEY, DataServer.DEFAULT_ADDRESS);
			if (!props.containsKey(SERVER_PORT_KEY))
				props.put(SERVER_PORT_KEY, String.valueOf(DataServer.DEFAULT_PORT));
			if (!props.containsKey(SERVER_DATA_PORT_KEY))
				props.put(SERVER_DATA_PORT_KEY, String.valueOf(DataServer.DEFAULT_DATA_PORT));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getServerAddress() {
		return props.getProperty(SERVER_IP_KEY, DataServer.DEFAULT_ADDRESS);
	}

	public static int getServerPort() {
		return Integer.valueOf(props.getProperty(SERVER_PORT_KEY, String.valueOf(DataServer.DEFAULT_PORT)));
	}

	public static int getServerDataPort() {
		return Integer.valueOf(props.getProperty(SERVER_DATA_PORT_KEY, String.valueOf(DataServer.DEFAULT_DATA_PORT)));
	}

	public static void loadProperties() throws IOException {
		props = new Properties();
		props.load(new FileInputStream(propertiesFile));
	}

	public static Properties getProperties() {
		return props;
	}

	public static void saveProperties(Properties props) throws IOException {
		FileOutputStream fos = new FileOutputStream(propertiesFile);
		props.store(fos, null);
		fos.close();
	}

	public static void storeLayoutInfo(Layout info) throws IOException {
		ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(layoutFile));
		writer.writeObject(info);
		writer.close();
	}

	public static Layout retrieveLayoutInfo() throws IOException {
		Layout layout = null;
		try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(layoutFile))) {
			layout = (Layout) reader.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return layout;
	}

}
