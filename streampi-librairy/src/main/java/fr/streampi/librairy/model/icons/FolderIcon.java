package fr.streampi.librairy.model.icons;

import fr.streampi.librairy.model.Layout;
import fr.streampi.librairy.model.enums.IconType;

public class FolderIcon extends Icon {

	/**
	 * 
	 */
	private static final long serialVersionUID = -515827901062237575L;
	protected Layout folderLayout;

	public FolderIcon() {
		super();
		this.iconType = IconType.FolderIcon;
	}

	public FolderIcon(String iconName, Layout folderLayout) {
		this();
		this.iconName = iconName;
		this.folderLayout = folderLayout;
	}

	/**
	 * @return the folderLayout
	 */
	public final Layout getFolderLayout() {
		return folderLayout;
	}

	/**
	 * @param folderLayout the folderLayout to set
	 */
	public final void setFolderLayout(Layout folderLayout) {
		this.folderLayout = folderLayout;
	}

}
