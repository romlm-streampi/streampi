package fr.streampi.librairy.model.icons;

import java.io.Serializable;

import fr.streampi.librairy.model.enums.IconType;

public class Icon implements Serializable{

	private static final long serialVersionUID = 6136734279023490068L;
	
	protected String iconName;
	protected IconType iconType = IconType.Default;

	public Icon() {
		super();
	}

	public Icon(String iconName, IconType type) {
		super();
		this.iconName = iconName;
		this.iconType = type;
	}

	/**
	 * @return the type
	 */
	public final IconType getIconType() {
		return iconType;
	}

	/**
	 * @param type the type to set
	 */
	public final void setIconType(IconType type) {
		this.iconType = type;
	}

	/**
	 * @return the iconName
	 */
	public final String getIconName() {
		return iconName;
	}

	/**
	 * @param iconName the iconName to set
	 */
	public final void setIconName(String iconName) {
		this.iconName = iconName;
	}

	@Override
	public String toString() {
		return "Icon [iconName=" + iconName + ", iconType=" + iconType + "]";
	}

}
