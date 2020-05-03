module fr.streampi.server {
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.graphics;

	requires transitive fr.streampi.librairy;

	opens fr.streampi.server.controler to javafx.fxml;

	exports fr.streampi.server to javafx.graphics;
	exports fr.streampi.server.controler to javafx.fxml;

	exports fr.streampi.server.io;
}