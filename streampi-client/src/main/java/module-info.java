module fr.streampi.client {
	requires javafx.controls;
	requires transitive javafx.graphics;

	requires transitive fr.streampi.librairy;

	exports fr.streampi.client to javafx.graphics;
}