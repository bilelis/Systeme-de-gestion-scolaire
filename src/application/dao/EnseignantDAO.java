package application.dao;

import application.model.Enseignant;
import application.cnx;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnseignantDAO {

    public List<Enseignant> findAll() {
        List<Enseignant> list = new ArrayList<>();
        String sql = "SELECT * FROM enseignant ORDER BY nom, prenom";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(map(rs));
            System.out.println("[EnseignantDAO] Donnees chargees: " + list.size());
        } catch (SQLException e) {
            System.err.println("[EnseignantDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    public Enseignant findByCredentials(String username, String password) {
        String sql = "SELECT * FROM enseignant WHERE username=? AND password=?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return map(rs);
            }
        } catch (SQLException e) {
            System.err.println("[EnseignantDAO.findByCredentials] " + e.getMessage());
        }
        return null;
    }

    public boolean isUsernameTaken(String username, int excludeId) {
        String sql = "SELECT id FROM enseignant WHERE username=? AND id!=?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[EnseignantDAO.isUsernameTaken] " + e.getMessage());
        }
        return false;
    }

    public boolean insert(Enseignant t) {
        String sql = "INSERT INTO enseignant (nom, prenom, telephone, username, password) VALUES (?,?,?,?,?)";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getNom());
            ps.setString(2, t.getPrenom());
            ps.setString(3, t.getTelephone());
            ps.setString(4, t.getUsername());
            ps.setString(5, t.getPassword());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("[EnseignantDAO.insert] Duplicate username.");
            return false;
        } catch (SQLException e) {
            System.err.println("[EnseignantDAO.insert] " + e.getMessage());
            return false;
        }
    }

    public boolean update(Enseignant t) {
        String sql = "UPDATE enseignant SET nom=?, prenom=?, telephone=?, username=?, password=? WHERE id=?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getNom());
            ps.setString(2, t.getPrenom());
            ps.setString(3, t.getTelephone());
            ps.setString(4, t.getUsername());
            ps.setString(5, t.getPassword());
            ps.setInt(6, t.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("[EnseignantDAO.update] Duplicate username.");
            return false;
        } catch (SQLException e) {
            System.err.println("[EnseignantDAO.update] " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM enseignant WHERE id = ?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[EnseignantDAO.delete] " + e.getMessage());
            return false;
        }
    }

    private Enseignant map(ResultSet rs) throws SQLException {
        return new Enseignant(rs.getInt("id"), rs.getString("nom"), rs.getString("prenom"),
                rs.getString("telephone"), rs.getString("username"), rs.getString("password"));
    }
}
