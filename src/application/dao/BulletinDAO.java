package application.dao;

import application.model.Bulletin;
import application.cnx;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BulletinDAO {

    public List<Bulletin> findAll() {
        List<Bulletin> list = new ArrayList<>();
        String sql = "SELECT b.*, CONCAT(e.prenom,' ',e.nom) AS eleve_nom, a.annee AS annee_label " +
                "FROM bulletin b " +
                "JOIN eleve e         ON e.id = b.eleve_id " +
                "JOIN annee_scolaire a ON a.id = b.annee_id " +
                "ORDER BY b.rang";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(map(rs));
            System.out.println("[BulletinDAO] Bulletins charges: " + list.size());
        } catch (SQLException e) {
            System.err.println("[BulletinDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    /**
     * Generate (insert or update) bulletin for every student in a school year
     * by computing weighted average from the note table.
     */
    public void generateForAnnee(int anneeId) {
        String selectSql = "SELECT e.id, " +
                "SUM(n.valeur*n.coefficient)/SUM(n.coefficient) AS moy " +
                "FROM eleve e " +
                "JOIN inscription i ON i.eleve_id = e.id AND i.annee_id = ? " +
                "JOIN note n        ON n.eleve_id  = e.id " +
                "GROUP BY e.id " +
                "ORDER BY moy DESC";
        String upsertSql = "INSERT INTO bulletin (eleve_id, annee_id, moyenne, rang, appreciation) " +
                "VALUES (?,?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE moyenne=VALUES(moyenne), rang=VALUES(rang), appreciation=VALUES(appreciation)";
        try (Connection con = cnx.getConnexion();
                PreparedStatement sel = con.prepareStatement(selectSql);
                PreparedStatement ups = con.prepareStatement(upsertSql)) {
            sel.setInt(1, anneeId);
            try (ResultSet rs = sel.executeQuery()) {
                int rang = 1;
                while (rs.next()) {
                    double moy = rs.getDouble("moy");
                    String apprec = moy >= 16 ? "Très Bien"
                            : moy >= 14 ? "Bien" : moy >= 12 ? "Assez Bien" : moy >= 10 ? "Passable" : "Insuffisant";
                    ups.setInt(1, rs.getInt("id"));
                    ups.setInt(2, anneeId);
                    ups.setDouble(3, moy);
                    ups.setInt(4, rang++);
                    ups.setString(5, apprec);
                    ups.addBatch();
                }
            }
            ups.executeBatch();
        } catch (SQLException e) {
            System.err.println("[BulletinDAO.generateForAnnee] " + e.getMessage());
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM bulletin WHERE id = ?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BulletinDAO.delete] " + e.getMessage());
            return false;
        }
    }

    private Bulletin map(ResultSet rs) throws SQLException {
        Bulletin b = new Bulletin(rs.getInt("id"), rs.getInt("eleve_id"), rs.getInt("annee_id"),
                rs.getFloat("moyenne"), rs.getInt("rang"), rs.getString("appreciation"));
        b.setEleveNom(rs.getString("eleve_nom"));
        b.setAnneeLabel(rs.getString("annee_label"));
        return b;
    }
}
