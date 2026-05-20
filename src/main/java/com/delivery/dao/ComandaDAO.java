package com.delivery.dao;

import com.delivery.model.Comanda;
import com.delivery.model.StatusComanda;
import com.delivery.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ComandaDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private static final String SELECT_BASE =
        "SELECT c.*, cl.nume AS client_nume, cu.nume AS curier_nume " +
        "FROM comenzi c " +
        "JOIN clienti cl ON c.client_id = cl.id " +
        "JOIN curieri cu ON c.curier_id = cu.id ";

    public List<Comanda> findAll() throws SQLException {
        List<Comanda> list = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(SELECT_BASE + "ORDER BY c.data_comanda DESC")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Comanda> search(String keyword, StatusComanda status) throws SQLException {
        List<Comanda> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_BASE +
            "WHERE (LOWER(cl.nume) LIKE ? OR LOWER(c.adresa_livrare) LIKE ?)");
        if (status != null) sql.append(" AND c.status = ?");
        sql.append(" ORDER BY c.data_comanda DESC");
        try (PreparedStatement ps = getConn().prepareStatement(sql.toString())) {
            String k = "%" + keyword.toLowerCase() + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            if (status != null) ps.setString(3, status.getDbValue());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public void insert(Comanda c) throws SQLException {
        String sql = "INSERT INTO comenzi (client_id,curier_id,adresa_livrare,status,total,data_comanda,observatii) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, c.getClientId());
            ps.setInt(2, c.getCurierId());
            ps.setString(3, c.getAdresaLivrare());
            ps.setString(4, c.getStatus().getDbValue());
            ps.setDouble(5, c.getTotal().doubleValue());
            ps.setString(6, c.getDataComanda().toString());
            ps.setString(7, c.getObservatii());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) c.setId(keys.getInt(1));
            }
        }
    }

    public void update(Comanda c) throws SQLException {
        String sql = "UPDATE comenzi SET client_id=?,curier_id=?,adresa_livrare=?,status=?,total=?,data_comanda=?,observatii=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, c.getClientId());
            ps.setInt(2, c.getCurierId());
            ps.setString(3, c.getAdresaLivrare());
            ps.setString(4, c.getStatus().getDbValue());
            ps.setDouble(5, c.getTotal().doubleValue());
            ps.setString(6, c.getDataComanda().toString());
            ps.setString(7, c.getObservatii());
            ps.setInt(8, c.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = getConn().prepareStatement("DELETE FROM comenzi WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Object[]> reportByStatus() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT status, COUNT(*) AS nr, SUM(total) AS total FROM comenzi GROUP BY status ORDER BY status";
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                list.add(new Object[]{rs.getString("status"), rs.getLong("nr"), BigDecimal.valueOf(rs.getDouble("total"))});
        }
        return list;
    }

    public List<Object[]> reportTopClienti() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT cl.nume, COUNT(c.id) AS nr_comenzi, SUM(c.total) AS total_cheltuit " +
                     "FROM comenzi c JOIN clienti cl ON c.client_id = cl.id " +
                     "WHERE c.status = 'LIVRAT' GROUP BY cl.id, cl.nume ORDER BY total_cheltuit DESC LIMIT 10";
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                list.add(new Object[]{rs.getString("nume"), rs.getLong("nr_comenzi"), BigDecimal.valueOf(rs.getDouble("total_cheltuit"))});
        }
        return list;
    }

    public List<Object[]> reportCurieri() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT cu.nume, cu.vehicul, COUNT(c.id) AS nr_livrari, " +
                     "SUM(CASE WHEN c.status='LIVRAT' THEN c.total ELSE 0 END) AS total_livrat " +
                     "FROM curieri cu LEFT JOIN comenzi c ON cu.id = c.curier_id " +
                     "GROUP BY cu.id, cu.nume, cu.vehicul ORDER BY nr_livrari DESC";
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                list.add(new Object[]{rs.getString("nume"), rs.getString("vehicul"), rs.getLong("nr_livrari"), BigDecimal.valueOf(rs.getDouble("total_livrat"))});
        }
        return list;
    }

    private Comanda map(ResultSet rs) throws SQLException {
        Comanda c = new Comanda(
            rs.getInt("id"),
            rs.getInt("client_id"),
            rs.getInt("curier_id"),
            rs.getString("adresa_livrare"),
            StatusComanda.fromString(rs.getString("status")),
            BigDecimal.valueOf(rs.getDouble("total")),
            LocalDateTime.parse(rs.getString("data_comanda").replace(" ", "T")),
            rs.getString("observatii")
        );
        c.setClientNume(rs.getString("client_nume"));
        c.setCurierNume(rs.getString("curier_nume"));
        return c;
    }
}
