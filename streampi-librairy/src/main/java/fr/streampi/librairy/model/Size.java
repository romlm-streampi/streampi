package fr.streampi.librairy.model;

import java.io.Serializable;

public class Size implements Serializable {

	private static final long serialVersionUID = 4894717925220986875L;

	private int columnCount;
	private int rowCount;

	public Size() {

	}

	public Size(int columnCount, int rowCount) {
		super();
		this.columnCount = columnCount;
		this.rowCount = rowCount;
	}

	/**
	 * @return the columnCount
	 */
	public final int getColumnCount() {
		return columnCount;
	}

	/**
	 * @param columnCount the columnCount to set
	 */
	public final void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}

	/**
	 * @return the rowCount
	 */
	public final int getRowCount() {
		return rowCount;
	}

	/**
	 * @param rowCount the rowCount to set
	 */
	public final void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	@Override
	public String toString() {
		return "Size [columnCount=" + columnCount + ", rowCount=" + rowCount + "]";
	}

}
