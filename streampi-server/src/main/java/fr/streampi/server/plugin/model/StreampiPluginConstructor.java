package fr.streampi.server.plugin.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import fr.streampi.server.plugin.annotations.StreampiPluginConstructorAnnotation;

public class StreampiPluginConstructor {

	private Constructor<?> constructor;
	private String name = "";
	private String description = "";
	private List<StreampiPluginParameter> params = new ArrayList<>();
	private StreampiPluginObject parent;

	public StreampiPluginConstructor() {
	}

	public StreampiPluginConstructor(Constructor<?> constructor, StreampiPluginObject parent) {
		setConstructor(constructor);
		this.parent = parent;
	}

	public void setConstructor(Constructor<?> constructor) {
		this.constructor = constructor;
		if (this.constructor.isAnnotationPresent(StreampiPluginConstructorAnnotation.class)) {
			StreampiPluginConstructorAnnotation descriptor = this.constructor
					.getAnnotation(StreampiPluginConstructorAnnotation.class);
			this.name = !descriptor.name().equals("") ? descriptor.name() : this.constructor.getName();
			this.description = descriptor.description();
			for (Parameter param : this.constructor.getParameters()) {
				this.params.add(new StreampiPluginParameter(param));
			}
		} else {
			this.name = this.constructor.getName();
		}
	}
	
	public void setParent(StreampiPluginObject parent) {
		this.parent = parent;
	}
	
	public StreampiPluginObject getParent() {
		return parent;
	}

	public Constructor<?> getConstructor() {
		return constructor;
	}

	public List<StreampiPluginParameter> getParams() {
		return params;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "StreampiPluginConstructor [name=" + name + ", description=" + description + ", params=" + params
				+ ", constructor=" + constructor + "]";
	}

}
