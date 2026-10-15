package com.reservation.dao;

import com.reservation.db.Database;
import com.reservation.model.Reservation;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

/** All SQL uses PreparedStatement to prevent SQL injection. */
public class ReservationDao {
    private static final SecureRandom RANDOM = new SecureRandom();

    /** Saves the booking and returns it with a freshly generated, unique PNR. */
    public Reservation insert(String passengerName, int trainNumber, String trainName,
                              String classType, LocalDate date, String source, String destination)
            throws SQLException {
        String sql = """
                INSERT INTO reservations
                (pnr, passenger_name, train_number, train_name, class_type, journey_date, source, destination)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)""";
        try (Connection con = Database.getConnection()) {
            String pnr = generateUniquePnr(con);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, pnr);
                ps.setString(2, passengerName);
                ps.setInt(3, trainNumber);
                ps.setString(4, trainName);
                ps.setString(5, classType);
                ps.setString(6, date.toString());
                ps.setString(7, source);
                ps.setString(8, destination);
                ps.executeUpdate();
            }
            return new Reservation(pnr, passengerName, trainNumber, trainName,
                    classType, date, source, destination);
        }
    }

    public Optional<Reservation> findByPnr(String pnr) throws SQLException {
        String sql = "SELECT * FROM reservations WHERE pnr = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pnr);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(new Reservation(
                        rs.getString("pnr"),
                        rs.getString("passenger_name"),
                        rs.getInt("train_number"),
                        rs.getString("train_name"),
                        rs.getString("class_type"),
                        LocalDate.parse(rs.getString("journey_date")),
                        rs.getString("source"),
                        rs.getString("destination")));
            }
        }
    }

    /** @return true if a booking was actually deleted. */
    public boolean deleteByPnr(String pnr) throws SQLException {
        String sql = "DELETE FROM reservations WHERE pnr = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pnr);
            return ps.executeUpdate() > 0;
        }
    }

    private String generateUniquePnr(Connection con) throws SQLException {
        String sql = "SELECT 1 FROM reservations WHERE pnr = ?";
        while (true) {
            // 10-digit number, never starting with 0
            String pnr = String.valueOf(1_000_000_000L + (long) (RANDOM.nextDouble() * 9_000_000_000L));
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, pnr);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return pnr;
                }
            }
        }
    }
}
