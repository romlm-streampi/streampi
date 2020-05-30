package fr.streampi.server.plugin.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import fr.streampi.server.plugin.annotations.StreampiPluginObjectAnnotation;

public class StreampiPluginModule {

	private static final String DESCRIPTOR_NAME = "plugin-descriptor.cfg";
	private static final String DESCRIPTOR_SEPARATOR = "=";
	private static final String DESCRIPTOR_COMMENT = "#";

	private Map<String, String> descriptor = new HashMap<>();
	private List<StreampiPluginObject> objects = new ArrayList<>();

	public StreampiPluginModule(ClassLoader loader, JarFile jarFile) {
		jarFile.stream().filter(entry -> {
			if (entry.getName().equals(DESCRIPTOR_NAME)) {
				try (InputStream stream = jarFile.getInputStream(entry)) {
					this.describeModule(stream);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return false;
			} else if (!entry.getName().endsWith(".class")) {
				return false;
			}
			if (entry.getName().contains("module-info"))
				return false;
			return true;
		}).map(entry -> entry.getName().subSequence(0, entry.getName().lastIndexOf('.')).toString().replaceAll("/",
				".")).forEach(className -> {
					try {
						Class<?> clazz = loader.loadClass(className);
						if (clazz.isAnnotationPresent(StreampiPluginObjectAnnotation.class))
							this.objects.add(new StreampiPluginObject(clazz));
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				});
	}

	private void describeModule(InputStream stream) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		this.descriptor = reader.lines().map(s -> s.replaceAll("^(\\s+)|(\\s+)$", ""))
				.filter(s -> !s.startsWith(DESCRIPTOR_COMMENT) && !s.equals("")).map(s -> s.split(DESCRIPTOR_SEPARATOR))
				.filter(sarr -> sarr.length == 2)
				.collect(Collectors.toMap(sarr -> sarr[0].replaceAll("^(\\s+)|(\\s+)$", ""),
						sarr -> sarr[1].replaceAll("^(\\s+)|(\\s+)$", "")));

	}

	public Map<String, String> getDescriptor() {
		return descriptor;
	}

	public List<StreampiPluginObject> getObjects() {
		return objects;
	}

	@Override
	public String toString() {
		return "StreampiPluginModule [descriptor=" + descriptor + ", objects=" + objects + "]";
	}
	
	

}
