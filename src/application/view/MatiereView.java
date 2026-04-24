package application.view;

import application.dao.MatiereDAO;
import application.dao.NiveauDAO;
import application.model.Matiere;
import application.model.Niveau;
import application.util.UIHelper;
import application.util.Validator;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.Arrays;

public class MatiereView extends BorderPane {

    private final MatiereDAO dao = new MatiereDAO();
    private final NiveauDAO niveauDAO = new NiveauDAO();
    private final TableView<Matiere> table = new TableView<>();

    private final TextField txtNom = new TextField();
    private final ComboBox<Niveau> cmbNiveau = new ComboBox<>();
    private final Label errNom = UIHelper.makeErrorLabel();
    private final Label errNiveau = UIHelper.makeErrorLabel();

    private int editingId = -1;

    public MatiereView() {
        getStyleClass().add("view-root");

        // --- TOP BAR ---
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.getStyleClass().add("topbar");

        Label viewTitle = new Label("Gestion des Matieres");
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
        table.getColumns().add(colString("ID", m -> String.valueOf(m.getId()), 60));
        table.getColumns().add(colString("Matiere", m -> m.getNom(), 200));
        table.getColumns().add(colString("Niveau", m -> m.getNiveauNom(), 150));

        table.getStyleClass().add("main-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().selectedItemProperty().addListener((obs, o, s) -> {
            if (s != null) {
                editingId = s.getId();
                txtNom.setText(s.getNom());
                cmbNiveau.getItems().stream().filter(n -> n.getId() == s.getNiveauId()).findFirst()
                        .ifPresent(cmbNiveau::setValue);
            }
        });
    }

    private VBox buildForm() {
        cmbNiveau.setItems(FXCollections.observableArrayList(niveauDAO.findAll()));
        cmbNiveau.setPromptText("Choisir Niveau...");
        cmbNiveau.setMaxWidth(Double.MAX_VALUE);

        Button btnSave = new Button("Enregistrer");
        Button btnCancel = new Button("Effacer");
        btnSave.getStyleClass().add("btn-primary");
        btnCancel.getStyleClass().add("btn-secondary");
        btnSave.setOnAction(e -> save());
        btnCancel.setOnAction(e -> clear());

        VBox f = new VBox(formTitle("Matiere"),
                UIHelper.fieldBox("Nom *", txtNom, errNom),
                UIHelper.fieldBox("Niveau *", cmbNiveau, errNiveau),
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
        boolean ok = true;
        if (txtNom.getText().trim().isEmpty()) {
            UIHelper.showFieldError(errNom, txtNom, "Nom obligatoire.");
            ok = false;
        } else
            UIHelper.hideFieldError(errNom, txtNom);

        if (cmbNiveau.getValue() == null) {
            UIHelper.showFieldError(errNiveau, cmbNiveau, "Niveau obligatoire.");
            ok = false;
        } else
            UIHelper.hideFieldError(errNiveau, cmbNiveau);

        if (!ok)
            return;

        Matiere m = new Matiere(editingId < 0 ? 0 : editingId, txtNom.getText().trim(), cmbNiveau.getValue().getId());
        boolean res = editingId < 0 ? dao.insert(m) : dao.update(m);
        if (!res) {
            UIHelper.showErrors(Arrays.asList("Erreur base de donnees."));
            return;
        }
        UIHelper.showSuccess("Matiere enregistree.");
        clear();
        refresh();
    }

    private void delete() {
        Matiere sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            UIHelper.showErrors(Arrays.asList("Selectionnez une matiere."));
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
        cmbNiveau.setValue(null);
        UIHelper.clearMark(txtNom);
        UIHelper.clearMark(cmbNiveau);
        errNom.setVisible(false);
        errNom.setManaged(false);
        errNiveau.setVisible(false);
        errNiveau.setManaged(false);
    }

    private void refresh() {
        table.setItems(FXCollections.observableArrayList(dao.findAll()));
    }

    private Label formTitle(String t) {
        Label l = new Label(t);
        l.getStyleClass().add("form-title");
        return l;
    }

    private TableColumn<Matiere, String> colString(String title, java.util.function.Function<Matiere, String> mapper,
            double width) {
        TableColumn<Matiere, String> c = new TableColumn<>(title);
        c.setCellValueFactory(cd -> {
            if (cd.getValue() == null)
                return null;
            return new javafx.beans.property.SimpleStringProperty(mapper.apply(cd.getValue()));
        });
        c.setPrefWidth(width);
        c.setCellFactory(tc -> new TableCell<Matiere, String>() {
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
