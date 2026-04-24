package application.model;

public class Inscription {

    private int id;
    private int eleveId;
    private int anneeId;
    private int niveauId;

    // Joined display fields
    private String eleveNom;
    private String anneeLabel;
    private String niveauNom;

    public Inscription() {
    }

    public Inscription(int id, int eleveId, int anneeId, int niveauId) {
        this.id = id;
        this.eleveId = eleveId;
        this.anneeId = anneeId;
        this.niveauId = niveauId;
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

    public void setEleveId(int eleveId) {
        this.eleveId = eleveId;
    }

    public int getAnneeId() {
        return anneeId;
    }

    public void setAnneeId(int anneeId) {
        this.anneeId = anneeId;
    }

    public int getNiveauId() {
        return niveauId;
    }

    public void setNiveauId(int niveauId) {
        this.niveauId = niveauId;
    }

    public String getEleveNom() {
        return eleveNom;
    }

    public void setEleveNom(String s) {
        this.eleveNom = s;
    }

    public String getAnneeLabel() {
        return anneeLabel;
    }

    public void setAnneeLabel(String s) {
        this.anneeLabel = s;
    }

    public String getNiveauNom() {
        return niveauNom;
    }

    public void setNiveauNom(String s) {
        this.niveauNom = s;
    }
}
