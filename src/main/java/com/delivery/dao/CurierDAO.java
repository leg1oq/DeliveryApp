package com.delivery.dao;

import com.delivery.model.Curier;
import com.delivery.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurierDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    public List<Curier> findAll() throws SQLException {
        List<Curier> list = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM curieri ORDER BY id")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Curier> findDisponibili() throws SQLException {
        List<Curier> list = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM curieri WHERE disponibil = 1 ORDER BY id")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Curier> search(String keyword, Boolean disponibil) throws SQLException {
        List<Curier> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT * FROM curieri WHERE (LOWER(nume) LIKE ? OR LOWER(vehicul) LIKE ?)");
        if (disponibil != null) sql.append(" AND disponibil = ?");
        sql.append(" ORDER BY id");
        try (PreparedStatement ps = getConn().prepareStatement(sql.toString())) {
            String k = "%" + keyword.toLowerCase() + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            if (disponibil != null) ps.setInt(3, disponibil ? 1 : 0);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public Curier findById(int id) throws SQLException {
        try (PreparedStatement ps = getConn().prepareStatement("SELECT * FROM curieri WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public void insert(Curier c) throws SQLException {
        String sql = "INSERT INTO curieri (nume,telefon,vehicul,disponibil) VALUES (?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, c.getNume());
            ps.setString(2, c.getTelefon());
            ps.setString(3, c.getVehicul());
            ps.setInt(4, c.isDisponibil() ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) c.setId(keys.getInt(1));
            }
        }
    }

    public void update(Curier c) throws SQLException {
        String sql = "UPDATE curieri SET nume=?,telefon=?,vehicul=?,disponibil=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, c.getNume());
            ps.setString(2, c.getTelefon());
            ps.setString(3, c.getVehicul());
            ps.setInt(4, c.isDisponibil() ? 1 : 0);
            ps.setInt(5, c.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = getConn().prepareStatement("DELETE FROM curieri WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Curier map(ResultSet rs) throws SQLException {
        return new Curier(
            rs.getInt("id"),
            rs.getString("nume"),
            rs.getString("telefon"),
            rs.getString("vehicul"),
            rs.getInt("disponibil") == 1
        );
    }
}
