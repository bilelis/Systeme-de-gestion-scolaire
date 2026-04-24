package application.view;

import application.dao.AnneeScolaireDAO;
import application.model.AnneeScolaire;
import application.util.UIHelper;
import application.util.Validator;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.util.Arrays;

public class AnneeScolaireView extends BorderPane {

    private final AnneeScolaireDAO dao = new AnneeScolaireDAO();
    private final TableView<AnneeScolaire> table = new TableView<>();

    private final TextField txtAnnee = new TextField();
    private final TextField txtDebut = new TextField();
    private final TextField txtFin = new TextField();
    private final Label errAnnee = UIHelper.makeErrorLabel();
    private final Label errDebut = UIHelper.makeErrorLabel();
    private final Label errFin = UIHelper.makeErrorLabel();
    private int editingId = -1;

    public AnneeScolaireView() {
        getStyleClass().add("view-root");

        // --- TOP BAR ---
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.getStyleClass().add("topbar");

        Label viewTitle = new Label("Gestion des Annees");
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
        table.getColumns().add(colString("ID", a -> String.valueOf(a.getId()), 50));
        table.getColumns().add(colString("Annee", a -> a.getAnnee(), 140));
        table.getColumns().add(colString("Debut",
                a -> a.getDateDebut() != null ? a.getDateDebut().format(Validator.DATE_FORMAT) : "", 120));
        table.getColumns().add(
                colString("Fin", a -> a.getDateFin() != null ? a.getDateFin().format(Validator.DATE_FORMAT) : "", 120));

        table.getStyleClass().add("main-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().selectedItemProperty().addListener((obs, o, a) -> {
            if (a != null) {
                editingId = a.getId();
                txtAnnee.setText(a.getAnnee());
                txtDebut.setText(a.getDateDebut() != null ? a.getDateDebut().format(Validator.DATE_FORMAT) : "");
                txtFin.setText(a.getDateFin() != null ? a.getDateFin().format(Validator.DATE_FORMAT) : "");
            }
        });
    }

    private VBox buildForm() {
        txtAnnee.setPromptText("ex: 2025-2026");
        txtDebut.setPromptText("jj/MM/aaaa");
        txtFin.setPromptText("jj/MM/aaaa");
        Button btnSave = new Button("Enregistrer");
        Button btnCancel = new Button("Effacer");
        btnSave.getStyleClass().add("btn-primary");
        btnCancel.getStyleClass().add("btn-secondary");
        btnSave.setOnAction(e -> save());
        btnCancel.setOnAction(e -> clear());
        VBox f = new VBox(formTitle("Annee Scolaire"),
                UIHelper.fieldBox("Annee * (ex: 2025-2026)", txtAnnee, errAnnee),
                UIHelper.fieldBox("Date debut * (jj/MM/aaaa)", txtDebut, errDebut),
                UIHelper.fieldBox("Date fin * (jj/MM/aaaa)", txtFin, errFin),
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
        Validator.ValidationResult r1 = Validator.notEmpty(txtAnnee.getText(), "Annee");
        Validator.ValidationResult r2 = Validator.notEmpty(txtDebut.getText(), "Date debut");
        Validator.ValidationResult r3 = Validator.notEmpty(txtFin.getText(), "Date fin");
        if (!r1.valid) {
            UIHelper.showFieldError(errAnnee, txtAnnee, r1.message);
            ok = false;
        } else
            UIHelper.hideFieldError(errAnnee, txtAnnee);
        if (!r2.valid) {
            UIHelper.showFieldError(errDebut, txtDebut, r2.message);
            ok = false;
        } else
            UIHelper.hideFieldError(errDebut, txtDebut);
        if (!r3.valid) {
            UIHelper.showFieldError(errFin, txtFin, r3.message);
            ok = false;
        } else
            UIHelper.hideFieldError(errFin, txtFin);
        if (!ok)
            return;
        try {
            LocalDate d1 = LocalDate.parse(txtDebut.getText().trim(), Validator.DATE_FORMAT);
            LocalDate d2 = LocalDate.parse(txtFin.getText().trim(), Validator.DATE_FORMAT);
            if (!d2.isAfter(d1)) {
                UIHelper.showErrors(Arrays.asList("La date de fin doit etre apres le debut."));
                return;
            }
            AnneeScolaire a = new AnneeScolaire(editingId < 0 ? 0 : editingId, txtAnnee.getText().trim(), d1, d2);
            boolean res = editingId < 0 ? dao.insert(a) : dao.update(a);
            if (!res) {
                UIHelper.showErrors(Arrays.asList("Erreur base de donnees."));
                return;
            }
            UIHelper.showSuccess("Annee scolaire enregistree.");
            clear();
            refresh();
        } catch (Exception ex) {
            UIHelper.showErrors(Arrays.asList("Format de date invalide (jj/MM/aaaa)."));
        }
    }

    private void delete() {
        AnneeScolaire sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            UIHelper.showErrors(Arrays.asList("Selectionnez une annee."));
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
        txtAnnee.clear();
        txtDebut.clear();
        txtFin.clear();
        for (Control c : new Control[] { txtAnnee, txtDebut, txtFin })
            UIHelper.clearMark(c);
        for (Label l : new Label[] { errAnnee, errDebut, errFin }) {
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

    private TableColumn<AnneeScolaire, String> colString(String title,
            java.util.function.Function<AnneeScolaire, String> mapper, double width) {
        TableColumn<AnneeScolaire, String> c = new TableColumn<>(title);
        c.setCellValueFactory(cd -> {
            if (cd.getValue() == null)
                return null;
            return new javafx.beans.property.SimpleStringProperty(mapper.apply(cd.getValue()));
        });
        c.setPrefWidth(width);
        c.setCellFactory(tc -> new TableCell<AnneeScolaire, String>() {
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
