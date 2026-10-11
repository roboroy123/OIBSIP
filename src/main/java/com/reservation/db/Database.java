package com.reservation.db;

import com.reservation.util.PasswordUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/** Opens SQLite connections and creates/seeds the schema on first run. */
public final class Database {
    private static final String URL = "jdbc:sqlite:reservation.db";

    private Database() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void init() throws SQLException {
        try (Connection con = getConnection(); Statement st = con.createStatement()) {
            st.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        username      TEXT PRIMARY KEY,
                        password_hash TEXT NOT NULL
                    )""");
            st.execute("""
                    CREATE TABLE IF NOT EXISTS trains (
                        train_number INTEGER PRIMARY KEY,
                        train_name   TEXT NOT NULL
                    )""");
            st.execute("""
                    CREATE TABLE IF NOT EXISTS reservations (
                        pnr            TEXT PRIMARY KEY,
                        passenger_name TEXT NOT NULL,
                        train_number   INTEGER NOT NULL,
                        train_name     TEXT NOT NULL,
                        class_type     TEXT NOT NULL,
                        journey_date   TEXT NOT NULL,
                        source         TEXT NOT NULL,
                        destination    TEXT NOT NULL,
                        FOREIGN KEY (train_number) REFERENCES trains(train_number)
                    )""");
        }
        seed();
    }

    private static void seed() throws SQLException {
        try (Connection con = getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT OR IGNORE INTO users(username, password_hash) VALUES (?, ?)")) {
                ps.setString(1, "admin");
                ps.setString(2, PasswordUtil.sha256("admin123"));
                ps.executeUpdate();
            }
            Object[][] trains = {
                    {12621, "Tamil Nadu Express"},
                    {12007, "Shatabdi Express"},
                    {12840, "Howrah Mail"},
                    {12163, "Dadar Chennai Express"},
                    {12601, "Chennai Mumbai Mail"},
                    {12649, "Karnataka Sampark Kranti"},
                    {12695, "Trivandrum Express"},
                    {22625, "Double Decker Express"}
            };
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT OR IGNORE INTO trains(train_number, train_name) VALUES (?, ?)")) {
                for (Object[] t : trains) {
                    ps.setInt(1, (Integer) t[0]);
                    ps.setString(2, (String) t[1]);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }
}
