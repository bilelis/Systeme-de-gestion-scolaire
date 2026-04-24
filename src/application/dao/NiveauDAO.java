package application.dao;

import application.model.Niveau;
import application.cnx;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NiveauDAO {

    public List<Niveau> findAll() {
        List<Niveau> list = new ArrayList<>();
        String sql = "SELECT * FROM niveau ORDER BY nom";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(new Niveau(rs.getInt("id"), rs.getString("nom")));
            System.out.println("[NiveauDAO] Niveaux charges: " + list.size());
        } catch (SQLException e) {
            System.err.println("[NiveauDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    public boolean insert(Niveau n) {
        String sql = "INSERT INTO niveau (nom) VALUES (?)";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, n.getNom());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[NiveauDAO.insert] " + e.getMessage());
            return false;
        }
    }

    public boolean update(Niveau n) {
        String sql = "UPDATE niveau SET nom=? WHERE id=?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, n.getNom());
            ps.setInt(2, n.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[NiveauDAO.update] " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM niveau WHERE id = ?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[NiveauDAO.delete] " + e.getMessage());
            return false;
        }
    }
}
