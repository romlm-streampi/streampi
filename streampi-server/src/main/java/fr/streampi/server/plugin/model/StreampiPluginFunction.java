package fr.streampi.server.plugin.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import fr.streampi.server.plugin.annotations.StreampiPluginFunctionAnnotation;

public class StreampiPluginFunction {

	private String name = "";
	private String description = "";
	private List<StreampiPluginParameter> params = new ArrayList<>();
	private Method method;
	private AtomicReference<Object> parent;

	public StreampiPluginFunction(Method method, AtomicReference<Object> parent) {
		this.method = method;
		this.setParent(parent);
		if (this.method.isAnnotationPresent(StreampiPluginFunctionAnnotation.class)) {
			StreampiPluginFunctionAnnotation descriptor = this.method
					.getAnnotation(StreampiPluginFunctionAnnotation.class);
			this.name = !descriptor.name().equals("") ? descriptor.name() : this.method.getName();
			this.description = descriptor.description();
			for (Parameter param : this.method.getParameters()) {
				params.add(new StreampiPluginParameter(param));
			}
		} else {
			this.name = this.method.getName();
		}
	}

	public void setParent(AtomicReference<Object> parent) {
		this.parent = parent;
	}

	public List<StreampiPluginParameter> getParams() {
		return params;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Object execute(Object... params)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return this.method.invoke(parent.get(), params);
	}

	@Override
	public String toString() {
		return "StreampiPluginFunction [name=" + name + ", description=" + description + ", params=" + params
				+ ", method=" + method + "]";
	}

}
