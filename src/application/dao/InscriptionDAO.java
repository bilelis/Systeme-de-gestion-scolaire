package application.dao;

import application.model.Inscription;
import application.cnx;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InscriptionDAO {

    public List<Inscription> findAll() {
        List<Inscription> list = new ArrayList<>();
        String sql = "SELECT i.id, i.eleve_id, i.annee_id, i.niveau_id, " +
                "CONCAT(e.prenom,' ',e.nom) AS eleve_nom, " +
                "a.annee AS annee_label, " +
                "n.nom   AS niveau_nom " +
                "FROM inscription i " +
                "JOIN eleve e         ON e.id = i.eleve_id " +
                "JOIN annee_scolaire a ON a.id = i.annee_id " +
                "JOIN niveau n         ON n.id = i.niveau_id " +
                "ORDER BY a.annee DESC, e.nom";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(map(rs));
            System.out.println("[InscriptionDAO] Donnees chargees: " + list.size());
        } catch (SQLException e) {
            System.err.println("[InscriptionDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    /** True when the student already has an inscription for this school year. */
    public boolean isDuplicate(int eleveId, int anneeId, int excludeId) {
        String sql = "SELECT id FROM inscription WHERE eleve_id=? AND annee_id=? AND id!=?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, eleveId);
            ps.setInt(2, anneeId);
            ps.setInt(3, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[InscriptionDAO.isDuplicate] " + e.getMessage());
        }
        return false;
    }

    public boolean insert(Inscription ins) {
        String sql = "INSERT INTO inscription (eleve_id, annee_id, niveau_id) VALUES (?,?,?)";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ins.getEleveId());
            ps.setInt(2, ins.getAnneeId());
            ps.setInt(3, ins.getNiveauId());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("[InscriptionDAO.insert] Duplicate inscription.");
            return false;
        } catch (SQLException e) {
            System.err.println("[InscriptionDAO.insert] " + e.getMessage());
            return false;
        }
    }

    public boolean update(Inscription ins) {
        String sql = "UPDATE inscription SET eleve_id=?, annee_id=?, niveau_id=? WHERE id=?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ins.getEleveId());
            ps.setInt(2, ins.getAnneeId());
            ps.setInt(3, ins.getNiveauId());
            ps.setInt(4, ins.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[InscriptionDAO.update] " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM inscription WHERE id = ?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[InscriptionDAO.delete] " + e.getMessage());
            return false;
        }
    }

    private Inscription map(ResultSet rs) throws SQLException {
        Inscription ins = new Inscription(
                rs.getInt("id"), rs.getInt("eleve_id"),
                rs.getInt("annee_id"), rs.getInt("niveau_id"));
        ins.setEleveNom(rs.getString("eleve_nom"));
        ins.setAnneeLabel(rs.getString("annee_label"));
        ins.setNiveauNom(rs.getString("niveau_nom"));
        return ins;
    }
}
