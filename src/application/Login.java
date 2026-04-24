package application;

import application.dao.EnseignantDAO;
import application.model.Enseignant;
import application.util.UIHelper;
import application.view.Dashboard;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Login extends VBox {

    private final EnseignantDAO dao = new EnseignantDAO();

    private TextField txtUser;
    private PasswordField txtPass;
    private Label errUser, errPass, errLogin;

    public Login() {
        getStyleClass().add("dashboard-root");
        setAlignment(Pos.CENTER);
        setPadding(new Insets(30, 50, 50, 50));
        setSpacing(25);

        // --- ISAAS LOGO IN LOGIN ---
        VBox header = new VBox(15);
        header.setAlignment(Pos.CENTER);
        try {
            Image logoImg = new Image(getClass().getResourceAsStream("/application/assets/isaas_logo.png"));
            ImageView logoView = new ImageView(logoImg);
            logoView.setFitHeight(120);
            logoView.setPreserveRatio(true);
            header.getChildren().add(logoView);
        } catch (Exception e) {
            Label placeholder = new Label("ISAAS SFAX");
            placeholder.getStyleClass().add("form-title");
            header.getChildren().add(placeholder);
        }

        Label title = new Label("SYSTEME DE GESTION DES ETUDES");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: -color-brand-primary;");
        Label subtitle = new Label("Identifiez-vous pour acceder au portail");
        subtitle.getStyleClass().add("card-subtitle");
        header.getChildren().addAll(title, subtitle);

        VBox card = new VBox(20);
        card.setMaxWidth(400);
        card.setPadding(new Insets(40));
        card.setStyle(
                "-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-radius: 12; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 25, 0, 0, 15);");

        Label cardHeader = new Label("Identification");
        cardHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: -color-brand-primary;");

        txtUser = new TextField();
        txtUser.setPromptText("Ex: nom.prenom");
        errUser = UIHelper.makeErrorLabel();

        txtPass = new PasswordField();
        txtPass.setPromptText("••••••••");
        errPass = UIHelper.makeErrorLabel();

        Button btnLogin = new Button("Se Connecter");
        btnLogin.getStyleClass().add("btn-primary");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setOnAction(e -> handleLogin());

        errLogin = UIHelper.makeErrorLabel();
        errLogin.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");

        VBox userBox = new VBox(8, new Label("Utilisateur"), txtUser, errUser);
        VBox passBox = new VBox(8, new Label("Mot de passe"), txtPass, errPass);

        card.getChildren().addAll(cardHeader, userBox, passBox, btnLogin, errLogin);
        getChildren().addAll(header, card);
    }

    private void handleLogin() {
        boolean ok = true;
        if (txtUser.getText().trim().isEmpty()) {
            UIHelper.showFieldError(errUser, txtUser, "Champ requis");
            ok = false;
        } else
            UIHelper.hideFieldError(errUser, txtUser);

        if (txtPass.getText().isEmpty()) {
            UIHelper.showFieldError(errPass, txtPass, "Champ requis");
            ok = false;
        } else
            UIHelper.hideFieldError(errPass, txtPass);

        if (!ok)
            return;

        Enseignant ens = dao.findByCredentials(txtUser.getText().trim(), txtPass.getText());
        if (ens == null) {
            errLogin.setText("Identifiants incorrects. Veuillez reessayer.");
            errLogin.setVisible(true);
            UIHelper.markError(txtUser);
            UIHelper.markError(txtPass);
        } else {
            Stage stage = (Stage) getScene().getWindow();
            Dashboard dash = new Dashboard(ens);
            Scene scene = new Scene(dash, 1280, 850);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("ISAAS Sfax - Portail Gestion - " + ens.getPrenom() + " " + ens.getNom());
            stage.centerOnScreen();
        }
    }
}