package application.model;

public class Classe {

    private int id;
    private String nom;
    private int capaciteMax;
    private int niveauId;

    // Joined
    private String niveauNom;

    public Classe() {
    }

    public Classe(int id, String nom, int capaciteMax, int niveauId) {
        this.id = id;
        this.nom = nom;
        this.capaciteMax = capaciteMax;
        this.niveauId = niveauId;
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

    public int getCapaciteMax() {
        return capaciteMax;
    }

    public void setCapaciteMax(int c) {
        this.capaciteMax = c;
    }

    public int getNiveauId() {
        return niveauId;
    }

    public void setNiveauId(int niveauId) {
        this.niveauId = niveauId;
    }

    public String getNiveauNom() {
        return niveauNom;
    }

    public void setNiveauNom(String s) {
        this.niveauNom = s;
    }

    @Override
    public String toString() {
        return nom + (niveauNom != null ? " (" + niveauNom + ")" : "");
    }
}
