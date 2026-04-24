package application.model;

import java.time.LocalDate;

public class Affectation {

    private int id;
    private int inscriptionId;
    private int classeId;
    private LocalDate dateAffectation;

    // Joined display fields
    private String eleveNom;
    private String classeNom;

    public Affectation() {
    }

    public Affectation(int id, int inscriptionId, int classeId, LocalDate dateAffectation) {
        this.id = id;
        this.inscriptionId = inscriptionId;
        this.classeId = classeId;
        this.dateAffectation = dateAffectation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInscriptionId() {
        return inscriptionId;
    }

    public void setInscriptionId(int v) {
        this.inscriptionId = v;
    }

    public int getClasseId() {
        return classeId;
    }

    public void setClasseId(int v) {
        this.classeId = v;
    }

    public LocalDate getDateAffectation() {
        return dateAffectation;
    }

    public void setDateAffectation(LocalDate d) {
        this.dateAffectation = d;
    }

    public String getEleveNom() {
        return eleveNom;
    }

    public void setEleveNom(String s) {
        this.eleveNom = s;
    }

    public String getClasseNom() {
        return classeNom;
    }

    public void setClasseNom(String s) {
        this.classeNom = s;
    }
}
