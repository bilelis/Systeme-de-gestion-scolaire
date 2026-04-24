package application.dao;

import application.model.Classe;
import application.cnx;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClasseDAO {

    public List<Classe> findAll() {
        List<Classe> list = new ArrayList<>();
        String sql = "SELECT c.*, n.nom AS niveau_nom " +
                "FROM classe c JOIN niveau n ON n.id = c.niveau_id " +
                "ORDER BY n.nom, c.nom";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(map(rs));
            System.out.println("[ClasseDAO] Donnees chargees: " + list.size());
        } catch (SQLException e) {
            System.err.println("[ClasseDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    public List<Classe> findByNiveau(int niveauId) {
        List<Classe> list = new ArrayList<>();
        String sql = "SELECT c.*, n.nom AS niveau_nom FROM classe c JOIN niveau n ON n.id=c.niveau_id WHERE c.niveau_id=?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, niveauId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(map(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ClasseDAO.findByNiveau] " + e.getMessage());
        }
        return list;
    }

    public int countEleves(int classeId) {
        String sql = "SELECT COUNT(*) FROM affectation WHERE classe_id=?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, classeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[ClasseDAO.countEleves] " + e.getMessage());
        }
        return 0;
    }

    public boolean insert(Classe c) {
        String sql = "INSERT INTO classe (nom, capacite_max, niveau_id) VALUES (?,?,?)";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNom());
            ps.setInt(2, c.getCapaciteMax());
            ps.setInt(3, c.getNiveauId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ClasseDAO.insert] " + e.getMessage());
            return false;
        }
    }

    public boolean update(Classe c) {
        String sql = "UPDATE classe SET nom=?, capacite_max=?, niveau_id=? WHERE id=?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNom());
            ps.setInt(2, c.getCapaciteMax());
            ps.setInt(3, c.getNiveauId());
            ps.setInt(4, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ClasseDAO.update] " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM classe WHERE id = ?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ClasseDAO.delete] " + e.getMessage());
            return false;
        }
    }

    private Classe map(ResultSet rs) throws SQLException {
        Classe c = new Classe(rs.getInt("id"), rs.getString("nom"), rs.getInt("capacite_max"), rs.getInt("niveau_id"));
        c.setNiveauNom(rs.getString("niveau_nom"));
        return c;
    }
}
