package fr.streampi.librairy.model;

import java.io.Serializable;

public class Positioner implements Serializable {

	private static final long serialVersionUID = -2170642326545142074L;

	private int columnIndex;
	private int rowIndex;

	public Positioner() {
		// TODO Auto-generated constructor stub
	}

	public Positioner(int columnIndex, int rowIndex) {
		super();
		this.columnIndex = columnIndex;
		this.rowIndex = rowIndex;
	}

	/**
	 * @return the xPos
	 */
	public final int getColumnIndex() {
		return columnIndex;
	}

	/**
	 * @param xPos the xPos to set
	 */
	public final void setColumnIndex(int xPos) {
		this.columnIndex = xPos;
	}

	/**
	 * @return the yPos
	 */
	public final int getRowIndex() {
		return rowIndex;
	}

	/**
	 * @param yPos the yPos to set
	 */
	public final void setRowIndex(int yPos) {
		this.rowIndex = yPos;
	}

	@Override
	public String toString() {
		return String.format("{colIndex: %d, rowIndex: %d}", columnIndex, rowIndex);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Positioner)) {
			return false;
		} else {
			return ((Positioner)obj).columnIndex == columnIndex && ((Positioner)obj).rowIndex == rowIndex;
		}
	}

}
