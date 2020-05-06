package fr.streampi.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;

import fr.streampi.client.io.DataClient;
import fr.streampi.client.io.utils.DataUtils;
import fr.streampi.librairy.model.icons.ScriptableIcon;
import fr.streampi.librairy.view.LayoutView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class StreampiClient extends Application {

	private BorderPane root;
	private LayoutView view;
	private DataClient client = new DataClient();

	@Override
	public void start(Stage primaryStage) throws Exception {

		view = new LayoutView() {

			@Override
			protected void onScriptTriggered(ScriptableIcon icon) {
				try {
					client.sendScriptInfo(icon.getScriptInfo());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		root = new BorderPane(view);
		primaryStage.initStyle(StageStyle.UNDECORATED);
		AnchorPane buttonBar = new AnchorPane();
		Button exitButton = new Button();
		AnchorPane.setRightAnchor(exitButton, 0d);
		buttonBar.setPrefHeight(0);

		URL url = StreampiClient.class.getClassLoader().getResource("close-icon.png");
		if(url != null)
			exitButton.setGraphic(new ImageView(url.toString()));
		else
			exitButton.setText("close");

		buttonBar.getChildren().add(exitButton);
		root.setTop(buttonBar);

		exitButton.setOnAction(action -> {
			primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
		});

		view.setIconsFolderURI(DataUtils.iconsFolder.toURI().toString());
		Scene scene = new Scene(root);

		client.connect(InetAddress.getByName(DataUtils.getAddress()), DataUtils.getPort(), DataUtils.getDataPort());

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
