package application.model;

public class Note {

    private int id;
    private int eleveId;
    private int matiereId;
    private int trimestre;
    private float valeur;
    private int coefficient;

    // Joined display fields
    private String eleveNom;
    private String matiereNom;

    public Note() {
    }

    public Note(int id, int eleveId, int matiereId, int trimestre, float valeur, int coefficient) {
        this.id = id;
        this.eleveId = eleveId;
        this.matiereId = matiereId;
        this.trimestre = trimestre;
        this.valeur = valeur;
        this.coefficient = coefficient;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEleveId() {
        return eleveId;
    }

    public void setEleveId(int v) {
        this.eleveId = v;
    }

    public int getMatiereId() {
        return matiereId;
    }

    public void setMatiereId(int v) {
        this.matiereId = v;
    }

    public int getTrimestre() {
        return trimestre;
    }

    public void setTrimestre(int v) {
        this.trimestre = v;
    }

    public float getValeur() {
        return valeur;
    }

    public void setValeur(float v) {
        this.valeur = v;
    }

    public int getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(int v) {
        this.coefficient = v;
    }

    public String getEleveNom() {
        return eleveNom;
    }

    public void setEleveNom(String s) {
        this.eleveNom = s;
    }

    public String getMatiereNom() {
        return matiereNom;
    }

    public void setMatiereNom(String s) {
        this.matiereNom = s;
    }
}
