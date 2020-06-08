package fr.streampi.server.controler;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import fr.streampi.librairy.model.IconPositioner;
import fr.streampi.librairy.model.Layout;
import fr.streampi.librairy.model.ScriptInfo;
import fr.streampi.librairy.model.enums.StreampiPluginCategory;
import fr.streampi.librairy.model.icons.ScriptableIcon;
import fr.streampi.librairy.view.LayoutView;
import fr.streampi.server.StreampiServer;
import fr.streampi.server.io.DataServer;
import fr.streampi.server.io.utils.DataUtils;
import fr.streampi.server.plugin.model.StreampiPluginFunction;
import fr.streampi.server.plugin.model.StreampiPluginModule;
import fr.streampi.server.plugin.model.StreampiPluginObject;
import fr.streampi.server.plugin.model.StreampiPluginRegisterer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
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

	private LayoutView layoutView = new LayoutView();

	private DataServer server = new DataServer();

	private Map<ScriptInfo, Map<StreampiPluginFunction, Object[]>> scripts = new HashMap<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		File root = new File(System.getProperty("user.home") + "/.streampi/plugins/");

		StreampiPluginRegisterer pr = new StreampiPluginRegisterer(root);
		List<StreampiPluginObject> objects = pr.getModules().stream().map(StreampiPluginModule::getObjects)
				.collect(listCollector());

		List<String> categories = objects.stream().map(StreampiPluginObject::getCategory)
				.map(StreampiPluginCategory::toString).collect(Collectors.toList());
		this.actionSelector.getPanes().clear();
		for (String category : categories) {
			this.actionSelector.getPanes().add(new TitledPane(category, new VBox()));
		}

		Map<StreampiPluginFunction, Object[]> deezerMuteFuncs = new HashMap<>();
		File deezer = new File(
				"C:\\Program Files\\WindowsApps\\Deezer.62021768415AF_4.19.30.0_x86__q7m17pa7q8kj0\\app\\Deezer.exe");

		for (StreampiPluginObject object : objects) {
			Optional<TitledPane> optPane = this.actionSelector.getPanes().stream()
					.filter(x -> x.getText().equals(object.getCategory().toString())).findAny();

			if (optPane.isPresent() && optPane.get().getContent() instanceof VBox) {
				VBox box = (VBox) optPane.get().getContent();
				for (StreampiPluginFunction func : object.getFunctions()) {
					// TODO : remove debug codes
					if (func.getName().equals("switchSessionMute")) {
						deezerMuteFuncs.put(func, new Object[] { deezer });
						try {
							func.getParentObject().selectDefaultConstructor();
						} catch (InstantiationException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}

					String id = object.getName() + "." + func.getName();
					ScriptInfo info = new ScriptInfo(id, object.getCategory());
					Button button = new Button(id);
					button.setOnAction(x -> System.out.println(info));
					box.getChildren().add(button);
				}
			}
		}
		this.scripts.put(new ScriptInfo("deezer_mute", StreampiPluginCategory.MULTIMEDIA), deezerMuteFuncs);

		try {
			Layout layout = DataUtils.retrieveLayoutInfo();
			layoutView.setLayout(layout);
		} catch (IOException e) {
			e.printStackTrace();
		}

		layoutView.setIconsFolderURI(DataUtils.iconsFolder.toURI().toString());
		streampiViewer.setCenter(layoutView);
		layoutView.setPadding(new Insets(20, 20, 20, 40));
		layoutView.setHgap(40);
		layoutView.setVgap(40);
		layoutView.setPrefWidth(900);

		this.layoutView.setOnScriptTriggered(positioner -> {
			loadButton(positioner);
		});

	}

	private void loadButton(IconPositioner<ScriptableIcon> positioner) {
		ScriptInfo info = positioner.getIcon().getScriptInfo();
		this.buttonViewer.getChildren().clear();
		this.buttonViewer.getChildren().add(new Label(info != null ? info.toString() : "null"));
	}

	private <T> Collector<List<T>, ?, List<T>> listCollector() {
		return Collector.of((Supplier<List<T>>) ArrayList::new, List::addAll, (left, right) -> {
			left.addAll(right);
			return left;
		});
	}

	public void setServer(DataServer server) {
		this.server = server;
		try {
			updateLayout();
		} catch (IOException e) {
			e.printStackTrace();
		}
		server.setOnScriptReceived(info -> {
			Map<StreampiPluginFunction, Object[]> functions = Optional.ofNullable(scripts.get(info))
					.orElseGet(HashMap::new);
			for (Entry<StreampiPluginFunction, Object[]> function : functions.entrySet()) {
				try {
					function.getKey().execute(function.getValue());
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@FXML
	public void updateLayout() throws IOException {
		server.setLayout(layoutView.getCloneLayout());
	}

	public void setMain(StreampiServer main) {
		this.main = main;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@Override
	public void close() throws IOException {
		DataUtils.storeLayoutInfo(this.layoutView.getCloneLayout());
	}

}
