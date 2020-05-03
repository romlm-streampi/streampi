package fr.streampi.librairy.model.icons;

import fr.streampi.librairy.model.ScriptInfo;
import fr.streampi.librairy.model.enums.IconType;

public class ScriptableIcon extends Icon {

	private static final long serialVersionUID = -125680045292545520L;
	
	protected ScriptInfo scriptInfo;

	public ScriptableIcon() {
		super();
		this.iconType = IconType.ScriptableIcon;
	}

	public ScriptableIcon(String iconName, ScriptInfo scriptInfo) {
		super(iconName, IconType.ScriptableIcon);
		this.scriptInfo = scriptInfo;
	}

	/**
	 * @return the script
	 */
	public final ScriptInfo getScriptInfo() {
		return scriptInfo;
	}

	/**
	 * @param scriptInfo the script to set
	 */
	public final void setScriptInfo(ScriptInfo scriptInfo) {
		this.scriptInfo = scriptInfo;
	}

}
