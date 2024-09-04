import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

public class Group {

    private JTextField groupNoField, prnField, firstNameField, lastNameField, classField,
            mobileNoField, emailField, quantityField, availableQuantityField, newComponentField;
    private JComboBox<String> componentNameComboBox;
    private JCheckBox addAnotherComponentCheckbox;
    private JDateChooser issueDateChooser, returnDateChooser;
    private JLabel newComponentLabel;  // Add a label for the new component field
    private JPanel panel;  // Keep a reference to the panel to dynamically add new components

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> new Group());
    }

    Group() {
        JFrame frame = new JFrame("Data Entry Form");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);

        panel = new JPanel();
        panel.setLayout(new GridLayout(15, 2, 10, 10));
        panel.setBackground(new Color(230, 230, 230));

        panel.add(new JLabel("  Group Number:"));
        groupNoField = new JTextField();
        panel.add(groupNoField);

        panel.add(new JLabel("  PRN NO:"));
        prnField = new JTextField();
        panel.add(prnField);

        panel.add(new JLabel("  First Name:"));
        firstNameField = new JTextField();
        panel.add(firstNameField);

        panel.add(new JLabel("  Last Name:"));
        lastNameField = new JTextField();
        panel.add(lastNameField);

        panel.add(new JLabel("  Class:"));
        classField = new JTextField();
        panel.add(classField);

        panel.add(new JLabel("  Issue Date:"));
        issueDateChooser = new JDateChooser();
        panel.add(issueDateChooser);

        panel.add(new JLabel("  Mobile Number:"));
        mobileNoField = new JTextField();
        panel.add(mobileNoField);

        panel.add(new JLabel("  Email:"));
        emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("  Return Date:"));
        returnDateChooser = new JDateChooser();
        panel.add(returnDateChooser);

        panel.add(new JLabel("  Component Name:"));
        componentNameComboBox = new JComboBox<>();
        loadComponentNames();
        panel.add(componentNameComboBox);

        panel.add(new JLabel("  Quantity:"));
        quantityField = new JTextField();
        panel.add(quantityField);

        panel.add(new JLabel("  Available Quantity:"));
        availableQuantityField = new JTextField();
        availableQuantityField.setEditable(false);
        panel.add(availableQuantityField);

        newComponentLabel = new JLabel("  New Component:");
        newComponentField = new JTextField();
        panel.add(newComponentLabel);
        panel.add(newComponentField);
        newComponentLabel.setVisible(false);
        newComponentField.setVisible(false);

        addAnotherComponentCheckbox = new JCheckBox("Add Another Component");
        panel.add(addAnotherComponentCheckbox);

        JButton submitButton = new JButton("Submit");
        panel.add(submitButton);
        submitButton.setBackground(new Color(0, 128, 0));

        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.getContentPane().add(BorderLayout.SOUTH, createButtonPanel(submitButton));

        frame.setVisible(true);
        frame.setResizable(false);

        componentNameComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAvailableQuantity();
            }
        });

        addAnotherComponentCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (addAnotherComponentCheckbox.isSelected()) {
                    showNewComponent();
                } else {
                    hideNewComponent();
                }
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateInputs()) {
                    try {
                        addDataToTable();
                        if (!addAnotherComponentCheckbox.isSelected()) {
                            JOptionPane.showMessageDialog(null, "Data added successfully!");
                            //System.exit(0);
                        } else {
                            clearFields();
                        }
                    } catch (ClassNotFoundException | SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error adding data to the database.");
                    }
                }
            }
        });
    }

    private JPanel createButtonPanel(JButton submitButton) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        return buttonPanel;
    }

    private void showNewComponent() {
        newComponentLabel.setVisible(true);
        newComponentField.setVisible(true);
        panel.revalidate();
        panel.repaint();
    }

    private void hideNewComponent() {
        newComponentLabel.setVisible(false);
        newComponentField.setVisible(false);
        panel.revalidate();
        panel.repaint();
    }

    private boolean validateInputs() {
        if (!isValidMobileNumber(mobileNoField.getText())) {
            JOptionPane.showMessageDialog(null, "Invalid Mobile Number");
            return false;
        }

        if (!isValidEmail(emailField.getText())) {
            JOptionPane.showMessageDialog(null, "Invalid Email");
            return false;
        }

        return true;
    }

    private boolean isValidMobileNumber(String mobileNumber) {
        String regex = "\\d{10}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(mobileNumber);
        return matcher.matches();
    }

    private boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void loadComponentNames() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/java1", "root", "Rinku@1219");
            String query = "SELECT C_name FROM components";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                componentNameComboBox.addItem(resultSet.getString("C_name"));
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateAvailableQuantity() {
        String selectedComponent = (String) componentNameComboBox.getSelectedItem();
        int availableQuantity = getAvailableQuantity(selectedComponent);
        availableQuantityField.setText(Integer.toString(availableQuantity));
    }

    private int getAvailableQuantity(String componentName) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/java1", "root", "Rinku@1219");
            String query = "SELECT Quantity FROM components WHERE C_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, componentName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("Quantity");
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return 0; // Default value if an error occurs
    }

    private void addDataToTable() throws ClassNotFoundException, SQLException, NumberFormatException {
        String groupNumber = groupNoField.getText();
        String prn = prnField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String className = classField.getText();
        String issueDate = ((JTextFieldDateEditor) issueDateChooser.getDateEditor()).getText();
        String mobileNumber = mobileNoField.getText();
        String email = emailField.getText();
        String returnDate = ((JTextFieldDateEditor) returnDateChooser.getDateEditor()).getText();
        String componentName = (String) componentNameComboBox.getSelectedItem();
        int quantity = Integer.parseInt(quantityField.getText());

        updateAvailableQuantityInDatabase(componentName, quantity);
        insertDataIntoGroupTable(groupNumber, prn, firstName, lastName, className, issueDate, mobileNumber, email, returnDate, componentName, quantity);

        if (addAnotherComponentCheckbox.isSelected()) {
            // If "Add Another Component" is selected, add the new component for the same group
            String newComponent = newComponentField.getText();
            if (!newComponent.isEmpty()) {
                insertDataIntoGroupTable(groupNumber, prn, "", "", "", "", "", "", "", newComponent, 1);
            }
        }
    }

    private void updateAvailableQuantityInDatabase(String componentName, int quantity) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/java1", "root", "Rinku@1219");

            String updateQuery = "UPDATE components SET Quantity = Quantity - ? WHERE C_name = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setInt(1, quantity);
            updateStatement.setString(2, componentName);
            updateStatement.executeUpdate();

            updateStatement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertDataIntoGroupTable(String groupNumber, String prn, String firstName, String lastName,
                                          String className, String issueDate, String mobileNumber, String email,
                                          String returnDate, String componentName, int quantity) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/java1", "root", "Rinku@1219");

            String insertQuery = "INSERT INTO group5 (G_No, PRN, f_name, l_name, className, Issue_Date, mo_no, email, Return_Date, C_name, Quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setString(1, groupNumber);
            insertStatement.setString(2, prn);
            insertStatement.setString(3, firstName);
            insertStatement.setString(4, lastName);
            insertStatement.setString(5, className);
            insertStatement.setString(6, issueDate);
            insertStatement.setString(7, mobileNumber);
            insertStatement.setString(8, email);
            insertStatement.setString(9, returnDate);
            insertStatement.setString(10, componentName);
            insertStatement.setInt(11, quantity);

            insertStatement.executeUpdate();

            insertStatement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        groupNoField.setText("");
        prnField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        classField.setText("");
        issueDateChooser.setDate(null);
        mobileNoField.setText("");
        emailField.setText("");
        returnDateChooser.setDate(null);
        componentNameComboBox.setSelectedIndex(0);
        quantityField.setText("");
        availableQuantityField.setText("");
        newComponentField.setText("");
        addAnotherComponentCheckbox.setSelected(false);
        hideNewComponent();  // Hide the new component field
    }
}
