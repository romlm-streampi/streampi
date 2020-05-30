package fr.streampi.server.plugin.model;

import java.lang.reflect.Parameter;

import fr.streampi.server.plugin.annotations.StreampiPluginParameterAnnotation;

public class StreampiPluginParameter {

	private Parameter param;
	private Class<?> type;
	private String name = "";
	private String description = "";

	public StreampiPluginParameter(Parameter param) {
		this.setParameter(param);
	}

	public StreampiPluginParameter() {
	}

	public void setParameter(Parameter param) {
		this.param = param;
		if (this.param.isAnnotationPresent(StreampiPluginParameterAnnotation.class)) {
			StreampiPluginParameterAnnotation descriptor = this.param
					.getAnnotation(StreampiPluginParameterAnnotation.class);
			this.name = !descriptor.name().equals("") ? descriptor.name() : this.param.getName();
			this.description = descriptor.description();
			this.type = this.param.getType();
		}
	}

	public Class<?> getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Parameter getParam() {
		return param;
	}

	@Override
	public String toString() {
		return "StreampiPluginParameter [name=" + name + ", description=" + description + ", type=" + type + ", param="
				+ param + "]";
	}

}
