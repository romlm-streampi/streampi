package fr.streampi.server.controler;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import fr.streampi.librairy.model.IconPositioner;
import fr.streampi.librairy.model.Layout;
import fr.streampi.librairy.model.Positioner;
import fr.streampi.librairy.model.Script;
import fr.streampi.librairy.model.ScriptInfo;
import fr.streampi.librairy.model.enums.ScriptType;
import fr.streampi.librairy.model.icons.FolderIcon;
import fr.streampi.librairy.model.icons.ScriptableIcon;
import fr.streampi.librairy.view.LayoutView;
import fr.streampi.server.StreampiServer;
import fr.streampi.server.io.DataServer;
import fr.streampi.server.io.utils.DataUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class StreampiServerViewControler implements Initializable, Closeable {

	@SuppressWarnings("unused")
	private StreampiServer main;
	@SuppressWarnings("unused")
	private Stage stage;

	@FXML
	private BorderPane streampiViewer;
	@FXML
	private AnchorPane buttonViewer;

	private LayoutView layoutView;

	private DataServer server = new DataServer();

	private Map<ScriptInfo, Script> scripts = new HashMap<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		ScriptInfo scriptInfo = new ScriptInfo("test_script", ScriptType.LAUNCHER);
		scripts.put(scriptInfo, () -> {
			Runtime.getRuntime().exec("cmd.exe /c start cmd");
		});

		server = new DataServer();
		server.setOnScriptReceived(info -> {
			Optional<Script> scr = Optional.ofNullable(scripts.get(info));
			if (scr.isPresent())
				try {
					scr.get().execute();
				} catch (IOException e) {
					e.printStackTrace();
				}
		});
		layoutView = new LayoutView() {

			@Override
			protected void onScriptTriggered(ScriptableIcon icon) {
				System.out.println(icon.getScriptInfo());

			}

		};
		layoutView.setIconsFolderURI(DataUtils.iconsFolder.toURI().toString());
		streampiViewer.setCenter(layoutView);

		FolderIcon icon = new FolderIcon();
		Layout subLayout = new Layout();
		subLayout.setSize(layoutView.getSize());
		subLayout.getIcons().add(new IconPositioner<ScriptableIcon>(new ScriptableIcon("play-icon.png", scriptInfo),
				new Positioner(1, 0)));
		icon.setFolderLayout(subLayout);

		layoutView.getIcons().add(new IconPositioner<FolderIcon>(icon, new Positioner(0, 0)));
		layoutView.getIcons().add(new IconPositioner<ScriptableIcon>(new ScriptableIcon(), new Positioner(1, 0)));

		server.setLayout(layoutView.getCloneLayout());
		try {
			server.bind(InetAddress.getByName("192.168.1.37"), DataServer.DEFAULT_PORT,
					DataServer.DEFAULT_DATA_PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void close() throws IOException {
		this.server.close();
	}

	public void setMain(StreampiServer main) {
		this.main = main;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

}
