package fr.streampi.librairy.model;

import java.io.Serializable;
import java.util.Objects;

import fr.streampi.librairy.model.enums.StreampiPluginCategory;

public class ScriptInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1167219143079000916L;

	private String id;
	private StreampiPluginCategory type = StreampiPluginCategory.DEFAULT;

	public ScriptInfo() {
		super();
	}

	public ScriptInfo(String name, StreampiPluginCategory type) {
		super();
		this.id = name;
		this.type = type;
	}

	/**
	 * @return the name
	 */
	public final String getId() {
		return id;
	}

	/**
	 * @param name the name to set
	 */
	public final void setId(String name) {
		this.id = name;
	}

	/**
	 * @return the type
	 */
	public final StreampiPluginCategory getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public final void setType(StreampiPluginCategory type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ScriptInfo [id=" + id + ", type=" + type.name() + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScriptInfo other = (ScriptInfo) obj;
		return Objects.equals(id, other.id) && type == other.type;
	}
}
