import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class List {


    private JTextField searchField;

    public List() {
        createAndShowGUI();
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> new List());
    }

    private void createAndShowGUI() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/java1", "root", "Rinku@1219");

            JFrame frame = new JFrame("Component List");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.getContentPane().setBackground(new Color(240, 240, 240));

            // Create a DefaultTableModel to hold the data
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Sr_No");
            model.addColumn("Component Name");
            model.addColumn("Quantity");

            // Populate the table model with data from the ResultSet
            String query = "SELECT * FROM components";
            PreparedStatement st = con.prepareStatement(query);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                int Sr_no = rs.getInt("Sr_no");
                String C_name = rs.getString("C_name");
                int Quantity = rs.getInt("Quantity");
                model.addRow(new Object[]{Sr_no, C_name, Quantity});
            }

            // Create a JTable using the DefaultTableModel
            JTable table = new JTable(model);
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

            // Customize the appearance of the table
            table.setFont(new Font("Arial", Font.PLAIN, 12));
            table.setRowHeight(25);

            // Create JScrollPane to host the JTable
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(700, 400));

            // Add the JScrollPane to the frame's content pane
            frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

            // Search Panel
            JPanel searchPanel = new JPanel(new FlowLayout());
            JLabel searchLabel = new JLabel("Search Component:");
            searchField = new JTextField(20);
            JButton searchButton = new JButton("Search");

            // ActionListener for the Search button
            searchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String searchTerm = searchField.getText().trim().toLowerCase();
                    if (!searchTerm.isEmpty()) {
                        // Search for the component in the table
                        boolean componentFound = false;
                        for (int i = 0; i < table.getRowCount(); i++) {
                            String componentName = table.getValueAt(i, 1).toString().toLowerCase();
                            if (componentName.contains(searchTerm)) {
                                // Component found, highlight the row
                                table.setRowSelectionInterval(i, i);
                                componentFound = true;
                                break;
                            }
                        }

                        if (!componentFound) {
                            JOptionPane.showMessageDialog(frame, "Component not found.");
                        }
                    }
                }
            });

            // Add components to the search panel
            searchPanel.add(searchLabel);
            searchPanel.add(searchField);
            searchPanel.add(searchButton);

            // Add the search panel to the frame's content pane
            frame.getContentPane().add(searchPanel, BorderLayout.SOUTH);

            // Center the frame on the screen
            frame.setLocationRelativeTo(null);

            // Set frame visibility
            frame.setVisible(true);

            // Close resources
            rs.close();
            st.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "There is a SQL Error");
        }
    }
}
