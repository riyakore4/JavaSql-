import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.event.ChangeListener;

public class Issue {

    private JComboBox<Integer> groupNumberComboBox;
    private DefaultTableModel tableModel;
    private JTable groupDetailsTable;
    private JButton issueButton, returnButton;
    private JLabel sumLabel;

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> new Issue());
    }

    Issue() {
        JFrame frame = new JFrame("Issue Components");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Group Number ComboBox
        JPanel comboBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboBoxPanel.add(new JLabel("Select Group Number:"));
        groupNumberComboBox = new JComboBox<>();
        loadGroupNumbers();
        comboBoxPanel.add(groupNumberComboBox);

        // Issue Button
        issueButton = new JButton("Issue");
        issueButton.setEnabled(false);
        issueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the Issue1 frame
                Integer selectedGroupNumber = (Integer) groupNumberComboBox.getSelectedItem();
                if (selectedGroupNumber != null) {
                    Issue1 issue1 = new Issue1(selectedGroupNumber);
                    issue1.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(javax.swing.event.ChangeEvent e) {
                            // Refresh the table when data is added in Issue1
                            loadAndDisplayGroupDetails(selectedGroupNumber);
                        }
                    });
                }
            }
        });

        // Return Button
        returnButton = new JButton("Return");
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the selected row from the table
                int selectedRow = groupDetailsTable.getSelectedRow();

                // Check if any row is selected
                if (selectedRow >= 0) {
                    // Get the PRN and Component from the selected row
                    int prn = (int) groupDetailsTable.getValueAt(selectedRow, 0);
                    String componentName = (String) groupDetailsTable.getValueAt(selectedRow, 8);

                    // Call the method to delete the selected data
                    deleteSelectedData(prn, componentName);

                    // Refresh the table after deletion
                    Integer selectedGroupNumber = (Integer) groupNumberComboBox.getSelectedItem();
                    if (selectedGroupNumber != null) {
                        loadAndDisplayGroupDetails(selectedGroupNumber);
                        displaySumOfQuantity(selectedGroupNumber);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a row to return the component.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Group Details Table
        tableModel = new DefaultTableModel();
        createTableModel();
        groupDetailsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(groupDetailsTable);

        // Add components to the main panel
        panel.add(comboBoxPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(issueButton);
        buttonPanel.add(returnButton);
        // Sum Label
        sumLabel = new JLabel("Sum of Quantity: ");
        buttonPanel.add(sumLabel);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // ActionListener for the View Details button
        groupNumberComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Integer selectedGroupNumber = (Integer) groupNumberComboBox.getSelectedItem();
                if (selectedGroupNumber != null) {
                    // Load and display details for the selected group number
                    loadAndDisplayGroupDetails(selectedGroupNumber);
                    // Enable the "Issue" button
                    issueButton.setEnabled(true);

                    // Display the sum of quantity
                    displaySumOfQuantity(selectedGroupNumber);
                }
            }
        });

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

    // Method to delete selected data
    private void deleteSelectedData(int prn, String componentName) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/java1", "root", "Rinku@1219");
            String query = "DELETE FROM group5 WHERE PRN=? AND C_name=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, prn);
            preparedStatement.setString(2, componentName);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadGroupNumbers() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/java1", "root", "Rinku@1219");
            String query = "SELECT DISTINCT CAST(G_No AS CHAR) AS G_No FROM group5";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String groupNumberString = resultSet.getString("G_No");
                if (groupNumberString != null && !groupNumberString.isEmpty()) {
                    groupNumberComboBox.addItem(Integer.parseInt(groupNumberString));
                }
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableModel() {
        tableModel.addColumn("PRN");
        tableModel.addColumn("First Name");
        tableModel.addColumn("Last Name");
        tableModel.addColumn("Class");
        tableModel.addColumn("Issue Date");
        tableModel.addColumn("Mobile");
        tableModel.addColumn("Email");
        tableModel.addColumn("Return Date");
        tableModel.addColumn("Component");
        tableModel.addColumn("Quantity");
    }

    private void loadAndDisplayGroupDetails(int selectedGroupNumber) {
        // Clear existing data in the table
        tableModel.setRowCount(0);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/java1", "root", "Rinku@1219");
            String query = "SELECT PRN, f_name, l_name, className, Issue_Date, mo_no, email, Return_Date, C_name, SUM(Quantity) AS Total_Quantity " +
                    "FROM group5 WHERE G_No=? GROUP BY PRN, f_name, l_name, className, Issue_Date, mo_no, email, Return_Date, C_name";
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
                int totalQuantity = resultSet.getInt("Total_Quantity");

                // Add row to the table model
                tableModel.addRow(new Object[]{prn, firstName, lastName, className, issueDate, mobile, email, returnDate, componentName, totalQuantity});
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void displaySumOfQuantity(int selectedGroupNumber) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/java1", "root", "Rinku@1219");
            String query = "SELECT SUM(Quantity) AS Total_Quantity FROM group5 WHERE G_No=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, selectedGroupNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int totalQuantity = resultSet.getInt("Total_Quantity");
                sumLabel.setText("Sum of Quantity: " + totalQuantity);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
