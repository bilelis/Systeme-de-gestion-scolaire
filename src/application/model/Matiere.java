package application.model;

public class Matiere {

    private int id;
    private String nom;
    private int niveauId;

    // Joined
    private String niveauNom;

    public Matiere() {
    }

    public Matiere(int id, String nom, int niveauId) {
        this.id = id;
        this.nom = nom;
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

    public int getNiveauId() {
        return niveauId;
    }

    public void setNiveauId(int v) {
        this.niveauId = v;
    }

    public String getNiveauNom() {
        return niveauNom;
    }

    public void setNiveauNom(String s) {
        this.niveauNom = s;
    }

    @Override
    public String toString() {
        return nom;
    }
}
