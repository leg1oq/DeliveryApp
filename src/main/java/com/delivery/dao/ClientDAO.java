package com.delivery.dao;

import com.delivery.model.Client;
import com.delivery.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    public List<Client> findAll() throws SQLException {
        List<Client> list = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM clienti ORDER BY id")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Client> search(String keyword) throws SQLException {
        List<Client> list = new ArrayList<>();
        String sql = "SELECT * FROM clienti WHERE LOWER(nume) LIKE ? OR LOWER(email) LIKE ? OR telefon LIKE ? ORDER BY id";
        String k = "%" + keyword.toLowerCase() + "%";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, k); ps.setString(2, k); ps.setString(3, k);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public Client findById(int id) throws SQLException {
        try (PreparedStatement ps = getConn().prepareStatement("SELECT * FROM clienti WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public void insert(Client c) throws SQLException {
        String sql = "INSERT INTO clienti (nume,email,telefon,adresa,data_inregistrare) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, c.getNume());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getTelefon());
            ps.setString(4, c.getAdresa());
            ps.setString(5, c.getDataInregistrare().toString());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) c.setId(keys.getInt(1));
            }
        }
    }

    public void update(Client c) throws SQLException {
        String sql = "UPDATE clienti SET nume=?,email=?,telefon=?,adresa=?,data_inregistrare=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, c.getNume());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getTelefon());
            ps.setString(4, c.getAdresa());
            ps.setString(5, c.getDataInregistrare().toString());
            ps.setInt(6, c.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = getConn().prepareStatement("DELETE FROM clienti WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Client map(ResultSet rs) throws SQLException {
        return new Client(
            rs.getInt("id"),
            rs.getString("nume"),
            rs.getString("email"),
            rs.getString("telefon"),
            rs.getString("adresa"),
            LocalDate.parse(rs.getString("data_inregistrare"))
        );
    }
}
