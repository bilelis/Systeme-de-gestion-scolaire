package application.model;

import java.time.LocalDate;

public class AnneeScolaire {

    private int id;
    private String annee; // e.g. "2025-2026"
    private LocalDate dateDebut;
    private LocalDate dateFin;

    public AnneeScolaire() {
    }

    public AnneeScolaire(int id, String annee, LocalDate dateDebut, LocalDate dateFin) {
        this.id = id;
        this.annee = annee;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAnnee() {
        return annee;
    }

    public void setAnnee(String annee) {
        this.annee = annee;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate d) {
        this.dateDebut = d;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate d) {
        this.dateFin = d;
    }

    @Override
    public String toString() {
        return annee;
    }
}
