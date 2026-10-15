package com.reservation.dao;

import com.reservation.db.Database;
import com.reservation.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    /** Returns true only if the username exists and the password matches. */
    public boolean authenticate(String username, String password) throws SQLException {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getString(1).equals(PasswordUtil.sha256(password));
            }
        }
    }
}
