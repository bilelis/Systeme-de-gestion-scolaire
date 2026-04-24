package application.model;

public class Bulletin {

    private int id;
    private int eleveId;
    private int anneeId;
    private float moyenne;
    private int rang;
    private String appreciation;

    // Joined display
    private String eleveNom;
    private String anneeLabel;

    public Bulletin() {
    }

    public Bulletin(int id, int eleveId, int anneeId, float moyenne, int rang, String appreciation) {
        this.id = id;
        this.eleveId = eleveId;
        this.anneeId = anneeId;
        this.moyenne = moyenne;
        this.rang = rang;
        this.appreciation = appreciation;
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

    public int getAnneeId() {
        return anneeId;
    }

    public void setAnneeId(int v) {
        this.anneeId = v;
    }

    public float getMoyenne() {
        return moyenne;
    }

    public void setMoyenne(float v) {
        this.moyenne = v;
    }

    public int getRang() {
        return rang;
    }

    public void setRang(int v) {
        this.rang = v;
    }

    public String getAppreciation() {
        return appreciation;
    }

    public void setAppreciation(String s) {
        this.appreciation = s;
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
}
