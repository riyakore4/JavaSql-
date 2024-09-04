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
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.lang.ClassCastException;

public class userdata {
    private JComboBox<Integer> groupNumberComboBox;
    private DefaultTableModel model;
    private JLabel groupNumberLabel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new userdata());
    }

    private userdata() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/java1", "root", "Rinku@1219");
            System.out.println("Connection is established");
            // Create a set to store unique group numbers
            Set<Integer> groupNumbers = new HashSet<>();

            String query = "SELECT DISTINCT G_No FROM group5 WHERE G_No != 0";
            PreparedStatement groupNumberStatement = con.prepareStatement(query);
            ResultSet groupNumberResultSet = groupNumberStatement.executeQuery();

            while (groupNumberResultSet.next()) {
                int groupNumber = groupNumberResultSet.getInt("G_No");
                groupNumbers.add(groupNumber);
            }

            // Create a combo box with unique group numbers
            groupNumberComboBox = new JComboBox<>(groupNumbers.toArray(new Integer[0]));

            // Create a label to display selected group number
            groupNumberLabel = new JLabel("Selected Group Number:");

            // Create a DefaultTableModel
            model = new DefaultTableModel();
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

            // Create JTable with the model
            JTable table = new JTable(model);

            // Create JScrollPane to hold the table
            JScrollPane scrollPane = new JScrollPane(table);

            // Create panels for better layout
            JPanel mainPanel = new JPanel(new BorderLayout());
            JPanel topPanel = new JPanel(new FlowLayout());

            // Add label and combo box to the top panel
            topPanel.add(groupNumberLabel);
            topPanel.add(groupNumberComboBox);

            // Add components to the main panel
            mainPanel.add(topPanel, BorderLayout.NORTH);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            // Create JFrame to hold the components
            JFrame frame = new JFrame("Registered Groups");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(mainPanel);
            frame.setSize(800, 600);
            frame.setVisible(true);

            // Add listener to the combo box for group number selection
            groupNumberComboBox.addActionListener(e -> loadAndDisplayGroupDetails((Integer) groupNumberComboBox.getSelectedItem()));

            // Add print button to trigger PDF creation
            JButton printButton = new JButton("Print to PDF");
            printButton.addActionListener(e -> printToPDF(model));
            mainPanel.add(printButton, BorderLayout.SOUTH);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAndDisplayGroupDetails(Integer selectedGroupNumber) {
        model.setRowCount(0);

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/java1", "root", "Rinku@1219");
            String query = "SELECT * FROM group5 WHERE G_No=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, selectedGroupNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int prn = resultSet.getInt("PRN");
                String firstName = resultSet.getString("f_name");
                String lastName = resultSet.getString("l_name");
                String className = resultSet.getString("className");
                String issueDate = resultSet.getString("Issue_Date");
                String mobile = resultSet.getString("mo_no");
                String email = resultSet.getString("email");
                String returnDate = resultSet.getString("Return_Date");
                String componentName = resultSet.getString("C_name");
                int quantity = resultSet.getInt("Quantity");

                // Add row to the table model
                model.addRow(new Object[]{selectedGroupNumber, prn, firstName, lastName, className, issueDate, mobile, email, returnDate, componentName, quantity});
            }

            // Update the label with the selected group number
            groupNumberLabel.setText("Selected Group Number: " + selectedGroupNumber);

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
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

