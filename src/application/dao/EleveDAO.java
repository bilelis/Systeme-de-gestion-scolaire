package application.dao;

import application.model.Eleve;
import application.cnx;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EleveDAO {

    public List<Eleve> findAll() {
        List<Eleve> list = new ArrayList<>();
        String sql = "SELECT * FROM eleve ORDER BY nom, prenom";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(map(rs));
            System.out.println("[EleveDAO] Donnees chargees: " + list.size());
        } catch (SQLException e) {
            System.err.println("[EleveDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    public Eleve findById(int id) {
        String sql = "SELECT * FROM eleve WHERE id = ?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return map(rs);
            }
        } catch (SQLException e) {
            System.err.println("[EleveDAO.findById] " + e.getMessage());
        }
        return null;
    }

    public boolean insert(Eleve e) {
        String sql = "INSERT INTO eleve (nom, prenom, date_naissance, adresse, tel_parent) VALUES (?,?,?,?,?)";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getNom());
            ps.setString(2, e.getPrenom());
            ps.setDate(3, Date.valueOf(e.getDateNaissance()));
            ps.setString(4, e.getAdresse());
            ps.setString(5, e.getTelParent());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("[EleveDAO.insert] " + ex.getMessage());
            return false;
        }
    }

    public boolean update(Eleve e) {
        String sql = "UPDATE eleve SET nom=?, prenom=?, date_naissance=?, adresse=?, tel_parent=? WHERE id=?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getNom());
            ps.setString(2, e.getPrenom());
            ps.setDate(3, Date.valueOf(e.getDateNaissance()));
            ps.setString(4, e.getAdresse());
            ps.setString(5, e.getTelParent());
            ps.setInt(6, e.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("[EleveDAO.update] " + ex.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM eleve WHERE id = ?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("[EleveDAO.delete] " + ex.getMessage());
            return false;
        }
    }

    private Eleve map(ResultSet rs) throws SQLException {
        Eleve e = new Eleve();
        e.setId(rs.getInt("id"));
        e.setNom(rs.getString("nom"));
        e.setPrenom(rs.getString("prenom"));
        Date d = rs.getDate("date_naissance");
        if (d != null)
            e.setDateNaissance(d.toLocalDate());
        e.setAdresse(rs.getString("adresse"));
        e.setTelParent(rs.getString("tel_parent"));
        return e;
    }
}
