package fr.streampi.server.plugin.model;

import java.io.File;
import java.io.IOException;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class StreampiPluginRegisterer {

	private File[] paths = {};
	
	public StreampiPluginRegisterer(File... paths) {
		this.paths = paths;
	}
	
	public StreampiPluginRegisterer() {
		// TODO Auto-generated constructor stub
	}

	public List<StreampiPluginModule> getModules() {
		List<StreampiPluginModule> modules = new ArrayList<>();
		for (File parentFile : paths) {
			if (parentFile.isDirectory()) {
				for (File childFile : parentFile.listFiles(f -> f.getName().endsWith(".jar") && f.isFile())) {
					modules.addAll(loadJar(childFile));
				}
			} else if (parentFile.getName().endsWith(".jar")) {
				modules.addAll(loadJar(parentFile));
			}
		}

		return modules;
	}
	
	public void setPaths(File... paths) {
		this.paths = paths;
	}
	
	public File[] getPaths() {
		return paths;
	}

	private List<StreampiPluginModule> loadJar(File file) {

		List<StreampiPluginModule> plugins = new ArrayList<>();
		ModuleFinder finder = ModuleFinder.of(file.toPath());
		try (JarFile jarFile = new JarFile(file)) {
			ModuleLayer parentLayer = ModuleLayer.boot();

			List<String> names = finder.findAll().stream().map(x -> x.descriptor().name()).collect(Collectors.toList());
			Configuration cf = parentLayer.configuration().resolve(finder, ModuleFinder.of(), names);
			ModuleLayer layer = parentLayer.defineModulesWithOneLoader(cf, ClassLoader.getSystemClassLoader());
			for (String name : names) {
				plugins.add(new StreampiPluginModule(layer.findLoader(name), jarFile));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return plugins;
	}

}
