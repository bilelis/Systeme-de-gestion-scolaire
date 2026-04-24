package application.dao;

import application.model.Matiere;
import application.cnx;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatiereDAO {

    public List<Matiere> findAll() {
        List<Matiere> list = new ArrayList<>();
        String sql = "SELECT m.*, n.nom AS niveau_nom FROM matiere m JOIN niveau n ON n.id=m.niveau_id ORDER BY m.nom";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(map(rs));
            System.out.println("[MatiereDAO] Donnees chargees: " + list.size());
        } catch (SQLException e) {
            System.err.println("[MatiereDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    public List<Matiere> findByNiveau(int niveauId) {
        List<Matiere> list = new ArrayList<>();
        String sql = "SELECT m.*, n.nom AS niveau_nom FROM matiere m JOIN niveau n ON n.id=m.niveau_id WHERE m.niveau_id=?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, niveauId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(map(rs));
            }
        } catch (SQLException e) {
            System.err.println("[MatiereDAO.findByNiveau] " + e.getMessage());
        }
        return list;
    }

    public boolean insert(Matiere m) {
        String sql = "INSERT INTO matiere (nom, niveau_id) VALUES (?,?)";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getNom());
            ps.setInt(2, m.getNiveauId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MatiereDAO.insert] " + e.getMessage());
            return false;
        }
    }

    public boolean update(Matiere m) {
        String sql = "UPDATE matiere SET nom=?, niveau_id=? WHERE id=?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getNom());
            ps.setInt(2, m.getNiveauId());
            ps.setInt(3, m.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MatiereDAO.update] " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM matiere WHERE id = ?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MatiereDAO.delete] " + e.getMessage());
            return false;
        }
    }

    private Matiere map(ResultSet rs) throws SQLException {
        Matiere m = new Matiere(rs.getInt("id"), rs.getString("nom"), rs.getInt("niveau_id"));
        m.setNiveauNom(rs.getString("niveau_nom"));
        return m;
    }
}
