package application.dao;

import application.model.AnneeScolaire;
import application.cnx;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnneeScolaireDAO {

    public List<AnneeScolaire> findAll() {
        List<AnneeScolaire> list = new ArrayList<>();
        String sql = "SELECT * FROM annee_scolaire ORDER BY annee DESC";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(map(rs));
            System.out.println("[AnneeScolaireDAO] Annees chargees: " + list.size());
        } catch (SQLException e) {
            System.err.println("[AnneeScolaireDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    public boolean insert(AnneeScolaire a) {
        String sql = "INSERT INTO annee_scolaire (annee, date_debut, date_fin) VALUES (?,?,?)";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getAnnee());
            ps.setDate(2, Date.valueOf(a.getDateDebut()));
            ps.setDate(3, Date.valueOf(a.getDateFin()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AnneeScolaireDAO.insert] " + e.getMessage());
            return false;
        }
    }

    public boolean update(AnneeScolaire a) {
        String sql = "UPDATE annee_scolaire SET annee=?, date_debut=?, date_fin=? WHERE id=?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getAnnee());
            ps.setDate(2, Date.valueOf(a.getDateDebut()));
            ps.setDate(3, Date.valueOf(a.getDateFin()));
            ps.setInt(4, a.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AnneeScolaireDAO.update] " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM annee_scolaire WHERE id = ?";
        try (Connection con = cnx.getConnexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AnneeScolaireDAO.delete] " + e.getMessage());
            return false;
        }
    }

    private AnneeScolaire map(ResultSet rs) throws SQLException {
        AnneeScolaire a = new AnneeScolaire();
        a.setId(rs.getInt("id"));
        a.setAnnee(rs.getString("annee"));
        Date d1 = rs.getDate("date_debut");
        Date d2 = rs.getDate("date_fin");
        if (d1 != null)
            a.setDateDebut(d1.toLocalDate());
        if (d2 != null)
            a.setDateFin(d2.toLocalDate());
        return a;
    }
}
