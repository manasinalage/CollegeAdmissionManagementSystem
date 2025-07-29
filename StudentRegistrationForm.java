package CollegeAdmission;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentRegistrationForm extends JFrame {

    private JTextField nameField, emailField, contactField, percentageField, ageField;
    private JComboBox<String> genderBox, courseBox, statusBox;
    private JButton registerBtn, updateBtn, exitBtn, viewBtn;

    public StudentRegistrationForm() {
        setTitle("Student Registration Form");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);

        JPanel background = new JPanel();
        background.setLayout(null);
        background.setBackground(new Color(153, 102, 204));
        background.setBounds(0, 0, 1920, 1080);
        add(background);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBounds(450, 100, 600, 550);
        background.add(formPanel);

        JLabel heading = new JLabel("Student Registration");
        heading.setFont(new Font("Arial", Font.BOLD, 22));
        heading.setBounds(180, 20, 300, 30);
        formPanel.add(heading);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(100, 80, 100, 25);
        formPanel.add(nameLabel);
        nameField = new JTextField();
        nameField.setBounds(220, 80, 250, 25);
        formPanel.add(nameField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(100, 120, 100, 25);
        formPanel.add(emailLabel);
        emailField = new JTextField();
        emailField.setBounds(220, 120, 250, 25);
        formPanel.add(emailField);

        JLabel contactLabel = new JLabel("Contact No:");
        contactLabel.setBounds(100, 160, 100, 25);
        formPanel.add(contactLabel);
        contactField = new JTextField();
        contactField.setBounds(220, 160, 250, 25);
        formPanel.add(contactField);

        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setBounds(100, 200, 100, 25);
        formPanel.add(genderLabel);
        String[] genders = {"Select", "Male", "Female", "Other"};
        genderBox = new JComboBox<>(genders);
        genderBox.setBounds(220, 200, 250, 25);
        formPanel.add(genderBox);

        JLabel courseLabel = new JLabel("Course:");
        courseLabel.setBounds(100, 240, 100, 25);
        formPanel.add(courseLabel);
        String[] courses = {"Select", "BCS", "BCA", "BSc IT"};
        courseBox = new JComboBox<>(courses);
        courseBox.setBounds(220, 240, 250, 25);
        formPanel.add(courseBox);

        JLabel percentageLabel = new JLabel("Percentage:");
        percentageLabel.setBounds(100, 280, 100, 25);
        formPanel.add(percentageLabel);
        percentageField = new JTextField();
        percentageField.setBounds(220, 280, 250, 25);
        formPanel.add(percentageField);

        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setBounds(100, 320, 100, 25);
        formPanel.add(ageLabel);
        ageField = new JTextField();
        ageField.setBounds(220, 320, 250, 25);
        formPanel.add(ageField);

        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setBounds(100, 360, 100, 25);
        formPanel.add(statusLabel);
        String[] statuses = {"Pending", "Approved", "Rejected"};
        statusBox = new JComboBox<>(statuses);
        statusBox.setBounds(220, 360, 250, 25);
        formPanel.add(statusBox);

        registerBtn = new JButton("Register");
        registerBtn.setBounds(40, 420, 110, 30);
        formPanel.add(registerBtn);

        updateBtn = new JButton("Update");
        updateBtn.setBounds(160, 420, 110, 30);
        formPanel.add(updateBtn);

        viewBtn = new JButton("View All Students");
        viewBtn.setBounds(280, 420, 160, 30);
        formPanel.add(viewBtn);

        exitBtn = new JButton("Exit");
        exitBtn.setBounds(450, 420, 100, 30);
        formPanel.add(exitBtn);

        nameField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                fetchStudentByName(nameField.getText().trim());
            }
        });

        registerBtn.addActionListener(e -> registerStudent());
        updateBtn.addActionListener(e -> updateStudent());
        viewBtn.addActionListener(e -> new ViewRegisteredStudents());
        exitBtn.addActionListener(e -> {
            dispose();
            new LoginPage();
        });

        setVisible(true);
    }

    private void fetchStudentByName(String name) {
        if (name.isEmpty()) return;

        try (Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/admission_db",
                "postgres", "manasi")) {

            String sql = "SELECT * FROM registration WHERE name = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, name);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                emailField.setText(rs.getString("email"));
                contactField.setText(rs.getString("contact_number"));
                genderBox.setSelectedItem(rs.getString("gender"));
                courseBox.setSelectedItem(rs.getString("course"));
                percentageField.setText(String.valueOf(rs.getDouble("percentage")));
                ageField.setText(String.valueOf(rs.getInt("age")));
                statusBox.setSelectedItem(rs.getString("status"));
            } else {
                clearFormExceptName();
            }

            rs.close();
            pst.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error fetching student data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registerStudent() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String contact = contactField.getText().trim();
        String gender = genderBox.getSelectedItem().toString();
        String course = courseBox.getSelectedItem().toString();
        String percentageText = percentageField.getText().trim();
        String ageText = ageField.getText().trim();
        String status = statusBox.getSelectedItem().toString();
        
        
        
        
        
        
        

        if (name.isEmpty() || email.isEmpty() || contact.isEmpty() ||
                gender.equals("Select") || course.equals("Select") || percentageText.isEmpty() || ageText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double percentage = Double.parseDouble(percentageText);
            int age = Integer.parseInt(ageText);

            Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/admission_db",
                    "postgres", "manasi"
            );

            String sql = "INSERT INTO registration (name, email, contact_number, gender, course, percentage, age, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, contact);
            pst.setString(4, gender);
            pst.setString(5, course);
            pst.setDouble(6, percentage);
            pst.setInt(7, age);
            pst.setString(8, status);

            int rowsInserted = pst.executeUpdate();

            JOptionPane.showMessageDialog(this, rowsInserted > 0
                    ? "Student Registered Successfully!"
                    : "Registration Failed!", "Info", JOptionPane.INFORMATION_MESSAGE);

            pst.close();
            conn.close();

            clearForm();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid numeric percentage and age!", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudent() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String contact = contactField.getText().trim();
        String gender = genderBox.getSelectedItem().toString();
        String course = courseBox.getSelectedItem().toString();
        String percentageText = percentageField.getText().trim();
        String ageText = ageField.getText().trim();
        String status = statusBox.getSelectedItem().toString();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter student name!", "Missing Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double percentage = Double.parseDouble(percentageText);
            int age = Integer.parseInt(ageText);

            Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/admission_db",
                    "postgres", "manasi"
            );

            String sql = "UPDATE registration SET email = ?, contact_number = ?, gender = ?, course = ?, percentage = ?, age = ?, status = ? WHERE name = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, email);
            pst.setString(2, contact);
            pst.setString(3, gender);
            pst.setString(4, course);
            pst.setDouble(5, percentage);
            pst.setInt(6, age);
            pst.setString(7, status);
            pst.setString(8, name);

            int rowsUpdated = pst.executeUpdate();

            JOptionPane.showMessageDialog(this, rowsUpdated > 0
                    ? "Student Updated Successfully!"
                    : "No record found with given name.", "Update Status", JOptionPane.INFORMATION_MESSAGE);

            pst.close();
            conn.close();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid numeric percentage and age!", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        nameField.setText("");
        clearFormExceptName();
    }

    private void clearFormExceptName() {
        emailField.setText("");
        contactField.setText("");
        genderBox.setSelectedIndex(0);
        courseBox.setSelectedIndex(0);
        percentageField.setText("");
        ageField.setText("");
        statusBox.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentRegistrationForm::new);
    }
}
