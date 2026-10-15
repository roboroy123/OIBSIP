package com.reservation.ui;

import com.reservation.dao.UserDao;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);
    private final UserDao userDao = new UserDao();

    public LoginFrame() {
        super("Online Reservation System - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("Please log in", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2; c.fill = GridBagConstraints.HORIZONTAL;
        form.add(title, c);

        c.gridwidth = 1; c.fill = GridBagConstraints.NONE;
        c.gridy = 1; c.gridx = 0; form.add(new JLabel("Username:"), c);
        c.gridx = 1; form.add(usernameField, c);
        c.gridy = 2; c.gridx = 0; form.add(new JLabel("Password:"), c);
        c.gridx = 1; form.add(passwordField, c);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> doLogin());
        c.gridy = 3; c.gridx = 0; c.gridwidth = 2; c.fill = GridBagConstraints.HORIZONTAL;
        form.add(loginBtn, c);

        setContentPane(form);
        getRootPane().setDefaultButton(loginBtn); // Enter key submits
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password are required.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            if (userDao.authenticate(username, password)) {
                new MainFrame(username).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Access denied: invalid username or password.",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
