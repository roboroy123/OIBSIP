import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Online Examination System (Java + Swing)
 *
 * Compile:  javac ExamSystem.java
 * Run:      java ExamSystem
 *
 * Demo logins:  student / pass123   |   alice / alice123
 *
 * Screens (switched with CardLayout):
 *   LOGIN -> PROFILE -> EXAM -> RESULT -> (logout) -> LOGIN
 */
public class ExamSystem extends JFrame {

    // ====================== CONFIGURATION ======================
    static final int EXAM_MINUTES = 30;                 // change to e.g. 1 to test auto-submit
    static final int EXAM_SECONDS = EXAM_MINUTES * 60;

    // ====================== DATA MODELS ======================
    static class User {
        final String username;
        String password;
        String displayName;

        User(String username, String password, String displayName) {
            this.username = username;
            this.password = password;
            this.displayName = displayName;
        }
    }

    static class Question {
        final String text;
        final String[] options;   // always 4
        final int correct;        // index 0..3

        Question(String text, int correct, String... options) {
            this.text = text;
            this.correct = correct;
            this.options = options;
        }
    }

    // ====================== APP STATE ======================
    private final Map<String, User> users = new HashMap<>();
    private final List<Question> questions = new ArrayList<>();
    private User currentUser;

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    private LoginPanel loginPanel;
    private ProfilePanel profilePanel;
    private ExamPanel examPanel;
    private ResultPanel resultPanel;

    private static final String LOGIN = "LOGIN", PROFILE = "PROFILE", EXAM = "EXAM", RESULT = "RESULT";

