package application.view;

import application.Login;
import application.model.Enseignant;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Dashboard extends BorderPane {

    public Dashboard(Enseignant ens) {
        getStyleClass().add("dashboard-root");

        // --- TOP BAR WITH ISAAS LOGO ---
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 30, 15, 30));
        topBar.getStyleClass().add("topbar");

        try {
            Image logoImg = new Image(getClass().getResourceAsStream("/application/assets/isaas_logo.png"));
            ImageView logoView = new ImageView(logoImg);
            logoView.setFitHeight(50);
            logoView.setPreserveRatio(true);
            topBar.getChildren().add(logoView);
        } catch (Exception e) {
            System.err.println("Logo non trouve: " + e.getMessage());
            Label textLogo = new Label("ISAAS SFAX");
            textLogo.getStyleClass().add("topbar-title");
            topBar.getChildren().add(textLogo);
        }

        Label appTitle = new Label("Portail Academique");
        appTitle.getStyleClass().add("topbar-title");

        Label userLabel = new Label("Connecte : " + ens.getPrenom() + " " + ens.getNom());
        userLabel.getStyleClass().add("topbar-user");
        userLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: -color-brand-primary;");

        Button btnLogout = new Button("Deconnexion");
        btnLogout.getStyleClass().add("btn-danger-small");
        btnLogout.setOnAction(e -> logout());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(appTitle, spacer, userLabel, btnLogout);
        setTop(topBar);

        // --- MAIN CONTENT ---
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(30, 40, 40, 40));
        mainContainer.setAlignment(Pos.TOP_CENTER);

        Label welcome = new Label("INSTITUT SUPERIEUR D'ADMINISTRATION DES AFFAIRES");
        welcome.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: -color-brand-primary;");

        Label subWelcome = new Label("Systeme de Gestion Integre - Session Academique 2024/2025");
        subWelcome.getStyleClass().add("card-subtitle");
        subWelcome.setPadding(new Insets(0, 0, 25, 0));

        TilePane tiles = new TilePane();
        tiles.setHgap(20);
        tiles.setVgap(20);
        tiles.setAlignment(Pos.CENTER);
        tiles.setPrefColumns(3);

        tiles.getChildren().addAll(
                card("Eleves", "Inscriptions et fiches etudiants", () -> open(new EleveView())),
                card("Enseignants", "Gestion du corps enseignant", () -> open(new EnseignantView())),
                card("Classes", "Groupes de TD et Amphis", () -> open(new ClasseView())),
                card("Matieres", "Unites d'Enseignement (UE)", () -> open(new MatiereView())),
                card("Inscriptions", "Affectations annuelles", () -> open(new InscriptionView())),
                card("Saisie des Notes", "Evaluations et examens", () -> open(new NoteView())),
                card("Bulletins", "Moyennes et Deliberations", () -> open(new BulletinView())),
                card("Niveaux", "Cycles de formation LMD", () -> open(new NiveauView())),
                card("Annees Scolaires", "Configuration du calendrier", () -> open(new AnneeScolaireView())));

        mainContainer.getChildren().addAll(welcome, subWelcome, tiles);

        ScrollPane scroll = new ScrollPane(mainContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent; -fx-background:transparent;");
        setCenter(scroll);

        Label status = new Label("ISAAS Sfax © 2025 - Systeme de Gestion des Etudes - Connecte");
        status.getStyleClass().add("statusbar");
        status.setPadding(new Insets(10, 30, 10, 30));
        status.setMaxWidth(Double.MAX_VALUE);
        setBottom(status);
    }

    private VBox card(String title, String subtitle, Runnable action) {
        Label lbl = new Label(title);
        lbl.getStyleClass().add("card-title");

        Label sub = new Label(subtitle);
        sub.getStyleClass().add("card-subtitle");
        sub.setWrapText(true);
        sub.setAlignment(Pos.CENTER);
        sub.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        VBox box = new VBox(10, lbl, sub);
        box.getStyleClass().add("nav-card");
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(25));
        box.setPrefSize(260, 160);
        box.setOnMouseClicked(e -> action.run());
        return box;
    }

    private void open(Pane view) {
        Stage stage = new Stage();
        Scene scene = new Scene(view, 1150, 750);
        scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("ISAAS Gestion - " + view.getClass().getSimpleName().replace("View", ""));
        stage.show();
    }

    private void logout() {
        Stage stage = (Stage) getScene().getWindow();
        Login login = new Login();
        Scene scene = new Scene(login, 480, 520);
        scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("ISAAS - Connexion");
        stage.setResizable(false);
        stage.centerOnScreen();
    }
}
