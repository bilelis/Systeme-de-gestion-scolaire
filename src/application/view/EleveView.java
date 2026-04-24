package application.view;

import application.dao.EleveDAO;
import application.model.Eleve;
import application.util.UIHelper;
import application.util.Validator;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.Arrays;

public class EleveView extends BorderPane {

    private final EleveDAO dao = new EleveDAO();
    private final TableView<Eleve> table = new TableView<>();

    private final TextField txtNom = new TextField();
    private final TextField txtPrenom = new TextField();
    private final TextField txtDate = new TextField();
    private final TextField txtAdr = new TextField();
    private final TextField txtTel = new TextField();

    private final Label errNom = UIHelper.makeErrorLabel();
    private final Label errPrenom = UIHelper.makeErrorLabel();
    private final Label errDate = UIHelper.makeErrorLabel();
    private final Label errTel = UIHelper.makeErrorLabel();

    private int editingId = -1;

    public EleveView() {
        getStyleClass().add("view-root");

        // --- TOP BAR ---
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.getStyleClass().add("topbar");

        Label viewTitle = new Label("Gestion des Eleves");
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
        table.getColumns().add(colString("ID", e -> String.valueOf(e.getId()), 50));
        table.getColumns().add(colString("Nom", e -> e.getNom(), 140));
        table.getColumns().add(colString("Prenom", e -> e.getPrenom(), 140));
        table.getColumns().add(colString("Date Naiss.",
                e -> e.getDateNaissance() != null ? e.getDateNaissance().format(Validator.DATE_FORMAT) : "", 120));
        table.getColumns().add(colString("Adresse", e -> e.getAdresse(), 200));
        table.getColumns().add(colString("Tel. Parent", e -> e.getTelParent(), 130));

        table.getStyleClass().add("main-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().selectedItemProperty().addListener((obs, o, s) -> {
            if (s != null) {
                editingId = s.getId();
                txtNom.setText(s.getNom());
                txtPrenom.setText(s.getPrenom());
                txtDate.setText(s.getDateNaissance() != null ? s.getDateNaissance().format(Validator.DATE_FORMAT) : "");
                txtAdr.setText(s.getAdresse());
                txtTel.setText(s.getTelParent());
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

        VBox f = new VBox(formTitle("Fiche Eleve"),
                UIHelper.fieldBox("Nom *", txtNom, errNom),
                UIHelper.fieldBox("Prenom *", txtPrenom, errPrenom),
                UIHelper.fieldBox("Date de naissance * (jj/MM/aaaa)", txtDate, errDate),
                UIHelper.fieldBox("Adresse", txtAdr, new Label()),
                UIHelper.fieldBox("Tel. Parent * (8 chiffres)", txtTel, errTel),
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
        Validator.ValidationResult r1 = Validator.validateName(txtNom.getText(), "Nom");
        Validator.ValidationResult r2 = Validator.validateName(txtPrenom.getText(), "Prenom");
        Validator.ValidationResult r3 = Validator.validateDate(txtDate.getText());
        Validator.ValidationResult r4 = Validator.validatePhone(txtTel.getText());

        if (!r1.valid) {
            UIHelper.showFieldError(errNom, txtNom, r1.message);
            ok = false;
        } else
            UIHelper.hideFieldError(errNom, txtNom);
        if (!r2.valid) {
            UIHelper.showFieldError(errPrenom, txtPrenom, r2.message);
            ok = false;
        } else
            UIHelper.hideFieldError(errPrenom, txtPrenom);
        if (!r3.valid) {
            UIHelper.showFieldError(errDate, txtDate, r3.message);
            ok = false;
        } else
            UIHelper.hideFieldError(errDate, txtDate);
        if (!r4.valid) {
            UIHelper.showFieldError(errTel, txtTel, r4.message);
            ok = false;
        } else
            UIHelper.hideFieldError(errTel, txtTel);

        if (!ok)
            return;

        Eleve e = new Eleve(editingId < 0 ? 0 : editingId, txtNom.getText().trim(), txtPrenom.getText().trim(),
                java.time.LocalDate.parse(txtDate.getText().trim(), Validator.DATE_FORMAT),
                txtAdr.getText().trim(), txtTel.getText().trim());

        boolean res = editingId < 0 ? dao.insert(e) : dao.update(e);
        if (!res) {
            UIHelper.showErrors(Arrays.asList("Erreur base de donnees."));
            return;
        }
        UIHelper.showSuccess(editingId < 0 ? "Eleve ajoute." : "Eleve mis a jour.");
        clear();
        refresh();
    }

    private void delete() {
        Eleve sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            UIHelper.showErrors(Arrays.asList("Selectionnez un eleve."));
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
        txtPrenom.clear();
        txtDate.clear();
        txtAdr.clear();
        txtTel.clear();
        for (Control c : new Control[] { txtNom, txtPrenom, txtDate, txtTel })
            UIHelper.clearMark(c);
        for (Label l : new Label[] { errNom, errPrenom, errDate, errTel }) {
            l.setVisible(false);
            l.setManaged(false);
        }
    }

    private void refresh() {
        table.setItems(FXCollections.observableArrayList(dao.findAll()));
    }

    private Label formTitle(String t) {
        Label l = new Label(t);
        l.getStyleClass().add("form-title");
        return l;
    }

    private TableColumn<Eleve, String> colString(String title, java.util.function.Function<Eleve, String> mapper,
            double width) {
        TableColumn<Eleve, String> c = new TableColumn<>(title);
        c.setCellValueFactory(cd -> {
            if (cd.getValue() == null)
                return null;
            return new javafx.beans.property.SimpleStringProperty(mapper.apply(cd.getValue()));
        });
        c.setPrefWidth(width);
        c.setCellFactory(tc -> new TableCell<Eleve, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // FORCE A LABEL GRAPHIC - This is the most aggressive fix
                    Label l = new Label(item);
                    l.setStyle("-fx-text-fill: #1e293b; -fx-font-weight: bold; -fx-font-size: 14px;");
                    setGraphic(l);
                    setText(null);
                    // Debug print to console
                    System.out.println("[DEBUG] Rendering cell: " + item);
                }
            }
        });
        return c;
    }
}
