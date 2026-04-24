package application.util;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Shared helpers for consistent UI styling and validation display.
 */
public class UIHelper {

    public static void markError(Control ctrl) {
        ctrl.getStyleClass().remove("field-ok");
        if (!ctrl.getStyleClass().contains("error-field"))
            ctrl.getStyleClass().add("error-field");
    }

    public static void markOk(Control ctrl) {
        ctrl.getStyleClass().remove("error-field");
    }

    public static void clearMark(Control ctrl) {
        ctrl.getStyleClass().removeAll("error-field", "field-ok");
    }

    public static void showErrors(List<String> errors) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de Validation");
        alert.setHeaderText("Des corrections sont necessaires :");
        alert.setContentText(String.join("\n", errors));

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(UIHelper.class.getResource("/application/application.css").toExternalForm());

        alert.showAndWait();
    }

    public static void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(UIHelper.class.getResource("/application/application.css").toExternalForm());

        alert.showAndWait();
    }

    public static boolean confirmDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de Suppression");
        alert.setHeaderText("Voulez-vous supprimer cet enregistrement ?");
        alert.setContentText("Cette operation est irreversible.");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(UIHelper.class.getResource("/application/application.css").toExternalForm());

        return alert.showAndWait().filter(r -> r == ButtonType.OK).isPresent();
    }

    public static Label makeErrorLabel() {
        Label lbl = new Label();
        lbl.getStyleClass().add("error-label");
        lbl.setVisible(false);
        lbl.setManaged(false);
        return lbl;
    }

    public static void showFieldError(Label lbl, Control field, String message) {
        lbl.setText(message);
        lbl.setVisible(true);
        lbl.setManaged(true);
        markError(field);
    }

    public static void hideFieldError(Label lbl, Control field) {
        lbl.setVisible(false);
        lbl.setManaged(false);
        markOk(field);
    }

    public static VBox fieldBox(String labelText, Control field, Label errorLbl) {
        Label lbl = new Label(labelText);
        lbl.getStyleClass().add("section-label");
        VBox box = new VBox(2, lbl, field, errorLbl);
        box.setPadding(new Insets(0, 0, 12, 0));
        return box;
    }
}
