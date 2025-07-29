package CollegeAdmission;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.*;
import java.sql.*;
import java.awt.Desktop;

public class PDFExporter {

    public static void exportAllAdmissionsToPDF() {
        try {
            // Path to Desktop
            String userHome = System.getProperty("user.home");
            String desktopPath = userHome + File.separator + "Desktop";
            String pdfPath = desktopPath + File.separator + "College Admission Report.pdf";

            // Create PDF Document
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
            document.open();

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("College Admission Report - 2025", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Table
            PdfPTable table = new PdfPTable(8); // 8 columns
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Column headers
            String[] headers = {"ID", "Name", "Email", "Contact", "Gender", "Course", "Percentage", "Status"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);
            }

            // Database connection
            Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/admission_db",
                "postgres", "manasi" // Update if password is different
            );
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM registration");

            // Add rows
            while (rs.next()) {
                table.addCell(String.valueOf(rs.getInt("id")));
                table.addCell(rs.getString("name"));
                table.addCell(rs.getString("email"));
                table.addCell(rs.getString("contact_number"));
                table.addCell(rs.getString("gender"));
                table.addCell(rs.getString("course"));
                table.addCell(String.valueOf(rs.getDouble("percentage")));
                table.addCell(rs.getString("status"));
            }

            // Add table to document
            document.add(table);
            document.close();
            rs.close();
            stmt.close();
            conn.close();

            System.out.println("✅ PDF Report Generated Successfully at Desktop!");

            // Auto open the PDF
            File pdfFile = new File(pdfPath);
            if (pdfFile.exists()) {
                Desktop.getDesktop().open(pdfFile); // Automatically open PDF
            }

        } catch (Exception e) {
            System.out.println("❌ Error during PDF generation:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        exportAllAdmissionsToPDF();
    }
}
