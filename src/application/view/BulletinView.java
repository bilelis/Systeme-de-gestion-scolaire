package application.view;

import application.dao.AnneeScolaireDAO;
import application.dao.BulletinDAO;
import application.model.AnneeScolaire;
import application.model.Bulletin;
import application.util.UIHelper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.Arrays;

public class BulletinView extends BorderPane {

    private final BulletinDAO dao = new BulletinDAO();
    private final AnneeScolaireDAO anneeDAO = new AnneeScolaireDAO();

    private final TableView<Bulletin> table = new TableView<>();
    private final ComboBox<AnneeScolaire> cmbAnnee = new ComboBox<>();

    public BulletinView() {
        getStyleClass().add("view-root");

        // --- TOP BAR ---
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.getStyleClass().add("topbar");

        Label viewTitle = new Label("Consultation des Bulletins");
        viewTitle.getStyleClass().add("topbar-title");

        cmbAnnee.setItems(FXCollections.observableArrayList(anneeDAO.findAll()));
        cmbAnnee.setPromptText("Annee Scolaire...");
        cmbAnnee.setPrefWidth(200);

        Button btnRefresh = new Button("Afficher / Actualiser");
        btnRefresh.getStyleClass().add("btn-secondary");
        btnRefresh.setOnAction(e -> refresh());

        Button btnGenerate = new Button("Generer Bulletins");
        btnGenerate.getStyleClass().add("btn-primary");
        btnGenerate.setOnAction(e -> generate());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(viewTitle, cmbAnnee, btnRefresh, spacer, btnGenerate);
        setTop(topBar);

        buildTable();
        setCenter(table);
        setBottom(buildToolbar());

        refresh();
    }

    @SuppressWarnings("unchecked")
    private void buildTable() {
        table.getColumns().clear();
        table.getColumns().add(colString("Rang", b -> String.valueOf(b.getRang()), 60));
        table.getColumns().add(colString("Eleve", b -> b.getEleveNom(), 200));
        table.getColumns().add(colString("Annee", b -> b.getAnneeLabel(), 120));
        table.getColumns().add(colString("Moyenne", b -> String.format("%.2f", b.getMoyenne()), 90));
        table.getColumns().add(colString("Appreciation", b -> b.getAppreciation(), 200));

        // Specialized cell factory for the rank with colors
        TableColumn<Bulletin, String> rangCol = (TableColumn<Bulletin, String>) table.getColumns().get(0);
        rangCol.setCellFactory(tc -> new TableCell<Bulletin, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                Label l = new Label("#" + item);
                if (item.equals("1"))
                    l.setStyle("-fx-text-fill:#FFD700; -fx-font-weight:bold; -fx-font-size: 15px;");
                else if (item.equals("2"))
                    l.setStyle("-fx-text-fill:#C0C0C0; -fx-font-weight:bold; -fx-font-size: 14px;");
                else if (item.equals("3"))
                    l.setStyle("-fx-text-fill:#CD7F32; -fx-font-weight:bold; -fx-font-size: 14px;");
                else
                    l.setStyle("-fx-text-fill:#1e293b; -fx-font-weight: bold;");
                setGraphic(l);
                setText(null);
            }
        });

        table.getStyleClass().add("main-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private HBox buildToolbar() {
        Button btnDel = new Button("Supprimer Bulletin");
        btnDel.getStyleClass().add("btn-danger");
        btnDel.setOnAction(e -> delete());
        HBox bar = new HBox(btnDel);
        bar.setPadding(new Insets(10, 16, 10, 16));
        bar.getStyleClass().add("toolbar");
        return bar;
    }

    private void generate() {
        AnneeScolaire sel = cmbAnnee.getValue();
        if (sel == null) {
            UIHelper.showErrors(Arrays.asList("Selectionnez une annee scolaire."));
            return;
        }
        dao.generateForAnnee(sel.getId());
        UIHelper.showSuccess("Bulletins generes pour " + sel.getAnnee() + ".");
        refresh();
    }

    private void delete() {
        Bulletin sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            UIHelper.showErrors(Arrays.asList("Selectionnez un bulletin."));
            return;
        }
        if (!UIHelper.confirmDelete())
            return;
        dao.delete(sel.getId());
        refresh();
    }

    private void refresh() {
        table.setItems(FXCollections.observableArrayList(dao.findAll()));
    }

    private TableColumn<Bulletin, String> colString(String title, java.util.function.Function<Bulletin, String> mapper,
            double width) {
        TableColumn<Bulletin, String> c = new TableColumn<>(title);
        c.setCellValueFactory(cd -> {
            if (cd.getValue() == null)
                return null;
            return new javafx.beans.property.SimpleStringProperty(mapper.apply(cd.getValue()));
        });
        c.setPrefWidth(width);
        c.setCellFactory(tc -> new TableCell<Bulletin, String>() {
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
