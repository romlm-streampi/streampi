package fr.streampi.librairy.model.enums;

public enum StreampiPluginCategory {

	MULTIMEDIA("multimedia"),
	STREAMING("streaming"),
	SYSTEM("system"),
	DEFAULT("miscellanous");

	private String category = "";

	private StreampiPluginCategory(String category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return category;
	}
}
