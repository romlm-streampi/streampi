package fr.streampi.server.controler;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.stream.Collector;

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
import fr.streampi.server.plugin.model.StreampiPluginModule;
import fr.streampi.server.plugin.model.StreampiPluginObject;
import fr.streampi.server.plugin.model.StreampiPluginRegisterer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
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
	@FXML
	private Accordion actionSelector;

	private LayoutView layoutView;

	private DataServer server = new DataServer();

	private Map<ScriptInfo, Script> scripts = new HashMap<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		File root = new File(System.getProperty("user.home") + "/.streampi/plugins/");

		StreampiPluginRegisterer pr = new StreampiPluginRegisterer(root);

		pr.getModules().stream().map(StreampiPluginModule::getObjects).collect(listCollector()).stream()
				.map(StreampiPluginObject::getFunctions).collect(listCollector()).forEach(x -> System.out.println(x.getName()));

		ScriptInfo scriptInfo = new ScriptInfo("deezer_mute", ScriptType.MODIFIER);
		scripts.put(scriptInfo, () -> {
			System.out.println("deezer muted");
		});

		server.setOnScriptReceived(info -> {
			Optional<Script> scr = Optional.ofNullable(scripts.get(info));
			if (scr.isPresent())
				try {
					scr.get().execute();
				} catch (IOException e) {
					e.printStackTrace();
				}
		});

		try {
			server.bind(InetAddress.getByName("192.168.1.37"), DataServer.DEFAULT_PORT, DataServer.DEFAULT_DATA_PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		layoutView = new LayoutView() {

			@Override
			protected void onScriptTriggered(ScriptableIcon icon) {
				System.out.println(icon.getScriptInfo());

			}

		};
		layoutView.setIconsFolderURI(DataUtils.iconsFolder.toURI().toString());
		streampiViewer.setCenter(layoutView);
		layoutView.setPadding(new Insets(20, 20, 20, 40));
		layoutView.setHgap(40);
		layoutView.setVgap(40);
		layoutView.setPrefWidth(900);

		FolderIcon icon = new FolderIcon();
		Layout subLayout = new Layout();
		subLayout.setSize(layoutView.getSize());
		subLayout.getIcons().add(new IconPositioner<ScriptableIcon>(new ScriptableIcon("play-icon.png", scriptInfo),
				new Positioner(1, 0)));
		icon.setFolderLayout(subLayout);

		layoutView.getIcons().add(new IconPositioner<FolderIcon>(icon, new Positioner(0, 0)));
		layoutView.getIcons().add(new IconPositioner<ScriptableIcon>(new ScriptableIcon(), new Positioner(1, 0)));

		updateLayout();

	}

	private <T> Collector<List<T>, ?, List<T>> listCollector() {
		return Collector.of((Supplier<List<T>>) ArrayList::new, List::addAll, (left, right) -> {
			left.addAll(right);
			return left;
		});
	}

	@FXML
	public void updateLayout() {
		server.setLayout(layoutView.getCloneLayout());
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
