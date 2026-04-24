package application.dao;

import application.model.Note;
import application.cnx;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteDAO {

    public List<Note> findAll() {
        List<Note> list = new ArrayList<>();
        String sql = "SELECT n.*, CONCAT(e.prenom,' ',e.nom) AS eleve_nom, m.nom AS matiere_nom " +
                "FROM note n " +
                "JOIN eleve e   ON e.id = n.eleve_id " +
                "JOIN matiere m ON m.id = n.matiere_id " +
                "ORDER BY e.nom, m.nom, n.trimestre";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(map(rs));
            System.out.println("[NoteDAO] Notes chargees: " + list.size());
        } catch (SQLException e) {
            System.err.println("[NoteDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    public List<Note> findByEleve(int eleveId) {
        List<Note> list = new ArrayList<>();
        String sql = "SELECT n.*, CONCAT(e.prenom,' ',e.nom) AS eleve_nom, m.nom AS matiere_nom FROM note n JOIN eleve e ON e.id=n.eleve_id JOIN matiere m ON m.id=n.matiere_id WHERE n.eleve_id=? ORDER BY m.nom, n.trimestre";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, eleveId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(map(rs));
            }
        } catch (SQLException e) {
            System.err.println("[NoteDAO.findByEleve] " + e.getMessage());
        }
        return list;
    }

    /** Weighted average: SUM(valeur*coefficient)/SUM(coefficient) for a student */
    public double getMoyenne(int eleveId) {
        String sql = "SELECT SUM(valeur*coefficient)/SUM(coefficient) AS moy FROM note WHERE eleve_id=?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, eleveId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getDouble("moy");
            }
        } catch (SQLException e) {
            System.err.println("[NoteDAO.getMoyenne] " + e.getMessage());
        }
        return 0.0;
    }

    /** Returns all students ranked by weighted average */
    public List<Object[]> getRanking() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT e.id, CONCAT(e.prenom,' ',e.nom) AS nom, " +
                "SUM(n.valeur*n.coefficient)/SUM(n.coefficient) AS moy " +
                "FROM note n JOIN eleve e ON e.id=n.eleve_id " +
                "GROUP BY e.id, nom ORDER BY moy DESC";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(new Object[] { rs.getInt("id"), rs.getString("nom"), rs.getDouble("moy") });
        } catch (SQLException e) {
            System.err.println("[NoteDAO.getRanking] " + e.getMessage());
        }
        return list;
    }

    public boolean insert(Note n) {
        String sql = "INSERT INTO note (eleve_id, matiere_id, trimestre, valeur, coefficient) VALUES (?,?,?,?,?)";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, n.getEleveId());
            ps.setInt(2, n.getMatiereId());
            ps.setInt(3, n.getTrimestre());
            ps.setFloat(4, n.getValeur());
            ps.setInt(5, n.getCoefficient());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[NoteDAO.insert] " + e.getMessage());
            return false;
        }
    }

    public boolean update(Note n) {
        String sql = "UPDATE note SET eleve_id=?, matiere_id=?, trimestre=?, valeur=?, coefficient=? WHERE id=?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, n.getEleveId());
            ps.setInt(2, n.getMatiereId());
            ps.setInt(3, n.getTrimestre());
            ps.setFloat(4, n.getValeur());
            ps.setInt(5, n.getCoefficient());
            ps.setInt(6, n.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[NoteDAO.update] " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM note WHERE id = ?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[NoteDAO.delete] " + e.getMessage());
            return false;
        }
    }

    private Note map(ResultSet rs) throws SQLException {
        Note n = new Note(rs.getInt("id"), rs.getInt("eleve_id"), rs.getInt("matiere_id"),
                rs.getInt("trimestre"), rs.getFloat("valeur"), rs.getInt("coefficient"));
        n.setEleveNom(rs.getString("eleve_nom"));
        n.setMatiereNom(rs.getString("matiere_nom"));
        return n;
    }
}
