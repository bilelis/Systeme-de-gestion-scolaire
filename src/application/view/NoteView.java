package application.view;

import application.dao.EleveDAO;
import application.dao.MatiereDAO;
import application.dao.NoteDAO;
import application.model.Eleve;
import application.model.Matiere;
import application.model.Note;
import application.util.UIHelper;
import application.util.Validator;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.Arrays;

public class NoteView extends BorderPane {

    private final NoteDAO noteDAO = new NoteDAO();
    private final EleveDAO eleveDAO = new EleveDAO();
    private final MatiereDAO matiereDAO = new MatiereDAO();

    private final TableView<Note> table = new TableView<>();

    private final ComboBox<Eleve> cmbEleve = new ComboBox<>();
    private final ComboBox<Matiere> cmbMatiere = new ComboBox<>();
    private final ComboBox<Integer> cmbTrimestre = new ComboBox<>();
    private final TextField txtNote = new TextField();
    private final TextField txtCoef = new TextField();

    private final Label errEleve = UIHelper.makeErrorLabel();
    private final Label errMatiere = UIHelper.makeErrorLabel();
    private final Label errTrimestre = UIHelper.makeErrorLabel();
    private final Label errNote = UIHelper.makeErrorLabel();
    private final Label errCoef = UIHelper.makeErrorLabel();

    private int editingId = -1;

