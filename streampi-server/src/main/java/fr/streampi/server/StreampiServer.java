package fr.streampi.server;

import java.io.IOException;
import java.net.InetAddress;

import fr.streampi.server.controler.StreampiServerViewControler;
import fr.streampi.server.io.DataServer;
import fr.streampi.server.io.utils.DataUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class StreampiServer extends Application {

	private Stage mainStage;
	private DataServer server = new DataServer();

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		this.server.bind(InetAddress.getByName(DataUtils.getServerAddress()), DataUtils.getServerPort(), DataUtils.getServerDataPort());

		this.mainStage = primaryStage;
		primaryStage.setTitle("streampi-server");
		primaryStage.getIcons().add(new Image("/streampi-icon.png"));
		StreampiServerViewControler controler = loadMainFrame();

		controler.setServer(server);

		this.mainStage.show();

		this.mainStage.setOnCloseRequest(ev -> {
			this.mainStage.hide();
			try {
				server.close();
				DataUtils.saveProperties(DataUtils.getProperties());
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