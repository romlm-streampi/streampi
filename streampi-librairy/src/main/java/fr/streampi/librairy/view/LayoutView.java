package fr.streampi.librairy.view;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import fr.streampi.librairy.model.IconPositioner;
import fr.streampi.librairy.model.Layout;
import fr.streampi.librairy.model.Size;
import fr.streampi.librairy.model.enums.IconType;
import fr.streampi.librairy.model.icons.FolderIcon;
import fr.streampi.librairy.model.icons.Icon;
import fr.streampi.librairy.model.icons.ScriptableIcon;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public abstract class LayoutView extends GridPane {

	private static final String BUTTON_CLASS = "streampi-button";
	private static final String ICON_PANE_CLASS = "streampi-icon-pane";

	private Layout layout;
	private ObservableList<IconPositioner<? extends Icon>> icons = FXCollections.observableArrayList();
	private String iconsFolderURI;

	public LayoutView() {
		this(new Layout(), new String());
	}

	public LayoutView(Layout layout, String iconsFolderURI) {
		super();
		this.getStylesheets().add("/style.css");
		this.setLayout(layout);

		this.setHgap(40);
		this.setVgap(40);
	}

	protected void reload() {
		this.getChildren().clear();
		this.getColumnConstraints().clear();
		for (int columnIndex = 0; columnIndex < layout.getSize().getColumnCount(); columnIndex++)
			this.getColumnConstraints().add(new ColumnConstraints(50, 150, 250));

		this.getRowConstraints().clear();
		for (int rowIndex = 0; rowIndex < layout.getSize().getRowCount(); rowIndex++)
			this.getRowConstraints().add(new RowConstraints(50, 150, 250));

		for (int rowIndex = 0; rowIndex < this.getSize().getRowCount(); rowIndex++) {
			for (int colIndex = 0; colIndex < getSize().getColumnCount(); colIndex++) {
				AnchorPane pane = new AnchorPane();
				pane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
				pane.getStyleClass().add(ICON_PANE_CLASS);
				this.add(pane, colIndex, rowIndex);
			}
		}
	}

	public ObservableList<IconPositioner<? extends Icon>> getIcons() {
		return icons;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
		this.reload();
		this.icons = FXCollections.observableArrayList(layout.getIcons());

		this.layout.setIcons(this.icons);
		this.icons.addListener((ListChangeListener<IconPositioner<? extends Icon>>) (change) -> {
			this.layout.setIcons(this.icons);
			this.reload();
			this.reloadIcons();
		});
		this.reloadIcons();

	}

	/**
	 * this method should not be used to modify Layout instead use
	 * {@linkplain LayoutView#getIcons()} for auto update
	 * 
	 * @return a clone of this layout
	 */
	public Layout getCloneLayout() {
		final Layout layout = new Layout(this.layout.getSize(), new ArrayList<>(this.icons));
		return layout;
	}

	protected void reloadIcons() {
		loadLayout(this.layout);
	}

	protected Button loadIcon(Icon icon, int columnIndex, int rowIndex) {
		AnchorPane pane = (AnchorPane) this.getChildren().get(columnIndex * this.getSize().getRowCount() + rowIndex);
		if (GridPane.getRowIndex(pane) != rowIndex || GridPane.getColumnIndex(pane) != columnIndex) {
			Optional<AnchorPane> optChild = this.getChildren().stream().filter(node -> {
				return node instanceof AnchorPane && GridPane.getColumnIndex(node) == columnIndex
						&& GridPane.getRowIndex(node) == rowIndex;
			}).map(node -> (AnchorPane) node).findFirst();

			if (optChild.isEmpty()) {
				this.add(pane, columnIndex, rowIndex);
			} else {
				pane = optChild.get();
			}
		}
		pane.getChildren().clear();
		Button button = new Button();
		button.getStyleClass().add(BUTTON_CLASS);
		pane.getChildren().add(button);

		button.setPrefSize(pane.getWidth(), pane.getHeight());
		pane.setMinSize(0, 0);

		pane.widthProperty().addListener((ChangeListener<Number>) (change, oldValue, newValue) -> {
			button.setPrefWidth(newValue.doubleValue());
		});

		pane.heightProperty().addListener((ChangeListener<Number>) (change, oldValue, newValue) -> {
			button.setPrefHeight(newValue.doubleValue());
		});

		return button;
	}

	protected void loadLayout(Layout layout) {
		for (IconPositioner<? extends Icon> positioner : layout.getIcons()) {
			Button button = loadIcon(positioner.getIcon(), positioner.getPositioner().getColumnIndex(),
					positioner.getPositioner().getRowIndex());

			if (positioner.getIcon().getIconType().equals(IconType.FolderIcon)) {
				FolderIcon icon = (FolderIcon) positioner.getIcon();
				button.setOnAction(ev -> {
					this.loadFolderLayout(layout, icon.getFolderLayout());
				});
			} else if (positioner.getIcon().getIconType().equals(IconType.ScriptableIcon)) {
				ScriptableIcon icon = (ScriptableIcon) positioner.getIcon();
				button.setOnAction(event -> {
					this.onScriptTriggered(icon);
				});
			}

			if (iconsFolderURI != null && positioner.getIcon().getIconName() != null) {
				File folder = new File(URI.create(iconsFolderURI));
				File iconFile = new File(folder, positioner.getIcon().getIconName());
				if (iconFile.exists())
					button.setGraphic(new ImageView(iconFile.toURI().toString()));
				else {
					button.setGraphic(new ImageView("/default-icon.png"));
				}
			} else {
				button.setGraphic(new ImageView("/default-icon.png"));
			}
		}
	}

	protected void loadFolderLayout(Layout parentLayout, Layout folderLayout) {
		List<IconPositioner<? extends Icon>> icons = folderLayout.getIcons().stream()
				.filter(pos -> pos.getPositioner().getColumnIndex() != 0 || pos.getPositioner().getRowIndex() != 0)
				.collect(Collectors.toList());

		Button backButton = loadIcon(new Icon(), 0, 0);
		folderLayout.setIcons(icons);
		loadLayout(folderLayout);
		backButton.setOnAction(ev -> {
			loadLayout(parentLayout);
		});

		backButton.setGraphic(new ImageView("/parent-folder-icon.png"));

	}

	public Size getSize() {
		return layout.getSize();
	}

	public String getIconsFolderURI() {
		return iconsFolderURI;
	}

	public void setIconsFolderURI(String iconsFolderURI) {
		this.iconsFolderURI = iconsFolderURI;
	}

	protected abstract void onScriptTriggered(ScriptableIcon icon);

}
