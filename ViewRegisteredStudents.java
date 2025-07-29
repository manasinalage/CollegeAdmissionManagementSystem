package CollegeAdmission;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ViewRegisteredStudents extends JFrame {
    JTable table;
    DefaultTableModel model;
    JButton deleteBtn;

    public ViewRegisteredStudents() {
        setTitle("Registered Students");
        setSize(1000, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 🟦 Table setup
        model = new DefaultTableModel();
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // 🔷 Add Columns
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Email");
        model.addColumn("Contact");
        model.addColumn("Age");            // ✅ Age Column
        model.addColumn("Gender");
        model.addColumn("Course");
        model.addColumn("Percentage");
        model.addColumn("Status");

        // 🟨 Top panel for buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        deleteBtn = new JButton("Delete Selected Student");
        topPanel.add(deleteBtn);
        add(topPanel, BorderLayout.NORTH);

        // 🟩 Button Action
        deleteBtn.addActionListener(e -> deleteSelectedStudent());

        fetchStudentData();

        setVisible(true);
    }

    // ✅ Method to fetch all student data from DB
    private void fetchStudentData() {
        model.setRowCount(0); // Clear table before reloading

        try (Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/admission_db", "postgres", "manasi");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM registration ORDER BY id ASC")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("contact_number"),
                        rs.getInt("age"),                            // ✅ Fetch age
                        rs.getString("gender"),
                        rs.getString("course"),
                        rs.getDouble("percentage"),
                        rs.getString("status")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    // ✅ Delete method
    private void deleteSelectedStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this student?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        int studentId = (int) model.getValueAt(selectedRow, 0); // Get ID from first column

        try (Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/admission_db", "postgres", "manasi");
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM registration WHERE id = ?")) {

            stmt.setInt(1, studentId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Student deleted successfully.");
                fetchStudentData(); // Refresh table
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting student: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new ViewRegisteredStudents();
    }
}
