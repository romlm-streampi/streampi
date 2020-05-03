package fr.streampi.server.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;

import fr.streampi.librairy.model.Layout;

public final class DataUtils {

	private DataUtils() {
	}

	public static final File parentFile = new File(System.getProperty("user.home") + "/.streampi/");
	public static final File layoutFile = new File(parentFile, ".layout");
	public static final File iconsFolder = new File(parentFile, "icons");
	public static final File iconsZipFile = new File(parentFile, "iconsFolder.zip");
	public static final File propertiesFile = new File(parentFile, "streampi-server.properties");

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
	}

	public static Properties getProperties() throws IOException {
		Properties props = new Properties();
		props.load(new FileInputStream(propertiesFile));
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
