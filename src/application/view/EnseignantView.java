package application.view;

import application.dao.EnseignantDAO;
import application.model.Enseignant;
import application.util.UIHelper;
import application.util.Validator;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.Arrays;

public class EnseignantView extends BorderPane {

    private final EnseignantDAO dao = new EnseignantDAO();
    private final TableView<Enseignant> table = new TableView<>();

    private final TextField txtNom = new TextField();
    private final TextField txtPrenom = new TextField();
    private final TextField txtTel = new TextField();
    private final TextField txtUser = new TextField();
    private final PasswordField txtPass = new PasswordField();

    private final Label errNom = UIHelper.makeErrorLabel();
    private final Label errPrenom = UIHelper.makeErrorLabel();
    private final Label errTel = UIHelper.makeErrorLabel();
    private final Label errUser = UIHelper.makeErrorLabel();
    private final Label errPass = UIHelper.makeErrorLabel();

    private int editingId = -1;

    public EnseignantView() {
        getStyleClass().add("view-root");

        // --- TOP BAR ---
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.getStyleClass().add("topbar");

        Label viewTitle = new Label("Gestion des Enseignants");
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
        table.getColumns().add(colString("Nom", e -> e.getNom(), 130));
        table.getColumns().add(colString("Prenom", e -> e.getPrenom(), 130));
        table.getColumns().add(colString("Telephone", e -> e.getTelephone(), 120));
        table.getColumns().add(colString("Utilisateur", e -> e.getUsername(), 130));

        table.getStyleClass().add("main-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().selectedItemProperty().addListener((obs, o, s) -> {
            if (s != null) {
                editingId = s.getId();
                txtNom.setText(s.getNom());
                txtPrenom.setText(s.getPrenom());
                txtTel.setText(s.getTelephone());
                txtUser.setText(s.getUsername());
                txtPass.setText(s.getPassword());
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

        VBox f = new VBox(formTitle("Fiche Enseignant"),
                UIHelper.fieldBox("Nom *", txtNom, errNom),
                UIHelper.fieldBox("Prenom *", txtPrenom, errPrenom),
                UIHelper.fieldBox("Telephone *", txtTel, errTel),
                UIHelper.fieldBox("Utilisateur *", txtUser, errUser),
                UIHelper.fieldBox("Mot de passe *", txtPass, errPass),
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
        if (!Validator.validateName(txtNom.getText(), "Nom").valid) {
            ok = false;
            UIHelper.showFieldError(errNom, txtNom, "Invalide.");
        } else
            UIHelper.hideFieldError(errNom, txtNom);
        if (!Validator.validateName(txtPrenom.getText(), "Prenom").valid) {
            ok = false;
            UIHelper.showFieldError(errPrenom, txtPrenom, "Invalide.");
        } else
            UIHelper.hideFieldError(errPrenom, txtPrenom);
        if (!Validator.validatePhone(txtTel.getText()).valid) {
            ok = false;
            UIHelper.showFieldError(errTel, txtTel, "8 chiffres.");
        } else
            UIHelper.hideFieldError(errTel, txtTel);
        if (txtUser.getText().trim().isEmpty()) {
            ok = false;
            UIHelper.showFieldError(errUser, txtUser, "Obligatoire.");
        } else
            UIHelper.hideFieldError(errUser, txtUser);
        if (txtPass.getText().length() < 4) {
            ok = false;
            UIHelper.showFieldError(errPass, txtPass, "Min 4 chars.");
        } else
            UIHelper.hideFieldError(errPass, txtPass);

        if (!ok)
            return;

        Enseignant en = new Enseignant(editingId < 0 ? 0 : editingId, txtNom.getText().trim(),
                txtPrenom.getText().trim(),
                txtTel.getText().trim(), txtUser.getText().trim(), txtPass.getText());

        boolean res = editingId < 0 ? dao.insert(en) : dao.update(en);
        if (!res) {
            UIHelper.showErrors(Arrays.asList("Erreur base de donnees."));
            return;
        }
        UIHelper.showSuccess("Enseignant enregistre.");
        clear();
        refresh();
    }

    private void delete() {
        Enseignant sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            UIHelper.showErrors(Arrays.asList("Selectionnez un enseignant."));
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
        txtTel.clear();
        txtUser.clear();
        txtPass.clear();
        for (Control c : new Control[] { txtNom, txtPrenom, txtTel, txtUser, txtPass })
            UIHelper.clearMark(c);
        for (Label l : new Label[] { errNom, errPrenom, errTel, errUser, errPass }) {
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

    private TableColumn<Enseignant, String> colString(String title,
            java.util.function.Function<Enseignant, String> mapper, double width) {
        TableColumn<Enseignant, String> c = new TableColumn<>(title);
        c.setCellValueFactory(cd -> {
            if (cd.getValue() == null)
                return null;
            return new javafx.beans.property.SimpleStringProperty(mapper.apply(cd.getValue()));
        });
        c.setPrefWidth(width);
        c.setCellFactory(tc -> new TableCell<Enseignant, String>() {
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
