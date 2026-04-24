package application.model;

import java.time.LocalDate;

public class Eleve {

    private int id;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String adresse;
    private String telParent;

    public Eleve() {
    }

    public Eleve(int id, String nom, String prenom, LocalDate dateNaissance, String adresse, String telParent) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.adresse = adresse;
        this.telParent = telParent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate d) {
        this.dateNaissance = d;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelParent() {
        return telParent;
    }

    public void setTelParent(String tel) {
        this.telParent = tel;
    }

    @Override
    public String toString() {
        return prenom + " " + nom;
    }
}
