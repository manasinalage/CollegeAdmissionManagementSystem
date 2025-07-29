package CollegeAdmission;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JFrame {
    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginButton, clearButton;
    Connection conn;

    public LoginPage() {
        setTitle("College Admission - Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Background Image
        ImageIcon bgIcon = new ImageIcon("C:\\Users\\Manasi\\Downloads\\java\\AdmissionBackground.jpg");
        Image img = bgIcon.getImage().getScaledInstance(1920, 1080, Image.SCALE_SMOOTH);
        JLabel background = new JLabel(new ImageIcon(img));
        background.setLayout(null);
        setContentPane(background);

        // Exit Confirmation
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(
                    LoginPage.this, "Are you sure you want to exit?",
                    "Exit Confirmation", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        // Login Panel
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(null);
        loginPanel.setBounds(650, 250, 400, 300);
        loginPanel.setBackground(new Color(255, 255, 255, 230));
        background.add(loginPanel);

        JLabel titleLabel = new JLabel("Login", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setBounds(0, 20, 400, 40);
        loginPanel.add(titleLabel);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        userLabel.setBounds(40, 80, 100, 30);
        loginPanel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 18));
        usernameField.setBounds(150, 80, 200, 30);
        loginPanel.add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        passLabel.setBounds(40, 130, 100, 30);
        loginPanel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 18));
        passwordField.setBounds(150, 130, 200, 30);
        loginPanel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBounds(80, 200, 100, 35);
        loginButton.setBackground(new Color(0, 153, 76));
        loginButton.setForeground(Color.WHITE);
        loginPanel.add(loginButton);

        clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Arial", Font.BOLD, 16));
        clearButton.setBounds(220, 200, 100, 35);
        clearButton.setBackground(new Color(204, 0, 0));
        clearButton.setForeground(Color.WHITE);
        loginPanel.add(clearButton);

        getRootPane().setDefaultButton(loginButton);
        usernameField.addActionListener(e -> passwordField.requestFocusInWindow());

        connectDB();

        loginButton.addActionListener(e -> verifyLogin());
        clearButton.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
            usernameField.requestFocusInWindow();
        });

        usernameField.requestFocusInWindow();
        setVisible(true);
    }

    void connectDB() {
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/admission_db", "postgres", "manasi"
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage());
        }
    }

    void verifyLogin() {
        String user = usernameField.getText();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        try {
            // 1. Check Admin
            String adminQuery = "SELECT * FROM admin WHERE username=? AND password=? AND user_type='admin'";
            PreparedStatement pst1 = conn.prepareStatement(adminQuery);
            pst1.setString(1, user);
            pst1.setString(2, pass);
            ResultSet rs1 = pst1.executeQuery();

            if (rs1.next()) {
                JOptionPane.showMessageDialog(this, "Admin Login Successful!");
                dispose();
                new AdminDashboard();
                return;
            }

            // 2. Check Student
            String studentQuery = "SELECT * FROM admin WHERE username=? AND password=? AND user_type='student'";
            PreparedStatement pst2 = conn.prepareStatement(studentQuery);
            pst2.setString(1, user);
            pst2.setString(2, pass);
            ResultSet rs2 = pst2.executeQuery();

            if (rs2.next()) {
                JOptionPane.showMessageDialog(this, "Student Login Successful!");
                dispose();
                new StudentRegistrationForm();
                return;
            }

            // 3. If student does not exist → insert automatically
            String insertStudent = "INSERT INTO admin (username, password, user_type) VALUES (?, ?, 'student')";
            PreparedStatement pst3 = conn.prepareStatement(insertStudent);
            pst3.setString(1, user);
            pst3.setString(2, pass);
            pst3.executeUpdate();

            JOptionPane.showMessageDialog(this, "New Student Registered & Logged In!");
            dispose();
            new StudentRegistrationForm();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Login Failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage());
    }
}
