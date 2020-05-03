package fr.streampi.client;

import java.io.IOException;
import java.net.InetAddress;

import fr.streampi.client.io.DataClient;
import fr.streampi.librairy.model.icons.ScriptableIcon;
import fr.streampi.librairy.view.LayoutView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StreampiClient extends Application {

	private LayoutView view;
	private DataClient client = new DataClient();

	@Override
	public void start(Stage primaryStage) throws Exception {
		view = new LayoutView() {

			@Override
			protected void onScriptTriggered(ScriptableIcon icon) {
				System.out.println("sending : " + icon.getScriptInfo());
				try {
					client.sendScriptInfo(icon.getScriptInfo());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		};
		Scene scene = new Scene(view);

		client.connect(InetAddress.getLoopbackAddress(), 9293, 9393);
		client.getLayoutProperty().addListener((change, oldValue, newValue) -> {
			Platform.runLater(() -> {
				view.setLayout(newValue);
			});
		});

		primaryStage.setMinWidth(0);
		primaryStage.setMinHeight(0);
		primaryStage.widthProperty().addListener((change, oldValue, newValue) -> {
			view.setPrefWidth(newValue.doubleValue());
		});

		primaryStage.heightProperty().addListener((change, oldValue, newValue) -> {
			view.setPrefHeight(newValue.doubleValue());
		});

		scene.getStylesheets().add("/style.css");

		primaryStage.setResizable(false);
		primaryStage.setAlwaysOnTop(true);

		primaryStage.setFullScreen(true);
		primaryStage.setScene(scene);

		primaryStage.setOnCloseRequest(ev -> {
			try {
				this.client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
