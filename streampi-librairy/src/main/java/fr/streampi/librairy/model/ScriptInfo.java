package fr.streampi.librairy.model;

import java.io.Serializable;

import fr.streampi.librairy.model.enums.ScriptType;

public class ScriptInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1167219143079000916L;

	private String name;
	private ScriptType type = ScriptType.DEFAULT;

	public ScriptInfo() {
		super();
	}

	public ScriptInfo(String name, ScriptType type) {
		super();
		this.name = name;
		this.type = type;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public final ScriptType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public final void setType(ScriptType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ScriptInfo [name=" + name + ", type=" + type.name() + "]";
	}
}