    public NoteView() {
        getStyleClass().add("view-root");

        // --- TOP BAR ---
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.getStyleClass().add("topbar");

        Label viewTitle = new Label("Saisie des Notes");
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
        table.getColumns().add(colString("ID", n -> String.valueOf(n.getId()), 50));
        table.getColumns().add(colString("Eleve", n -> n.getEleveNom(), 180));
        table.getColumns().add(colString("Matiere", n -> n.getMatiereNom(), 150));
        table.getColumns().add(colString("Trim.", n -> String.valueOf(n.getTrimestre()), 60));
        table.getColumns().add(colString("Note", n -> String.valueOf(n.getValeur()), 70));
        table.getColumns().add(colString("Coef.", n -> String.valueOf(n.getCoefficient()), 60));

        table.getStyleClass().add("main-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().selectedItemProperty().addListener((obs, o, s) -> {
            if (s != null) {
                editingId = s.getId();
                cmbEleve.getItems().stream().filter(e -> e.getId() == s.getEleveId()).findFirst()
                        .ifPresent(cmbEleve::setValue);
                cmbMatiere.getItems().stream().filter(m -> m.getId() == s.getMatiereId()).findFirst()
                        .ifPresent(cmbMatiere::setValue);
                cmbTrimestre.setValue(s.getTrimestre());
                txtNote.setText(String.valueOf(s.getValeur()));
                txtCoef.setText(String.valueOf(s.getCoefficient()));
            }
        });
    }

    private VBox buildForm() {
        cmbEleve.setItems(FXCollections.observableArrayList(eleveDAO.findAll()));
        cmbMatiere.setItems(FXCollections.observableArrayList(matiereDAO.findAll()));
        cmbTrimestre.setItems(FXCollections.observableArrayList(1, 2, 3));

        for (ComboBox<?> c : Arrays.asList(cmbEleve, cmbMatiere, cmbTrimestre)) {
            c.setPromptText("Selectionner...");
            c.setMaxWidth(Double.MAX_VALUE);
        }

        txtCoef.setPromptText("ex: 1");

        Button btnSave = new Button("Enregistrer");
        Button btnCancel = new Button("Effacer");
        btnSave.getStyleClass().add("btn-primary");
        btnCancel.getStyleClass().add("btn-secondary");
        btnSave.setOnAction(e -> save());
        btnCancel.setOnAction(e -> clear());

        VBox f = new VBox(formTitle("Note Evaluation"),
                UIHelper.fieldBox("Eleve *", cmbEleve, errEleve),
                UIHelper.fieldBox("Matiere *", cmbMatiere, errMatiere),
                UIHelper.fieldBox("Trimestre *", cmbTrimestre, errTrimestre),
                UIHelper.fieldBox("Note * (0-20)", txtNote, errNote),
                UIHelper.fieldBox("Coefficient *", txtCoef, errCoef),
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
        if (cmbEleve.getValue() == null) {
            UIHelper.showFieldError(errEleve, cmbEleve, "Eleve requis.");
            ok = false;
        } else
            UIHelper.hideFieldError(errEleve, cmbEleve);

        if (cmbMatiere.getValue() == null) {
            UIHelper.showFieldError(errMatiere, cmbMatiere, "Matiere requise.");
            ok = false;
        } else
            UIHelper.hideFieldError(errMatiere, cmbMatiere);

        if (cmbTrimestre.getValue() == null) {
            UIHelper.showFieldError(errTrimestre, cmbTrimestre, "Trimestre requis.");
            ok = false;
        } else
            UIHelper.hideFieldError(errTrimestre, cmbTrimestre);

        Validator.ValidationResult rNote = Validator.validateGrade(txtNote.getText());
        if (!rNote.valid) {
            UIHelper.showFieldError(errNote, txtNote, rNote.message);
            ok = false;
        } else
            UIHelper.hideFieldError(errNote, txtNote);

        if (txtCoef.getText().trim().isEmpty()) {
            UIHelper.showFieldError(errCoef, txtCoef, "Coef requis.");
            ok = false;
        } else
            UIHelper.hideFieldError(errCoef, txtCoef);

        if (!ok)
            return;

        try {
            float val = Float.parseFloat(txtNote.getText().trim());
            int coef = Integer.parseInt(txtCoef.getText().trim());
            Note n = new Note(editingId < 0 ? 0 : editingId,
                    cmbEleve.getValue().getId(),
                    cmbMatiere.getValue().getId(),
                    cmbTrimestre.getValue(),
                    val,
                    coef);

            boolean res = editingId < 0 ? noteDAO.insert(n) : noteDAO.update(n);
            if (!res) {
                UIHelper.showErrors(Arrays.asList("Erreur base de donnees."));
                return;
            }
            UIHelper.showSuccess("Note enregistree.");
            clear();
            refresh();
        } catch (Exception ex) {
            UIHelper.showErrors(Arrays.asList("Donnees numeriques invalides."));
        }
    }

    private void delete() {
        Note sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            UIHelper.showErrors(Arrays.asList("Selectionnez une note."));
            return;
        }
        if (!UIHelper.confirmDelete())
            return;
        noteDAO.delete(sel.getId());
        clear();
        refresh();
    }

    private void clear() {
        editingId = -1;
        cmbEleve.setValue(null);
        cmbMatiere.setValue(null);
        cmbTrimestre.setValue(null);
        txtNote.clear();
        txtCoef.clear();
        for (Control c : Arrays.asList(cmbEleve, cmbMatiere, cmbTrimestre, txtNote, txtCoef))
            UIHelper.clearMark(c);
        for (Label l : new Label[] { errEleve, errMatiere, errTrimestre, errNote, errCoef }) {
            l.setVisible(false);
            l.setManaged(false);
        }
    }

    private void refresh() {
        table.setItems(FXCollections.observableArrayList(noteDAO.findAll()));
    }

    private Label formTitle(String t) {
        Label l = new Label(t);
        l.getStyleClass().add("form-title");
        return l;
    }

    private TableColumn<Note, String> colString(String title, java.util.function.Function<Note, String> mapper,
            double width) {
        TableColumn<Note, String> c = new TableColumn<>(title);
        c.setCellValueFactory(cd -> {
            if (cd.getValue() == null)
                return null;
            return new javafx.beans.property.SimpleStringProperty(mapper.apply(cd.getValue()));
        });
        c.setPrefWidth(width);
        c.setCellFactory(tc -> new TableCell<Note, String>() {
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
