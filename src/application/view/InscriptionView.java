package application.view;

import application.dao.AnneeScolaireDAO;
import application.dao.EleveDAO;
import application.dao.InscriptionDAO;
import application.dao.NiveauDAO;
import application.model.*;
import application.util.UIHelper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.Arrays;

public class InscriptionView extends BorderPane {

    private final InscriptionDAO dao = new InscriptionDAO();
    private final EleveDAO eleveDAO = new EleveDAO();
    private final AnneeScolaireDAO anneeDAO = new AnneeScolaireDAO();
    private final NiveauDAO niveauDAO = new NiveauDAO();

    private final TableView<Inscription> table = new TableView<>();

    private final ComboBox<Eleve> cmbEleve = new ComboBox<>();
    private final ComboBox<AnneeScolaire> cmbAnnee = new ComboBox<>();
    private final ComboBox<Niveau> cmbNiveau = new ComboBox<>();

    private final Label errEleve = UIHelper.makeErrorLabel();
    private final Label errAnnee = UIHelper.makeErrorLabel();
    private final Label errNiveau = UIHelper.makeErrorLabel();

    private int editingId = -1;

    public InscriptionView() {
        getStyleClass().add("view-root");

        // --- TOP BAR ---
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.getStyleClass().add("topbar");

        Label viewTitle = new Label("Gestion des Inscriptions");
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
        table.getColumns().add(colString("ID", i -> String.valueOf(i.getId()), 50));
        table.getColumns().add(colString("Eleve", i -> i.getEleveNom(), 180));
        table.getColumns().add(colString("Annee", i -> i.getAnneeLabel(), 120));
        table.getColumns().add(colString("Niveau", i -> i.getNiveauNom(), 150));

        table.getStyleClass().add("main-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().selectedItemProperty().addListener((obs, o, s) -> {
            if (s != null) {
                editingId = s.getId();
                cmbEleve.getItems().stream().filter(e -> e.getId() == s.getEleveId()).findFirst()
                        .ifPresent(cmbEleve::setValue);
                cmbAnnee.getItems().stream().filter(a -> a.getId() == s.getAnneeId()).findFirst()
                        .ifPresent(cmbAnnee::setValue);
                cmbNiveau.getItems().stream().filter(n -> n.getId() == s.getNiveauId()).findFirst()
                        .ifPresent(cmbNiveau::setValue);
            }
        });
    }

    private VBox buildForm() {
        cmbEleve.setItems(FXCollections.observableArrayList(eleveDAO.findAll()));
        cmbAnnee.setItems(FXCollections.observableArrayList(anneeDAO.findAll()));
        cmbNiveau.setItems(FXCollections.observableArrayList(niveauDAO.findAll()));
        for (ComboBox<?> c : Arrays.asList(cmbEleve, cmbAnnee, cmbNiveau)) {
            c.setPromptText("Selectionner...");
            c.setMaxWidth(Double.MAX_VALUE);
        }

        Button btnSave = new Button("Enregistrer");
        Button btnCancel = new Button("Effacer");
        btnSave.getStyleClass().add("btn-primary");
        btnCancel.getStyleClass().add("btn-secondary");
        btnSave.setOnAction(e -> save());
        btnCancel.setOnAction(e -> clear());

        VBox f = new VBox(formTitle("Inscription"),
                UIHelper.fieldBox("Eleve *", cmbEleve, errEleve),
                UIHelper.fieldBox("Annee Scolaire *", cmbAnnee, errAnnee),
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
        if (cmbEleve.getValue() == null) {
            UIHelper.showFieldError(errEleve, cmbEleve, "Selectionnez un eleve.");
            ok = false;
        } else
            UIHelper.hideFieldError(errEleve, cmbEleve);
        if (cmbAnnee.getValue() == null) {
            UIHelper.showFieldError(errAnnee, cmbAnnee, "Selectionnez une annee.");
            ok = false;
        } else
            UIHelper.hideFieldError(errAnnee, cmbAnnee);
        if (cmbNiveau.getValue() == null) {
            UIHelper.showFieldError(errNiveau, cmbNiveau, "Selectionnez un niveau.");
            ok = false;
        } else
            UIHelper.hideFieldError(errNiveau, cmbNiveau);
        if (!ok)
            return;

        int eleveId = cmbEleve.getValue().getId();
        int anneeId = cmbAnnee.getValue().getId();
        if (dao.isDuplicate(eleveId, anneeId, editingId < 0 ? 0 : editingId)) {
            UIHelper.showErrors(Arrays.asList("Cet eleve est deja inscrit pour cette annee scolaire."));
            return;
        }
        Inscription ins = new Inscription(editingId < 0 ? 0 : editingId, eleveId, anneeId,
                cmbNiveau.getValue().getId());
        boolean res = editingId < 0 ? dao.insert(ins) : dao.update(ins);
        if (!res) {
            UIHelper.showErrors(Arrays.asList("Erreur base de donnees."));
            return;
        }
        UIHelper.showSuccess("Inscription enregistree.");
        clear();
        refresh();
    }

    private void delete() {
        Inscription sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            UIHelper.showErrors(Arrays.asList("Selectionnez une inscription."));
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
        cmbEleve.setValue(null);
        cmbAnnee.setValue(null);
        cmbNiveau.setValue(null);
        for (ComboBox<?> c : Arrays.asList(cmbEleve, cmbAnnee, cmbNiveau))
            UIHelper.clearMark(c);
        for (Label l : new Label[] { errEleve, errAnnee, errNiveau }) {
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

    private TableColumn<Inscription, String> colString(String title,
            java.util.function.Function<Inscription, String> mapper, double width) {
        TableColumn<Inscription, String> c = new TableColumn<>(title);
        c.setCellValueFactory(cd -> {
            if (cd.getValue() == null)
                return null;
            return new javafx.beans.property.SimpleStringProperty(mapper.apply(cd.getValue()));
        });
        c.setPrefWidth(width);
        c.setCellFactory(tc -> new TableCell<Inscription, String>() {
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
