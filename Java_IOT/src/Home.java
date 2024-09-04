import javax.swing.*;
import java.awt.*;

public class Home {

    private static final String BACKGROUND_IMAGE_PATH = "C:/Users/Proventeq/Desktop/Java swing/Java_Project_IOT/src/Background.jpeg";
    private JFrame homeFrame;

    public Home() {
        createAndShowGUI();
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> new Home());
    }

    private void createAndShowGUI() {
        homeFrame = new JFrame();
        JButton listButton, groupButton, issueButton, registeredButton;

        homeFrame.setSize(1400, 800);
        ImageIcon backgroundImage = new ImageIcon(BACKGROUND_IMAGE_PATH);
        Image img = backgroundImage.getImage().getScaledInstance(homeFrame.getWidth(), homeFrame.getHeight(), Image.SCALE_SMOOTH);
        backgroundImage = new ImageIcon(img);

        JLabel background = new JLabel(backgroundImage);
        background.setBounds(0, 0, homeFrame.getWidth(), homeFrame.getHeight());
        homeFrame.setContentPane(background);

        listButton = createButton("List", 400, 200);
        groupButton = createButton("Group", 750, 200);
        issueButton = createButton("Issue", 400, 450);
        registeredButton = createButton("Registerd", 750, 450);

        listButton.addActionListener(e -> openListScreen());
        groupButton.addActionListener(e -> openGroupScreen());
        issueButton.addActionListener(e -> openIssueScreen());
        registeredButton.addActionListener(e -> openRegisteredScreen());

        homeFrame.setLayout(null);
        homeFrame.setLocationRelativeTo(null);
        homeFrame.setVisible(true);
        homeFrame.setResizable(true);
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close application on window close
    }

    private JButton createButton(String label, int x, int y) {
        JButton button = new JButton(label);
        button.setBounds(x, y, 200, 70);
        button.setFont(new Font("Times New Roman", Font.BOLD, 26));
        button.setForeground(Color.RED);
        button.setBackground(Color.WHITE);
        homeFrame.add(button);
        return button;
    }

    private void openListScreen() {
        try {
            List list = new List();
        } catch (Exception ex) {
            handleException("List Screen", ex);
        }
    }

    private void openGroupScreen() {
        try {
            Group group = new Group();
        } catch (Exception ex) {
            handleException("Group Screen", ex);
        }
    }

    private void openIssueScreen() {
        try {
            Issue Issue = new Issue();
        } catch (Exception ex) {
            handleException("Issue Screen", ex);
        }
    }
    private void openRegisteredScreen(){
        try{
            Registered_Groups r = new Registered_Groups();
        }catch(Exception er){
            JOptionPane.showMessageDialog(null,"Something Wrong");
        }
    }

    private void handleException(String screen, Exception ex) {
        JOptionPane.showMessageDialog(null, "Error opening " + screen + ": " + ex.getMessage());
    }
}

