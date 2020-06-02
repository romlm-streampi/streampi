package fr.streampi.librairy.model;

import java.io.Serializable;

import fr.streampi.librairy.model.icons.Icon;


public class IconPositioner<T extends Icon> implements Serializable {

	private static final long serialVersionUID = 7319841029455565918L;

	private Positioner positioner;
	private T icon;

	public IconPositioner() {
		super();
	}

	public IconPositioner(T icon, Positioner positioner) {
		super();
		this.positioner = positioner;
		this.icon = icon;
	}

	/**
	 * @return the positioner
	 */
	public final Positioner getPositioner() {
		return positioner;
	}

	/**
	 * @param positioner the positioner to set
	 */
	public final void setPositioner(Positioner positioner) {
		this.positioner = positioner;
	}

	/**
	 * @return the icon
	 */
	public final T getIcon() {
		return icon;
	}

	/**
	 * @param icon the icon to set
	 */
	public final void setIcon(T icon) {
		this.icon = icon;
	}

	@Override
	public String toString() {
		return "IconPositioner [positioner=" + positioner + ", icon=" + icon + "]";
	}

}
