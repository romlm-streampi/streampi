package fr.streampi.server;

import java.io.IOException;

import fr.streampi.server.controler.StreampiServerViewControler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class StreampiServer extends Application {

	private Stage mainStage;

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		this.mainStage = primaryStage;
		primaryStage.setTitle("streampi-server");
		primaryStage.getIcons().add(new Image("/streampi-icon.png"));
		StreampiServerViewControler controler = loadMainFrame();

		this.mainStage.show();

		this.mainStage.setOnCloseRequest(ev -> {
			this.mainStage.hide();
			try {
				controler.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		});

	}

	private StreampiServerViewControler loadMainFrame() throws IOException {
		FXMLLoader loader = new FXMLLoader(
				StreampiServer.class.getModule().getClassLoader().getResource("StreampiServerView.fxml"));

		BorderPane parent = loader.<BorderPane>load();
		Scene scene = new Scene(parent);
		this.mainStage.setScene(scene);

		StreampiServerViewControler controler = loader.<StreampiServerViewControler>getController();
		controler.setMain(this);
		controler.setStage(mainStage);

		return controler;
	}

	public static void main(String[] args) {
		launch(args);

	}

}