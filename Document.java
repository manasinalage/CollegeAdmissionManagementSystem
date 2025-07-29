package CollegeAdmission;

import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class Document {

    private com.itextpdf.text.Document document;
    private PdfWriter writer;
    private String filePath = "StudentReport.pdf";

    public void open() {
        try {
            document = new com.itextpdf.text.Document();
            writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addTitle(String title) {
        try {
            document.add(new Paragraph(title));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public void add(PdfPTable pdfTable) throws DocumentException {
        document.add(pdfTable);
    }

    public void close() {
        if (document != null) {
            document.close();
        }

        // Automatically open the generated PDF
        try {
            java.awt.Desktop.getDesktop().open(new java.io.File(filePath));
        } catch (IOException e) {
            System.out.println("PDF created, but couldn't open automatically.");
        }
    }
}
