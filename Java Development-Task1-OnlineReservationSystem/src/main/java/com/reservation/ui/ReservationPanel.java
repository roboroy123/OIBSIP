package com.reservation.ui;

import com.reservation.dao.ReservationDao;
import com.reservation.dao.TrainDao;
import com.reservation.model.Options;
import com.reservation.model.Reservation;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Optional;

public class ReservationPanel extends JPanel {
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    private final JTextField nameField = new JTextField(20);
    private final JTextField trainNoField = new JTextField(20);
    private final JTextField trainNameField = new JTextField(20);
    private final JComboBox<String> classCombo = new JComboBox<>(Options.CLASS_TYPES);
    private final JTextField dateField = new JTextField(20);
    private final JComboBox<String> sourceCombo = new JComboBox<>(Options.STATIONS);
    private final JComboBox<String> destCombo = new JComboBox<>(Options.STATIONS);

    private final TrainDao trainDao = new TrainDao();
    private final ReservationDao reservationDao = new ReservationDao();

    public ReservationPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        trainNameField.setEditable(false);
        destCombo.setSelectedIndex(1);
        dateField.setToolTipText("Format: yyyy-MM-dd");

        addRow(0, "Passenger Name *", nameField);
        addRow(1, "Train Number *", trainNoField);
        addRow(2, "Train Name (auto)", trainNameField);
        addRow(3, "Class Type *", classCombo);
        addRow(4, "Date of Journey * (yyyy-MM-dd)", dateField);
        addRow(5, "From Station *", sourceCombo);
        addRow(6, "To Station *", destCombo);

        JButton bookBtn = new JButton("Book Ticket");
        bookBtn.addActionListener(e -> book());
        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> clearForm());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttons.add(bookBtn);
        buttons.add(clearBtn);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 7; c.gridwidth = 2; c.insets = new Insets(18, 6, 6, 6);
        add(buttons, c);

        // Auto-populate train name as the train number is typed
        trainNoField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { lookupTrain(); }
            public void removeUpdate(DocumentEvent e) { lookupTrain(); }
            public void changedUpdate(DocumentEvent e) { lookupTrain(); }
        });
    }

    private void addRow(int row, String label, JComponent field) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 6, 5, 6);
        c.gridy = row; c.gridx = 0; c.anchor = GridBagConstraints.WEST;
        add(new JLabel(label), c);
        c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1;
        add(field, c);
    }

    private void lookupTrain() {
        String text = trainNoField.getText().trim();
        if (!text.matches("\\d{1,9}")) {
            trainNameField.setText("");
            return;
        }
        try {
            Optional<String> name = trainDao.findNameByNumber(Integer.parseInt(text));
            trainNameField.setText(name.orElse("Train not found"));
        } catch (SQLException ex) {
            trainNameField.setText("");
        }
    }

    private void book() {
        String name = nameField.getText().trim();
        String trainNoText = trainNoField.getText().trim();
        String dateText = dateField.getText().trim();
        String source = (String) sourceCombo.getSelectedItem();
        String dest = (String) destCombo.getSelectedItem();

        // ---- validation ----
        if (name.isEmpty() || trainNoText.isEmpty() || dateText.isEmpty()) {
            error("Passenger name, train number and date of journey are required.");
            return;
        }
        if (!name.matches("[A-Za-z .'-]+")) {
            error("Passenger name may contain only letters, spaces, '.', '-' and apostrophes.");
            return;
        }
        if (!trainNoText.matches("\\d{1,9}")) {
            error("Train number must be numeric.");
            return;
        }
        LocalDate date;
        try {
            date = LocalDate.parse(dateText, DATE_FMT);
        } catch (DateTimeParseException ex) {
            error("Invalid date. Use the format yyyy-MM-dd (e.g. 2026-12-25).");
            return;
        }
        if (date.isBefore(LocalDate.now())) {
            error("Date of journey cannot be in the past.");
            return;
        }
        if (source.equals(dest)) {
            error("Source and destination stations must be different.");
            return;
        }

        // ---- save ----
        try {
            int trainNo = Integer.parseInt(trainNoText);
            Optional<String> trainName = trainDao.findNameByNumber(trainNo);
            if (trainName.isEmpty()) {
                error("Train number " + trainNo + " does not exist.");
                return;
            }
            Reservation r = reservationDao.insert(name, trainNo, trainName.get(),
                    (String) classCombo.getSelectedItem(), date, source, dest);

            JTextArea area = new JTextArea(r.toDisplayString());
            area.setEditable(false);
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            JOptionPane.showMessageDialog(this, area, "Booking Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        } catch (SQLException ex) {
            error("Database error: " + ex.getMessage());
        }
    }

    private void clearForm() {
        nameField.setText("");
        trainNoField.setText("");
        trainNameField.setText("");
        dateField.setText("");
        classCombo.setSelectedIndex(0);
        sourceCombo.setSelectedIndex(0);
        destCombo.setSelectedIndex(1);
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }
}
