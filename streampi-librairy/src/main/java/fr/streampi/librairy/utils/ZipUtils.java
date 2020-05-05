package fr.streampi.librairy.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class ZipUtils {

	private ZipUtils() {
	}

	public static void zipFolder(Path sourceFolder, Path zipFile) throws IOException {

		if (!Files.exists(zipFile))
			zipFile = Files.createFile(zipFile);

		try (ZipOutputStream writer = new ZipOutputStream(Files.newOutputStream(zipFile))) {
			Files.walk(sourceFolder).filter(file -> !Files.isDirectory(file)).forEach(file -> {
				ZipEntry entry = new ZipEntry(sourceFolder.relativize(file).toString());
				try {
					writer.putNextEntry(entry);
					Files.copy(file, writer);
					writer.closeEntry();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void unzipFile(Path zipFile, Path sourceFolder) throws IOException {
		if (!Files.exists(sourceFolder))
			sourceFolder = Files.createDirectory(sourceFolder);

		try (ZipInputStream reader = new ZipInputStream(Files.newInputStream(zipFile))) {
			ZipEntry entry;
			while ((entry = reader.getNextEntry()) != null) {
				File fileToWrite = new File(sourceFolder.toFile().getAbsolutePath() + File.separator + entry.getName());
				fileToWrite.createNewFile();
				FileOutputStream writer = new FileOutputStream(fileToWrite);
				int value;
				while((value = reader.read()) != -1)
					writer.write(value);
				
				writer.close();
				reader.closeEntry();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
