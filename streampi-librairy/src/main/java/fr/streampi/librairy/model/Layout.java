package fr.streampi.librairy.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.streampi.librairy.model.icons.Icon;

public class Layout implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6892898735986883787L;

	public static final Size DEFAULT_LAYOUT_SIZE = new Size(5, 3);

	protected List<IconPositioner<? extends Icon>> icons = new ArrayList<>();
	protected Size size = DEFAULT_LAYOUT_SIZE;

	public Layout() {
	}

	public Layout(Size size, List<IconPositioner<? extends Icon>> icons) {
		this.size = size;
		this.icons = icons;
	}
	
	public Size getSize() {
		return size;
	}
	
	public void setSize(Size size) {
		this.size = size;
	}

	public List<IconPositioner<? extends Icon>> getIcons() {
		return icons;
	}

	public void setIcons(List<IconPositioner<? extends Icon>> icons) {
		this.icons = icons;
	}

	@Override
	public String toString() {
		return "Layout [icons=" + icons + ", size=" + size + "]";
	}
	
	

}
