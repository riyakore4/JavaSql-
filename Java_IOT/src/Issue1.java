import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Issue1 {

    private JTextField quantityField;
    private JComboBox<String> componentNameComboBox;
    private JDateChooser issueDateChooser, returnDateChooser;
    private List<ChangeListener> changeListeners = new ArrayList<>();
    private Integer selectedGroupNumber;

    public Issue1(Integer selectedGroupNumber) {
        this.selectedGroupNumber = selectedGroupNumber;
        JFrame frame = new JFrame("ISSUE");
        frame.getContentPane().setBackground(Color.ORANGE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.getContentPane().setBackground(Color.ORANGE);

        // Panel to issue components
        JPanel issueComponentPanel = createIssueComponentPanel();
        frame.getContentPane().add(issueComponentPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private JPanel createIssueComponentPanel() {
        JPanel issueComponentPanel = new JPanel();
        issueComponentPanel.setLayout(new GridLayout(5, 2, 10, 10));
        issueComponentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel componentNameLabel = new JLabel("Component Name:");
        issueComponentPanel.add(componentNameLabel);

        componentNameComboBox = new JComboBox<>();
        loadComponentNames();
        issueComponentPanel.add(componentNameComboBox);

        JLabel quantityLabel = new JLabel("Quantity:");
        issueComponentPanel.add(quantityLabel);

        quantityField = new JTextField();
        issueComponentPanel.add(quantityField);

        JLabel issueDateLabel = new JLabel("Issue Date:");
        issueComponentPanel.add(issueDateLabel);

        issueDateChooser = new JDateChooser();
        issueComponentPanel.add(issueDateChooser);

        JLabel returnDateLabel = new JLabel("Return Date:");
        issueComponentPanel.add(returnDateLabel);

        returnDateChooser = new JDateChooser();
        issueComponentPanel.add(returnDateChooser);

        JButton submitButton = new JButton("Submit");
        issueComponentPanel.add(submitButton);

        // ActionListener for the Submit button
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addComponentToTable();
            }
        });

        return issueComponentPanel;
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

    private void addComponentToTable() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/java1", "root", "Rinku@1219");

            String componentName = (String) componentNameComboBox.getSelectedItem();
            int quantityUsed = Integer.parseInt(quantityField.getText());

            // Get PRN, last name, first name, class, mobile, and email from the first entry for the group
            String groupDetailsQuery = "SELECT PRN, l_name, f_name, className, mo_no, email FROM group5 WHERE G_No = ? LIMIT 1";
            PreparedStatement groupDetailsStatement = con.prepareStatement(groupDetailsQuery);
            groupDetailsStatement.setInt(1, selectedGroupNumber);
            ResultSet groupDetailsResult = groupDetailsStatement.executeQuery();

            int prn = 0; // default value
            String lastName = "SampleLastName";
            String firstName = "SampleFirstName";
            String className = "SampleClass";
            String mobile = "SampleMobile";
            String email = "SampleEmail";

            if (groupDetailsResult.next()) {
                prn = groupDetailsResult.getInt("PRN");
                lastName = groupDetailsResult.getString("l_name");
                firstName = groupDetailsResult.getString("f_name");
                className = groupDetailsResult.getString("className");
                mobile = groupDetailsResult.getString("mo_no");
                email = groupDetailsResult.getString("email");
            }

            // Insert data into the group5 table
            String insertQuery = "INSERT INTO group5 (G_No, PRN, l_name, f_name, className, mo_no, email, C_name, Quantity, Issue_Date, Return_Date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement st = con.prepareStatement(insertQuery);

            st.setInt(1, selectedGroupNumber);
            st.setInt(2, prn);
            st.setString(3, lastName);
            st.setString(4, firstName);
            st.setString(5, className);
            st.setString(6, mobile);
            st.setString(7, email);
            st.setString(8, componentName);
            st.setInt(9, quantityUsed);
            st.setString(10, formatDate(issueDateChooser.getDate()));
            st.setString(11, formatDate(returnDateChooser.getDate()));

            st.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data added successfully!");

            // Notify listeners (Issue class) that data has been added
            fireChangeEvent();

            // Update the quantity in the components table
            String updateQuantityQuery = "UPDATE components SET Quantity = Quantity - ? WHERE C_name = ?";
            PreparedStatement updateQuantityStatement = con.prepareStatement(updateQuantityQuery);
            updateQuantityStatement.setInt(1, quantityUsed);
            updateQuantityStatement.setString(2, componentName);
            updateQuantityStatement.executeUpdate();
            updateQuantityStatement.close();

            con.close();

            // Clear fields for a new component
            componentNameComboBox.setSelectedIndex(0);
            quantityField.setText("");
            issueDateChooser.setDate(null);
            returnDateChooser.setDate(null);

        } catch (ClassNotFoundException | SQLException | NumberFormatException ex) {
            ex.printStackTrace();
        }
    }

    private String formatDate(java.util.Date date) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Issue1(null));
    }

    // Add a change listener to the list
    public void addChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }

    // Notify all registered listeners that a change has occurred
    private void fireChangeEvent() {
        ChangeEvent event = new ChangeEvent(this);
        for (ChangeListener listener : changeListeners) {
            listener.stateChanged(event);
        }
    }
}

