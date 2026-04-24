package application.view;

import application.dao.NiveauDAO;
import application.model.Niveau;
import application.util.UIHelper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.Arrays;

public class NiveauView extends BorderPane {

    private final NiveauDAO dao = new NiveauDAO();
    private final TableView<Niveau> table = new TableView<>();
    private final TextField txtNom = new TextField();
    private final Label errNom = UIHelper.makeErrorLabel();
    private int editingId = -1;

    public NiveauView() {
        getStyleClass().add("view-root");

        // --- TOP BAR ---
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.getStyleClass().add("topbar");

        Label viewTitle = new Label("Gestion des Niveaux");
        viewTitle.getStyleClass().add("topbar-title");

        Button btnRefresh = new Button("Afficher / Actualiser");
        btnRefresh.getStyleClass().add("btn-secondary");
        btnRefresh.setOnAction(e -> refresh());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(viewTitle, spacer, btnRefresh);
        setTop(topBar);

        buildTable();
        setCenter(table);
        setRight(buildForm());
        setBottom(buildToolbar());

        refresh();
    }

    @SuppressWarnings("unchecked")
    private void buildTable() {
        table.getColumns().clear();
        table.getColumns().add(colString("ID", n -> String.valueOf(n.getId()), 60));
        table.getColumns().add(colString("Nom du Niveau", n -> n.getNom(), 300));

        table.getStyleClass().add("main-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().selectedItemProperty().addListener((obs, o, s) -> {
            if (s != null) {
                editingId = s.getId();
                txtNom.setText(s.getNom());
            }
        });
    }

    private VBox buildForm() {
        Button btnSave = new Button("Enregistrer");
        Button btnCancel = new Button("Effacer");
        btnSave.getStyleClass().add("btn-primary");
        btnCancel.getStyleClass().add("btn-secondary");
        btnSave.setOnAction(e -> save());
        btnCancel.setOnAction(e -> clear());
        VBox f = new VBox(formTitle("Niveau"), UIHelper.fieldBox("Nom du niveau *", txtNom, errNom),
                new HBox(8, btnSave, btnCancel));
        f.setPadding(new Insets(16));
        f.setSpacing(0);
        f.setPrefWidth(340);
        f.getStyleClass().add("form-panel");
        return f;
    }

    private HBox buildToolbar() {
        Button b = new Button("Supprimer");
        b.getStyleClass().add("btn-danger");
        b.setOnAction(e -> delete());
        HBox bar = new HBox(b);
        bar.setPadding(new Insets(10, 16, 10, 16));
        bar.getStyleClass().add("toolbar");
        return bar;
    }

    private void save() {
        if (txtNom.getText().trim().isEmpty()) {
            UIHelper.showFieldError(errNom, txtNom, "Nom obligatoire.");
            return;
        }
        UIHelper.hideFieldError(errNom, txtNom);
        Niveau n = new Niveau(editingId < 0 ? 0 : editingId, txtNom.getText().trim());
        boolean ok = editingId < 0 ? dao.insert(n) : dao.update(n);
        if (!ok) {
            UIHelper.showErrors(Arrays.asList("Erreur base de donnees."));
            return;
        }
        UIHelper.showSuccess("Niveau enregistre.");
        clear();
        refresh();
    }

    private void delete() {
        Niveau sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            UIHelper.showErrors(Arrays.asList("Selectionnez un niveau."));
            return;
        }
        if (!UIHelper.confirmDelete())
            return;
        dao.delete(sel.getId());
        clear();
        refresh();
    }

    private void clear() {
        editingId = -1;
        txtNom.clear();
        UIHelper.clearMark(txtNom);
        errNom.setVisible(false);
        errNom.setManaged(false);
    }

    private void refresh() {
        table.setItems(FXCollections.observableArrayList(dao.findAll()));
    }

    private Label formTitle(String t) {
        Label l = new Label(t);
        l.getStyleClass().add("form-title");
        return l;
    }

    private TableColumn<Niveau, String> colString(String title, java.util.function.Function<Niveau, String> mapper,
            double width) {
        TableColumn<Niveau, String> c = new TableColumn<>(title);
        c.setCellValueFactory(cd -> {
            if (cd.getValue() == null)
                return null;
            return new javafx.beans.property.SimpleStringProperty(mapper.apply(cd.getValue()));
        });
        c.setPrefWidth(width);
        c.setCellFactory(tc -> new TableCell<Niveau, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label l = new Label(item);
                    l.setStyle("-fx-text-fill: #1e293b; -fx-font-weight: bold; -fx-font-size: 14px;");
                    setGraphic(l);
                    setText(null);
                }
            }
        });
        return c;
    }
}
