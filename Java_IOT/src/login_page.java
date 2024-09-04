import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class login_page {

    JFrame frame7;
    JLabel title, user, password, background;
    JTextField username, Password;
    JButton login, reset;

    public static void main(String args[]) {
        new login_page();
    }

    login_page() {
        frame7 = new JFrame();
        frame7.setTitle("Administration");
        frame7.setSize(1400, 800); // Increased frame size

        ImageIcon backgroundImage = new ImageIcon("C:\\Users\\Proventeq\\Desktop\\Java swing\\Java_IOT\\src\\iot.jpeg.jpeg");
        Image img = backgroundImage.getImage().getScaledInstance(frame7.getWidth(), frame7.getHeight(), Image.SCALE_SMOOTH);
        backgroundImage = new ImageIcon(img);

        background = new JLabel(backgroundImage);
        background.setBounds(0, 0, frame7.getWidth(), frame7.getHeight());
        title = new JLabel("WELCOME TO CSIT");
        title.setBounds(200, 30, 400, 60); // Increased font size and label size
        title.setFont(new Font("Serif", Font.BOLD, 40)); // Changed font style
        title.setForeground(Color.RED);

        user = new JLabel("Username");
        user.setBounds(150, 150, 200, 40);
        user.setFont(new Font("Arial", Font.BOLD, 30)); // Changed font style
        user.setForeground(Color.white);
        password = new JLabel("Password");
        password.setBounds(150, 250, 200, 40);
        password.setFont(new Font("Arial", Font.BOLD, 30)); // Changed font style
        password.setForeground(Color.white);

        username = new JTextField();
        username.setBounds(350, 150, 200, 40);
        Password = new JPasswordField();
        Password.setBounds(350, 250, 200, 40);

        login = new JButton("Login");
        login.setBounds(100, 350, 150, 50); // Increased button size
        login.setBackground(Color.GREEN);
        login.setFont(new Font("Arial", Font.BOLD, 20)); // Changed font style
        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent a) {
                String g_username = username.getText();
                String g_password = Password.getText();
                try {
                    if (g_username.equals("user") && g_password.equals("Admin@123")) {
                        Home home_page = new Home();
                        frame7.dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid Details");
                    }
                } catch (Exception exception) {
                    System.out.println("Enter valid Details!!");
                }
            }
        });

        reset = new JButton("Reset");
        reset.setBounds(400, 350, 150, 50); // Increased button size
        reset.setBackground(Color.RED);
        reset.setFont(new Font("Arial", Font.BOLD, 20)); // Changed font style
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    username.setText("");
                    Password.setText("");
                } catch (Exception ee) {
                    JOptionPane.showMessageDialog(null, "Clear Data");
                }
            }
        });

        frame7.setLayout(null);
        frame7.setContentPane(background);

        background.add(title);
        background.add(user);
        background.add(password);
        background.add(login);
        background.add(reset);
        background.add(username);
        background.add(Password);

        frame7.setLocationRelativeTo(null);
        frame7.setVisible(true);
        frame7.setResizable(true);
        frame7.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