    // ====================== CONSTRUCTOR ======================
    public ExamSystem() {
        super("Online Examination System");
        seedUsers();
        seedQuestions();

        loginPanel = new LoginPanel();
        profilePanel = new ProfilePanel();
        examPanel = new ExamPanel();
        resultPanel = new ResultPanel();

        root.add(loginPanel, LOGIN);
        root.add(profilePanel, PROFILE);
        root.add(examPanel, EXAM);
        root.add(resultPanel, RESULT);
        add(root);

        // Session management: intercept the window close button
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClose();
            }
        });

        setSize(820, 600);
        setMinimumSize(new Dimension(720, 520));
        setLocationRelativeTo(null);
        showScreen(LOGIN);
    }

    private void showScreen(String name) {
        cards.show(root, name);
    }

    private void handleWindowClose() {
        if (examPanel.isExamInProgress()) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to quit?\nYour exam will NOT be submitted and progress will be lost.",
                    "Quit Exam", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) return;
            examPanel.abort();
        }
        dispose();
        System.exit(0);
    }

    // ====================== SEED DATA ======================
    private void seedUsers() {
        users.put("student", new User("student", "pass123", "Student"));
        users.put("alice", new User("alice", "alice123", "Alice"));
    }

    private void seedQuestions() {
        questions.add(new Question("Which keyword is used to inherit a class in Java?", 1,
                "implements", "extends", "inherits", "super"));
        questions.add(new Question("Which of these is NOT a primitive data type in Java?", 2,
                "int", "boolean", "String", "char"));
        questions.add(new Question("What is the default value of an int instance variable?", 0,
                "0", "null", "undefined", "-1"));
        questions.add(new Question("Which collection does NOT allow duplicate elements?", 3,
                "ArrayList", "LinkedList", "Vector", "HashSet"));
        questions.add(new Question("Which method is the entry point of a Java application?", 2,
                "start()", "run()", "main()", "init()"));
        questions.add(new Question("Which access modifier makes a member visible only inside its own class?", 3,
                "public", "protected", "default (package-private)", "private"));
        questions.add(new Question("Which Swing class is used to group radio buttons so only one can be selected?", 1,
                "JPanelGroup", "ButtonGroup", "RadioManager", "JToggleGroup"));
        questions.add(new Question("What does the 'final' keyword on a variable mean?", 0,
                "Its value cannot be reassigned", "It is stored on the heap only",
                "It is visible to all packages", "It is automatically synchronized"));
        questions.add(new Question("Which exception is thrown when dividing an int by zero?", 2,
                "NullPointerException", "NumberFormatException", "ArithmeticException", "IOException"));
        questions.add(new Question("Which interface must a class implement to be run by a Thread?", 1,
                "Callable", "Runnable", "Executor", "Serializable"));
    }

    // =====================================================================
    //  SCREEN 1: LOGIN
    // =====================================================================
    private class LoginPanel extends JPanel {
        private final JTextField userField = new JTextField(16);
        private final JPasswordField passField = new JPasswordField(16);
        private final JLabel errorLabel = new JLabel(" ");

        LoginPanel() {
            setLayout(new GridBagLayout());
            JPanel box = new JPanel(new GridBagLayout());
            box.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(24, 32, 24, 32)));

            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(6, 6, 6, 6);
            c.fill = GridBagConstraints.HORIZONTAL;

            JLabel title = new JLabel("Online Examination System", SwingConstants.CENTER);
            title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
            c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
            box.add(title, c);

            JLabel sub = new JLabel("Please log in to continue", SwingConstants.CENTER);
            sub.setForeground(Color.GRAY);
            c.gridy = 1;
            box.add(sub, c);

            c.gridwidth = 1; c.gridy = 2; c.gridx = 0;
            box.add(new JLabel("Username:"), c);
            c.gridx = 1;
            box.add(userField, c);

            c.gridy = 3; c.gridx = 0;
            box.add(new JLabel("Password:"), c);
            c.gridx = 1;
            box.add(passField, c);

            errorLabel.setForeground(new Color(190, 30, 30));
            c.gridy = 4; c.gridx = 0; c.gridwidth = 2;
            box.add(errorLabel, c);

            JButton loginBtn = new JButton("Login");
            c.gridy = 5;
            box.add(loginBtn, c);

            JLabel hint = new JLabel("Demo: student / pass123   or   alice / alice123", SwingConstants.CENTER);
            hint.setFont(hint.getFont().deriveFont(Font.ITALIC, 11f));
            hint.setForeground(Color.GRAY);
            c.gridy = 6;
            box.add(hint, c);

            add(box);

            ActionListener doLogin = e -> attemptLogin();
            loginBtn.addActionListener(doLogin);
            userField.addActionListener(doLogin);
            passField.addActionListener(doLogin);
        }

        private void attemptLogin() {
            String u = userField.getText().trim();
            String p = new String(passField.getPassword());
            User user = users.get(u);
            if (user != null && user.password.equals(p)) {
                currentUser = user;
                errorLabel.setText(" ");
                profilePanel.load(user);
                showScreen(PROFILE);
            } else {
                errorLabel.setText("Invalid username or password.");
                passField.setText("");
            }
        }

        void reset() {
            userField.setText("");
            passField.setText("");
            errorLabel.setText(" ");
            userField.requestFocusInWindow();
        }
    }

    // =====================================================================
    //  SCREEN 2: PROFILE UPDATE (before starting the exam)
    // =====================================================================
    private class ProfilePanel extends JPanel {
        private final JLabel welcome = new JLabel();
        private final JTextField nameField = new JTextField(18);
        private final JPasswordField newPass = new JPasswordField(18);
        private final JPasswordField confirmPass = new JPasswordField(18);
        private final JLabel status = new JLabel(" ");

        ProfilePanel() {
            setLayout(new GridBagLayout());
            JPanel box = new JPanel(new GridBagLayout());
            box.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(24, 32, 24, 32)));

            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(6, 6, 6, 6);
            c.fill = GridBagConstraints.HORIZONTAL;

            welcome.setFont(welcome.getFont().deriveFont(Font.BOLD, 20f));
            c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
            box.add(welcome, c);

            JLabel info = new JLabel("<html>Exam: " + questions.size() + " questions, " + EXAM_MINUTES
                    + " minutes.<br>You may update your profile before starting.</html>");
            info.setForeground(Color.DARK_GRAY);
            c.gridy = 1;
            box.add(info, c);

            c.gridwidth = 1; c.gridy = 2; c.gridx = 0;
            box.add(new JLabel("Display name:"), c);
            c.gridx = 1;
            box.add(nameField, c);

            c.gridy = 3; c.gridx = 0;
            box.add(new JLabel("New password:"), c);
            c.gridx = 1;
            box.add(newPass, c);

            c.gridy = 4; c.gridx = 0;
            box.add(new JLabel("Confirm password:"), c);
            c.gridx = 1;
            box.add(confirmPass, c);

            JLabel note = new JLabel("(leave password blank to keep the current one)");
            note.setFont(note.getFont().deriveFont(Font.ITALIC, 11f));
            note.setForeground(Color.GRAY);
            c.gridy = 5; c.gridx = 0; c.gridwidth = 2;
            box.add(note, c);

            c.gridy = 6;
            box.add(status, c);

            JPanel buttons = new JPanel(new GridLayout(1, 3, 8, 0));
            JButton save = new JButton("Save Changes");
            JButton start = new JButton("Start Exam");
            JButton back = new JButton("Log Out");
            start.setFont(start.getFont().deriveFont(Font.BOLD));
            buttons.add(back);
            buttons.add(save);
            buttons.add(start);
            c.gridy = 7;
            box.add(buttons, c);

            add(box);

            save.addActionListener(e -> saveProfile());
            back.addActionListener(e -> logout());
            start.addActionListener(e -> {
                // Save any pending edits first; abort if they're invalid
                if (hasPendingEdits() && !saveProfile()) return;
                examPanel.startExam(currentUser);
                showScreen(EXAM);
            });
        }

        void load(User u) {
            welcome.setText("Welcome, " + u.displayName + "!");
            nameField.setText(u.displayName);
            newPass.setText("");
            confirmPass.setText("");
            status.setText(" ");
        }

        private boolean hasPendingEdits() {
            return !nameField.getText().trim().equals(currentUser.displayName)
                    || newPass.getPassword().length > 0 || confirmPass.getPassword().length > 0;
        }

        private boolean saveProfile() {
            String name = nameField.getText().trim();
            String p1 = new String(newPass.getPassword());
            String p2 = new String(confirmPass.getPassword());

            if (name.isEmpty()) {
                setStatus("Display name cannot be empty.", true);
                return false;
            }
            if (!p1.isEmpty() || !p2.isEmpty()) {
                if (p1.length() < 4) {
                    setStatus("Password must be at least 4 characters.", true);
                    return false;
                }
                if (!p1.equals(p2)) {
                    setStatus("Passwords do not match.", true);
                    return false;
                }
                currentUser.password = p1;
            }
            currentUser.displayName = name;
            welcome.setText("Welcome, " + name + "!");
            newPass.setText("");
            confirmPass.setText("");
            setStatus("Profile updated successfully.", false);
            return true;
        }

        private void setStatus(String msg, boolean error) {
            status.setForeground(error ? new Color(190, 30, 30) : new Color(20, 130, 40));
            status.setText(msg);
        }
    }

    // =====================================================================
    //  SCREEN 3: EXAM
    // =====================================================================
    private class ExamPanel extends JPanel {
        private final JLabel timerLabel = new JLabel("30:00", SwingConstants.CENTER);
        private final JLabel progressLabel = new JLabel();
        private final JLabel studentLabel = new JLabel();
        private final JTextArea questionArea = new JTextArea();
        private final JRadioButton[] radios = new JRadioButton[4];
        private final ButtonGroup group = new ButtonGroup();
        private final JButton prevBtn = new JButton("\u25C0 Previous");
        private final JButton nextBtn = new JButton("Next \u25B6");
        private final JButton clearBtn = new JButton("Clear Answer");
        private final JButton submitBtn = new JButton("Submit Exam");
        private final JPanel palette = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        private JButton[] paletteBtns;

        private javax.swing.Timer countdown;
        private long endMillis;
        private long startMillis;
        private int[] answers;           // -1 = unanswered
        private int current;
        private boolean inProgress;
        private boolean loadingQuestion; // guards radio listeners during programmatic changes

        ExamPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

            // ---- top bar: student name | progress | timer ----
            JPanel top = new JPanel(new BorderLayout());
            studentLabel.setFont(studentLabel.getFont().deriveFont(Font.BOLD, 14f));
            progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
            progressLabel.setFont(progressLabel.getFont().deriveFont(Font.BOLD, 14f));

            timerLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 26));
            timerLabel.setOpaque(true);
            timerLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY),
                    BorderFactory.createEmptyBorder(4, 14, 4, 14)));

            top.add(studentLabel, BorderLayout.WEST);
            top.add(progressLabel, BorderLayout.CENTER);
            top.add(timerLabel, BorderLayout.EAST);
            add(top, BorderLayout.NORTH);

            // ---- center: question + options ----
            JPanel center = new JPanel();
            center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
            center.setBorder(BorderFactory.createTitledBorder("Question"));

            questionArea.setEditable(false);
            questionArea.setLineWrap(true);
            questionArea.setWrapStyleWord(true);
            questionArea.setOpaque(false);
            questionArea.setFont(questionArea.getFont().deriveFont(Font.BOLD, 17f));
            questionArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 16, 8));
            questionArea.setAlignmentX(Component.LEFT_ALIGNMENT);
            center.add(questionArea);

            for (int i = 0; i < 4; i++) {
                final int idx = i;
                radios[i] = new JRadioButton();
                radios[i].setFont(radios[i].getFont().deriveFont(15f));
                radios[i].setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 8));
                radios[i].setAlignmentX(Component.LEFT_ALIGNMENT);
                group.add(radios[i]);
                center.add(radios[i]);
                radios[i].addActionListener(e -> {
                    if (!loadingQuestion) {
                        answers[current] = idx;
                        refreshPalette();
                    }
                });
            }
            center.add(Box.createVerticalGlue());
            add(center, BorderLayout.CENTER);

            // ---- bottom: palette + navigation ----
            JPanel bottom = new JPanel(new BorderLayout(0, 8));

            palette.setBorder(BorderFactory.createTitledBorder("Question Palette (green = answered)"));
            bottom.add(palette, BorderLayout.NORTH);

            JPanel nav = new JPanel(new BorderLayout());
            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            left.add(prevBtn);
            left.add(nextBtn);
            left.add(clearBtn);
            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            submitBtn.setFont(submitBtn.getFont().deriveFont(Font.BOLD));
            submitBtn.setForeground(new Color(150, 20, 20));
            right.add(submitBtn);
            nav.add(left, BorderLayout.WEST);
            nav.add(right, BorderLayout.EAST);
            bottom.add(nav, BorderLayout.SOUTH);
            add(bottom, BorderLayout.SOUTH);

            // ---- actions ----
            prevBtn.addActionListener(e -> goTo(current - 1));
            nextBtn.addActionListener(e -> goTo(current + 1));
            clearBtn.addActionListener(e -> {
                answers[current] = -1;
                group.clearSelection();
                refreshPalette();
            });
            submitBtn.addActionListener(e -> manualSubmit());
        }

        boolean isExamInProgress() {
            return inProgress;
        }

        void startExam(User user) {
            answers = new int[questions.size()];
            java.util.Arrays.fill(answers, -1);
            current = 0;
            inProgress = true;
            studentLabel.setText("Student: " + user.displayName);

            // build question palette
            palette.removeAll();
            paletteBtns = new JButton[questions.size()];
            for (int i = 0; i < paletteBtns.length; i++) {
                final int idx = i;
                JButton b = new JButton(String.valueOf(i + 1));
                b.setMargin(new Insets(2, 6, 2, 6));
                b.setFocusable(false);
                b.addActionListener(e -> goTo(idx));
                paletteBtns[i] = b;
                palette.add(b);
            }
            palette.revalidate();

            loadQuestion();

            // Timer: based on wall-clock so it stays accurate even if the EDT lags
            startMillis = System.currentTimeMillis();
            endMillis = startMillis + EXAM_SECONDS * 1000L;
            updateTimerLabel(EXAM_SECONDS);
            if (countdown != null) countdown.stop();
            countdown = new javax.swing.Timer(250, e -> tick());
            countdown.start();
        }

        private void tick() {
            long remainingMs = endMillis - System.currentTimeMillis();
            int remaining = (int) Math.max(0, (remainingMs + 999) / 1000);
            updateTimerLabel(remaining);
            if (remainingMs <= 0) {
                finishExam(true);
            }
        }

        private void updateTimerLabel(int seconds) {
            timerLabel.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
            if (seconds <= 60) {
                timerLabel.setBackground(new Color(255, 205, 205));
                timerLabel.setForeground(new Color(170, 0, 0));
            } else if (seconds <= 300) {
                timerLabel.setBackground(new Color(255, 240, 200));
                timerLabel.setForeground(new Color(150, 90, 0));
            } else {
                timerLabel.setBackground(new Color(230, 245, 230));
                timerLabel.setForeground(new Color(20, 100, 30));
            }
        }

        private void goTo(int index) {
            if (index < 0 || index >= questions.size()) return;
            current = index;
            loadQuestion();
        }

        private void loadQuestion() {
            Question q = questions.get(current);
            loadingQuestion = true;
            questionArea.setText((current + 1) + ". " + q.text);
            for (int i = 0; i < 4; i++) radios[i].setText(q.options[i]);
            if (answers[current] >= 0) radios[answers[current]].setSelected(true);
            else group.clearSelection();
            loadingQuestion = false;

            progressLabel.setText("Question " + (current + 1) + " of " + questions.size());
            prevBtn.setEnabled(current > 0);
            nextBtn.setEnabled(current < questions.size() - 1);
            refreshPalette();
        }

        private void refreshPalette() {
            for (int i = 0; i < paletteBtns.length; i++) {
                JButton b = paletteBtns[i];
                b.setOpaque(true);
                b.setBackground(answers[i] >= 0 ? new Color(170, 225, 170) : null);
                b.setFont(b.getFont().deriveFont(i == current ? Font.BOLD : Font.PLAIN));
                b.setBorder(i == current
                        ? BorderFactory.createLineBorder(new Color(30, 90, 200), 2)
                        : UIManager.getBorder("Button.border"));
                b.setMargin(new Insets(2, 6, 2, 6));
            }
            palette.repaint();
        }

        private void manualSubmit() {
            int unanswered = 0;
            for (int a : answers) if (a < 0) unanswered++;
            String msg = unanswered > 0
                    ? "You have " + unanswered + " unanswered question(s).\nSubmit the exam anyway?"
                    : "Are you sure you want to submit your exam?";
            int choice = JOptionPane.showConfirmDialog(ExamSystem.this, msg,
                    "Confirm Submission", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION && inProgress) {
                finishExam(false);
            }
        }

        /** Called on manual submit or when time runs out. */
        private void finishExam(boolean timedOut) {
            if (!inProgress) return;
            inProgress = false;
            countdown.stop();

            long elapsedMs = timedOut ? EXAM_SECONDS * 1000L : System.currentTimeMillis() - startMillis;
            elapsedMs = Math.min(elapsedMs, EXAM_SECONDS * 1000L);

            resultPanel.showResult(currentUser, answers.clone(), (int) (elapsedMs / 1000), timedOut);
            showScreen(RESULT);

            if (timedOut) {
                JOptionPane.showMessageDialog(ExamSystem.this,
                        "Time is up! Your exam was submitted automatically.",
                        "Time's Up", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        /** Stop the timer without submitting (used when the user quits). */
        void abort() {
            inProgress = false;
            if (countdown != null) countdown.stop();
        }
    }

    // =====================================================================
    //  SCREEN 4: RESULT
    // =====================================================================
    private class ResultPanel extends JPanel {
        private final JLabel scoreLabel = new JLabel("", SwingConstants.CENTER);
        private final JLabel detailLabel = new JLabel("", SwingConstants.CENTER);
        private final JLabel noteLabel = new JLabel(" ", SwingConstants.CENTER);
        private final DefaultTableModel model = new DefaultTableModel(
                new String[]{"#", "Question", "Your Answer", "Correct Answer", "Result"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        ResultPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

            JPanel header = new JPanel(new GridLayout(4, 1, 0, 4));
            JLabel title = new JLabel("Exam Result", SwingConstants.CENTER);
            title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
            scoreLabel.setFont(scoreLabel.getFont().deriveFont(Font.BOLD, 30f));
            noteLabel.setForeground(new Color(170, 0, 0));
            header.add(title);
            header.add(scoreLabel);
            header.add(detailLabel);
            header.add(noteLabel);
            add(header, BorderLayout.NORTH);

            JTable table = new JTable(model);
            table.setRowHeight(26);
            table.getColumnModel().getColumn(0).setMaxWidth(40);
            table.getColumnModel().getColumn(1).setPreferredWidth(320);
            table.getColumnModel().getColumn(4).setMaxWidth(110);
            table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                               boolean focus, int r, int c) {
                    Component comp = super.getTableCellRendererComponent(t, v, sel, focus, r, c);
                    setHorizontalAlignment(CENTER);
                    String s = String.valueOf(v);
                    if (!sel) {
                        if (s.equals("Correct")) comp.setForeground(new Color(20, 130, 40));
                        else if (s.equals("Incorrect")) comp.setForeground(new Color(190, 30, 30));
                        else comp.setForeground(Color.GRAY);
                    }
                    return comp;
                }
            });
            add(new JScrollPane(table), BorderLayout.CENTER);

            JButton logout = new JButton("Logout");
            logout.setFont(logout.getFont().deriveFont(Font.BOLD));
            logout.addActionListener(e -> logout());
            JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
            south.add(logout);
            add(south, BorderLayout.SOUTH);
        }

        void showResult(User user, int[] answers, int elapsedSeconds, boolean timedOut) {
            model.setRowCount(0);
            int correct = 0, incorrect = 0, skipped = 0;

            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                String given = answers[i] < 0 ? "\u2014 (not answered)" : q.options[answers[i]];
                String status;
                if (answers[i] < 0) { status = "Skipped"; skipped++; }
                else if (answers[i] == q.correct) { status = "Correct"; correct++; }
                else { status = "Incorrect"; incorrect++; }
                model.addRow(new Object[]{i + 1, q.text, given, q.options[q.correct], status});
            }

            int total = questions.size();
            int pct = Math.round(100f * correct / total);
            scoreLabel.setText("Score: " + correct + " out of " + total + "  (" + pct + "%)");
            detailLabel.setText(String.format(
                    "%s  |  Time taken: %02d:%02d of %02d:00  |  Correct: %d   Incorrect: %d   Skipped: %d",
                    user.displayName, elapsedSeconds / 60, elapsedSeconds % 60, EXAM_MINUTES,
                    correct, incorrect, skipped));
            noteLabel.setText(timedOut ? "Exam was auto-submitted because time ran out." : " ");
        }
    }

    // ====================== LOGOUT ======================
    private void logout() {
        currentUser = null;
        loginPanel.reset();
        showScreen(LOGIN);
    }

    // ====================== MAIN ======================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) { }
            new ExamSystem().setVisible(true);
        });
    }
}
