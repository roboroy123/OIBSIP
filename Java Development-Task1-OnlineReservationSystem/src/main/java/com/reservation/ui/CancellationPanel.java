package com.reservation.ui;

import com.reservation.dao.ReservationDao;
import com.reservation.model.Reservation;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Optional;

public class CancellationPanel extends JPanel {
    private final JTextField pnrField = new JTextField(15);
    private final JTextArea detailsArea = new JTextArea(10, 30);
    private final JButton cancelBtn = new JButton("Confirm Cancellation");
    private final ReservationDao dao = new ReservationDao();
    private Reservation current; // the booking currently displayed

    public CancellationPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JButton fetchBtn = new JButton("Fetch");
        fetchBtn.addActionListener(e -> fetch());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("PNR Number:"));
        top.add(pnrField);
        top.add(fetchBtn);

        detailsArea.setEditable(false);
        detailsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        detailsArea.setBorder(BorderFactory.createTitledBorder("Booking Details"));

        cancelBtn.setEnabled(false);
        cancelBtn.addActionListener(e -> cancel());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.add(cancelBtn);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void fetch() {
        String pnr = pnrField.getText().trim();
        reset();
        if (pnr.isEmpty()) {
            warn("Please enter a PNR number.");
            return;
        }
        if (!pnr.matches("\\d{10}")) {
            warn("PNR must be a 10-digit number.");
            return;
        }
        try {
            Optional<Reservation> r = dao.findByPnr(pnr);
            if (r.isPresent()) {
                current = r.get();
                detailsArea.setText(current.toDisplayString());
                cancelBtn.setEnabled(true);
            } else {
                warn("No booking found for PNR " + pnr + ".");
            }
        } catch (SQLException ex) {
            warn("Database error: " + ex.getMessage());
        }
    }

    private void cancel() {
        if (current == null) return;
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel PNR " + current.pnr() + "?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;

        try {
            if (dao.deleteByPnr(current.pnr())) {
                JOptionPane.showMessageDialog(this,
                        "Booking " + current.pnr() + " has been cancelled successfully.",
                        "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                pnrField.setText("");
                reset();
            } else {
                warn("Booking was not found (it may already be cancelled).");
                reset();
            }
        } catch (SQLException ex) {
            warn("Database error: " + ex.getMessage());
        }
    }

    private void reset() {
        current = null;
        detailsArea.setText("");
        cancelBtn.setEnabled(false);
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Notice", JOptionPane.WARNING_MESSAGE);
    }
}
