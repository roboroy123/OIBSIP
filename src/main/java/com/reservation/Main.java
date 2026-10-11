package com.reservation;

import com.reservation.db.Database;
import com.reservation.ui.LoginFrame;

import javax.swing.*;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        try {
            Database.init();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Could not initialise database:\n" + e.getMessage(),
                    "Startup Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
