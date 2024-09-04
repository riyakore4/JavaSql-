import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Registered_Groups {
    public static void main(String args[]) {
        new Registered_Groups();
    }

    Registered_Groups() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/java1", "root", "Rinku@1219");
            System.out.println("Connection is established");
            String a = "SELECT * FROM group5";
            PreparedStatement st = con.prepareStatement(a);
            ResultSet rs = st.executeQuery();

            // Create a DefaultTableModel
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("G_No");
            model.addColumn("PRN");
            model.addColumn("First Name");
            model.addColumn("Last Name");
            model.addColumn("Class Name");
            model.addColumn("Issue Date");
            model.addColumn("Mobile");
            model.addColumn("Email");
            model.addColumn("Return Date");
            model.addColumn("Component");
            model.addColumn("Quantity");

            while (rs.next()) {
                int Sr_no = rs.getInt("G_No");
                int Roll = rs.getInt("PRN");
                String fname = rs.getString("f_name");
                String lname = rs.getString("l_name");
                String Cname = rs.getString("className");
                String Idate = rs.getString("Issue_Date");
                String mob = rs.getString("mo_no");
                String mail = rs.getString("email");
                String rdate = rs.getString("Return_Date");
                String Component = rs.getString("C_name");
                int quantity = rs.getInt("Quantity");

                // Add row to the model
                model.addRow(new Object[]{Sr_no, Roll, fname, lname, Cname, Idate, mob, mail, rdate, Component, quantity});
            }

            // Create JTable with the model
            JTable table = new JTable(model);

            // Create JScrollPane to hold the table
            JScrollPane scrollPane = new JScrollPane(table);

            // Create JFrame to hold the components
            JFrame frame = new JFrame("Registered Groups");
            frame.setLayout(new BorderLayout());
            frame.add(scrollPane, BorderLayout.CENTER);
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            // Add print button to trigger PDF creation
            JButton printButton = new JButton("Print to PDF");
            printButton.addActionListener(e -> printToPDF(model));
            frame.add(printButton, BorderLayout.SOUTH);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void printToPDF(DefaultTableModel model) {
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream("Registered_Groups.pdf"));
            document.open();

            // Add title to the PDF
            document.add(new Paragraph("Registered Groups"));

            // Add table to the PDF
            PdfPTable pdfTable = new PdfPTable(model.getColumnCount());

            // Add column headers to the table
            for (int i = 0; i < model.getColumnCount(); i++) {
                pdfTable.addCell(model.getColumnName(i));
            }

            // Add data rows to the table
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object value = model.getValueAt(i, j);
                    pdfTable.addCell(value != null ? value.toString() : ""); // Check for null before calling toString()
                }
            }

            document.add(pdfTable);
            document.close();
            JOptionPane.showMessageDialog(null, "PDF created successfully!");

        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
