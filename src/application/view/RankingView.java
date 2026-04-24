package application.view;

import application.dao.NoteDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import java.util.List;

public class RankingView extends BorderPane {

    private final NoteDAO dao;
    private final TableView<Object[]> table = new TableView<>();

    public RankingView(NoteDAO dao) {
        this.dao = dao;
        getStyleClass().add("view-root");

        Label title = new Label("Classement des Élèves");
        title.getStyleClass().add("form-title");
        title.setPadding(new Insets(16, 16, 8, 16));

        buildTable();
        setTop(title);
        setCenter(table);
        refresh();
    }

    @SuppressWarnings("unchecked")
    private void buildTable() {
        TableColumn<Object[], String> colRank = new TableColumn<>("Rang");
        colRank.setCellValueFactory(d -> new SimpleStringProperty("#" + (table.getItems().indexOf(d.getValue()) + 1)));
        colRank.setPrefWidth(70);
        colRank.setCellFactory(tc -> new TableCell<Object[], String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(item);
                int r = getIndex();
                if (r == 0)
                    setStyle("-fx-text-fill:#FFD700;-fx-font-weight:bold;-fx-font-size:15;");
                else if (r == 1)
                    setStyle("-fx-text-fill:#C0C0C0;-fx-font-weight:bold;");
                else if (r == 2)
                    setStyle("-fx-text-fill:#CD7F32;-fx-font-weight:bold;");
                else
                    setStyle("-fx-text-fill:#c8cad8;");
            }
        });

        TableColumn<Object[], String> colName = new TableColumn<>("Élève");
        colName.setCellValueFactory(d -> new SimpleStringProperty((String) d.getValue()[1]));
        colName.setPrefWidth(250);

        TableColumn<Object[], String> colMoy = new TableColumn<>("Moyenne Pondérée");
        colMoy.setCellValueFactory(d -> new SimpleStringProperty(String.format("%.2f / 20", (Double) d.getValue()[2])));
        colMoy.setPrefWidth(160);

        table.getColumns().addAll(colRank, colName, colMoy);
        table.getStyleClass().add("main-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void refresh() {
        List<Object[]> data = dao.getRanking();
        table.setItems(FXCollections.observableArrayList(data));
    }
}
