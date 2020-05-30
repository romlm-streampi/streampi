package fr.streampi.server.plugin.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import fr.streampi.server.plugin.annotations.StreampiPluginConstructorAnnotation;
import fr.streampi.server.plugin.annotations.StreampiPluginFunctionAnnotation;
import fr.streampi.server.plugin.annotations.StreampiPluginObjectAnnotation;
import fr.streampi.server.plugin.enums.ExposurePolicy;
import fr.streampi.server.plugin.enums.StreampiPluginCategory;

public class StreampiPluginObject {
	private List<StreampiPluginFunction> functions = new ArrayList<>();
	private List<StreampiPluginConstructor> constructors = new ArrayList<>();
	private String name = "";
	private String description = "";
	private ExposurePolicy policy;
	private StreampiPluginCategory category;
	private Class<?> clazz;
	private AtomicReference<Object> instance = new AtomicReference<>();

	public StreampiPluginObject(Class<?> clazz) {
		this.setObject(clazz);
	}

	public StreampiPluginObject() {
	}

	public void setObject(Class<?> clazz) {
		this.clazz = clazz;
		if (this.clazz.isAnnotationPresent(StreampiPluginObjectAnnotation.class)) {
			StreampiPluginObjectAnnotation descriptor = this.clazz.getAnnotation(StreampiPluginObjectAnnotation.class);
			this.policy = descriptor.policy();
			this.name = !descriptor.name().equals("") ? descriptor.name() : clazz.getName();
			this.description = descriptor.description();
			this.category = descriptor.category();
			if (this.policy == ExposurePolicy.EXPOSED) {
				for (Method method : this.clazz.getMethods()) {
					functions.add(new StreampiPluginFunction(method, instance));
				}
				for (Constructor<?> constructor : this.clazz.getConstructors()) {
					this.constructors.add(new StreampiPluginConstructor(constructor, this));
				}

			} else if (this.policy == ExposurePolicy.RESTRICTED) {
				for (Method method : this.clazz.getMethods()) {
					if (method.isAnnotationPresent(StreampiPluginFunctionAnnotation.class)) {
						this.functions.add(new StreampiPluginFunction(method, instance));
					}
				}
				for (Constructor<?> constructor : this.clazz.getConstructors()) {
					if (constructor.isAnnotationPresent(StreampiPluginConstructorAnnotation.class))
						this.constructors.add(new StreampiPluginConstructor(constructor, this));
				}
			}

		}

	}

	public void selectDefaultConstructor() {
		try {
			this.instance.set(this.clazz.getConstructor().newInstance());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	public void selectConstructor(StreampiPluginConstructor constructor, Object... params)
			throws NullPointerException, IllegalArgumentException {
		if (this.clazz == null)
			throw new NullPointerException("clazz has not been set");
		if (!this.constructors.contains(constructor))
			throw new IllegalArgumentException("could not find constructor : " + constructor);

		try {
			this.instance.set(constructor.getConstructor().newInstance(params));
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public StreampiPluginCategory getCategory() {
		return category;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public List<StreampiPluginFunction> getFunctions() {
		return functions;
	}

	public List<StreampiPluginConstructor> getConstructors() {
		return constructors;
	}

	public Class<?> getObject() {
		return clazz;
	}

	@Override
	public String toString() {
		return "StreampiPluginObject [name=" + name + ", policy=" + policy + ", description=" + description
				+ ", functions=" + functions + ", clazz=" + clazz + "]";
	}

}
