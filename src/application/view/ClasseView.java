package application.view;

import application.dao.ClasseDAO;
import application.dao.NiveauDAO;
import application.model.Classe;
import application.model.Niveau;
import application.util.UIHelper;
import application.util.Validator;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.Arrays;

public class ClasseView extends BorderPane {

    private final ClasseDAO classeDAO = new ClasseDAO();
    private final NiveauDAO niveauDAO = new NiveauDAO();
    private final TableView<Classe> table = new TableView<>();

    private final TextField txtNom = new TextField();
    private final TextField txtCap = new TextField();
    private final ComboBox<Niveau> cmbNiveau = new ComboBox<>();
    private final Label errNom = UIHelper.makeErrorLabel();
    private final Label errCap = UIHelper.makeErrorLabel();
    private final Label errNiveau = UIHelper.makeErrorLabel();
    private int editingId = -1;

    public ClasseView() {
        getStyleClass().add("view-root");

        // --- TOP BAR ---
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.getStyleClass().add("topbar");

        Label viewTitle = new Label("Gestion des Classes");
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
        table.getColumns().add(colString("ID", c -> String.valueOf(c.getId()), 50));
        table.getColumns().add(colString("Nom", c -> c.getNom(), 140));
        table.getColumns().add(colString("Capacite", c -> String.valueOf(c.getCapaciteMax()), 80));
        table.getColumns().add(colString("Niveau", c -> c.getNiveauNom(), 160));

        table.getStyleClass().add("main-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().selectedItemProperty().addListener((obs, o, s) -> {
            if (s != null) {
                editingId = s.getId();
                txtNom.setText(s.getNom());
                txtCap.setText(String.valueOf(s.getCapaciteMax()));
                cmbNiveau.getItems().stream().filter(n -> n.getId() == s.getNiveauId()).findFirst()
                        .ifPresent(cmbNiveau::setValue);
            }
        });
    }

    private VBox buildForm() {
        cmbNiveau.setItems(FXCollections.observableArrayList(niveauDAO.findAll()));
        cmbNiveau.setPromptText("Selectionner niveau");
        cmbNiveau.setMaxWidth(Double.MAX_VALUE);
        txtCap.setPromptText("max 20");
        Button btnSave = new Button("Enregistrer");
        Button btnCancel = new Button("Effacer");
        btnSave.getStyleClass().add("btn-primary");
        btnCancel.getStyleClass().add("btn-secondary");
        btnSave.setOnAction(e -> save());
        btnCancel.setOnAction(e -> clear());
        VBox f = new VBox(formTitle("Classe"),
                UIHelper.fieldBox("Nom *", txtNom, errNom),
                UIHelper.fieldBox("Capacite * (<=20)", txtCap, errCap),
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
        Validator.ValidationResult r1 = Validator.notEmpty(txtNom.getText(), "Nom");
        Validator.ValidationResult r2 = Validator.validateCapacity(txtCap.getText());
        if (!r1.valid) {
            UIHelper.showFieldError(errNom, txtNom, r1.message);
            ok = false;
        } else
            UIHelper.hideFieldError(errNom, txtNom);
        if (!r2.valid) {
            UIHelper.showFieldError(errCap, txtCap, r2.message);
            ok = false;
        } else
            UIHelper.hideFieldError(errCap, txtCap);
        if (cmbNiveau.getValue() == null) {
            UIHelper.showFieldError(errNiveau, cmbNiveau, "Selectionnez un niveau.");
            ok = false;
        } else
            UIHelper.hideFieldError(errNiveau, cmbNiveau);
        if (!ok)
            return;
        Classe c = new Classe(editingId < 0 ? 0 : editingId, txtNom.getText().trim(),
                Integer.parseInt(txtCap.getText().trim()), cmbNiveau.getValue().getId());
        boolean res = editingId < 0 ? classeDAO.insert(c) : classeDAO.update(c);
        if (!res) {
            UIHelper.showErrors(Arrays.asList("Erreur base de donnees."));
            return;
        }
        UIHelper.showSuccess("Classe enregistree.");
        clear();
        refresh();
    }

    private void delete() {
        Classe sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            UIHelper.showErrors(Arrays.asList("Selectionnez une classe."));
            return;
        }
        if (!UIHelper.confirmDelete())
            return;
        classeDAO.delete(sel.getId());
        clear();
        refresh();
    }

    private void clear() {
        editingId = -1;
        txtNom.clear();
        txtCap.clear();
        cmbNiveau.setValue(null);
        for (Control c : new Control[] { txtNom, txtCap, cmbNiveau })
            UIHelper.clearMark(c);
        for (Label l : new Label[] { errNom, errCap, errNiveau }) {
            l.setVisible(false);
            l.setManaged(false);
        }
    }

    private void refresh() {
        table.setItems(FXCollections.observableArrayList(classeDAO.findAll()));
    }

    private Label formTitle(String t) {
        Label l = new Label(t);
        l.getStyleClass().add("form-title");
        return l;
    }

    private TableColumn<Classe, String> colString(String title, java.util.function.Function<Classe, String> mapper,
            double width) {
        TableColumn<Classe, String> c = new TableColumn<>(title);
        c.setCellValueFactory(cd -> {
            if (cd.getValue() == null)
                return null;
            return new javafx.beans.property.SimpleStringProperty(mapper.apply(cd.getValue()));
        });
        c.setPrefWidth(width);
        c.setCellFactory(tc -> new TableCell<Classe, String>() {
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
