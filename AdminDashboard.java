// AdminDashboard.java
package CollegeAdmission;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.*;

public class AdminDashboard extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JButton downloadPdfBtn, exportCsvBtn;

    public AdminDashboard() {
        setTitle("Admin Dashboard - Merit Report");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Panel: Search
        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search: "));
        searchField = new JTextField(30);
        searchPanel.add(searchField);
        topPanel.add(searchPanel, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel();
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Email");
        model.addColumn("Contact");
        model.addColumn("Gender");
        model.addColumn("Course");
        model.addColumn("Percentage");
        model.addColumn("Status");
        model.addColumn("Merit Rank");

        fetchMeritList();

        // Real-time search
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String searchText = searchField.getText().trim();
                searchStudents(searchText);
            }
        });

        // Bottom Panel: Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        downloadPdfBtn = new JButton("Download PDF Report");
        exportCsvBtn = new JButton("Export CSV Report");
        btnPanel.add(downloadPdfBtn);
        btnPanel.add(exportCsvBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // Actions
        downloadPdfBtn.addActionListener(e -> generatePDF());
        exportCsvBtn.addActionListener(e -> exportCSV());

        setVisible(true);
    }

    private void fetchMeritList() {
        model.setRowCount(0);
        int rank = 1;
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/admission_db", "postgres", "manasi");
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM registration WHERE percentage >= ? ORDER BY percentage DESC")) {

            pst.setDouble(1, 60.0); // cut-off
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("contact_number"),
                        rs.getString("gender"),
                        rs.getString("course"),
                        rs.getDouble("percentage"),
                        rs.getString("status"),
                        rank++
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading merit list: " + e.getMessage());
        }
    }

    private void searchStudents(String keyword) {
        model.setRowCount(0);
        int rank = 1;
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/admission_db", "postgres", "manasi");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM registration WHERE " +
                             "CAST(id AS TEXT) ILIKE ? OR name ILIKE ? OR email ILIKE ? OR " +
                             "contact_number ILIKE ? OR gender ILIKE ? OR course ILIKE ? OR " +
                             "CAST(percentage AS TEXT) ILIKE ? OR status ILIKE ? ORDER BY percentage DESC")) {

            for (int i = 1; i <= 8; i++) {
                stmt.setString(i, "%" + keyword + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("contact_number"),
                        rs.getString("gender"),
                        rs.getString("course"),
                        rs.getDouble("percentage"),
                        rs.getString("status"),
                        rank++
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error searching data: " + ex.getMessage());
        }
    }

    private void exportCSV() {
        if (model == null || model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data available to export.");
            return;
        }

        try {
            // Save to Desktop for easy access
            String filePath = System.getProperty("user.home") + "/Desktop/Merit_Report.csv";
            FileWriter csvWriter = new FileWriter(filePath);

            // Write column headers
            for (int i = 0; i < model.getColumnCount(); i++) {
                csvWriter.append("\"").append(model.getColumnName(i)).append("\"");
                if (i < model.getColumnCount() - 1) csvWriter.append(",");
            }
            csvWriter.append("\n");

            // Write table data
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object value = model.getValueAt(i, j);
                    csvWriter.append("\"").append(value != null ? value.toString() : "").append("\"");
                    if (j < model.getColumnCount() - 1) csvWriter.append(",");
                }
                csvWriter.append("\n");
            }

            csvWriter.flush();
            csvWriter.close();
            File csvFile = new File(filePath);
            if (csvFile.exists()) {
                Desktop.getDesktop().open(csvFile);
            }

            JOptionPane.showMessageDialog(this, "CSV exported successfully at:\n" + filePath);
            System.out.println("CSV created at: " + filePath);

        } catch (Exception e) {
            e.printStackTrace();  // for debugging
            JOptionPane.showMessageDialog(this, "Error exporting CSV: " + e.getMessage());
        }
    }

    private void generatePDF() {
        if (model == null || model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data to export.");
            return;
        }

        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        try {
            String filePath = System.getProperty("user.home") + "/Desktop/Merit_Report.pdf";
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            Paragraph title = new Paragraph("Merit Report",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            PdfPTable pdfTable = new PdfPTable(model.getColumnCount());
            pdfTable.setWidthPercentage(100);

            for (int i = 0; i < model.getColumnCount(); i++) {
                PdfPCell cell = new PdfPCell(new Phrase(model.getColumnName(i)));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                pdfTable.addCell(cell);
            }

            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object value = model.getValueAt(i, j);
                    pdfTable.addCell(value != null ? value.toString() : "");
                }
            }

            document.add(pdfTable);
            document.close();

            File pdfFile = new File(filePath);
            if (pdfFile.exists()) {
                JOptionPane.showMessageDialog(this, "PDF Report generated successfully at:\n" + filePath);
                System.out.println("PDF created at: " + filePath);

                // ✅ Auto-open the generated PDF
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                }
            } else {
                JOptionPane.showMessageDialog(this, "PDF generation failed. File not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating PDF: " + e.getMessage());
        }
    }



    public static void main(String[] args) {
        new AdminDashboard();
    }
}
