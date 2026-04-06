package org.example.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.example.dao.UserDAO;
import org.example.model.User;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class LoginForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private UserDAO userDAO;

    public LoginForm() {
        userDAO = new UserDAO();
        initComponents();
    }

    private void initComponents() {
        setTitle("Rel Express - Modern Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true); // Biar bisa di-resize
        setSize(600, 600);
        setLocationRelativeTo(null);

        // Custom Gradient Background
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(15, 12, 41), getWidth(), getHeight(),
                        new Color(48, 43, 99));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // Glassmorphism-lite Login Card
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(255, 255, 255, 10)); // Slightly less translucent
        card.setBorder(BorderFactory.createEmptyBorder(60, 100, 60, 100)); // More padding
        card.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 40;" +
                "border: 1,1,1,1,rgba(255,255,255,0.08)");
        card.setMaximumSize(new Dimension(650, 650)); // Limit card width

        // Logo Image
        JLabel lblLogo = new JLabel();
        try {
            URL logoUrl = getClass().getResource("/logo-rel-expres.png");
            if (logoUrl != null) {
                ImageIcon originalIcon = new ImageIcon(logoUrl);
                Image img = originalIcon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
                lblLogo.setIcon(new ImageIcon(img));
            } else {
                lblLogo.setText("REL EXPRESS");
                lblLogo.setFont(new Font("Inter", Font.BOLD, 48));
                lblLogo.setForeground(Color.WHITE);
            }
        } catch (Exception e) {
            lblLogo.setText("REL EXPRESS");
        }
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Welcome Header
        JLabel lblHeader = new JLabel("Train Station Sign-In");
        lblHeader.setFont(new Font("Inter", Font.BOLD, 26));
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Enter your credentials to manage tickets");
        lblSub.setFont(new Font("Inter", Font.PLAIN, 15));
        lblSub.setForeground(new Color(180, 180, 180));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username Field (Bigger)
        txtUsername = new JTextField();
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Username");
        txtUsername.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 15;" +
                "margin: 10,25,10,25;" +
                "background: rgba(0,0,0,0.3);" +
                "focusedBackground: rgba(0,0,0,0.4)");
        txtUsername.setPreferredSize(new Dimension(450, 65));
        txtUsername.setMaximumSize(new Dimension(450, 65));

        // Password Field (Bigger)
        txtPassword = new JPasswordField();
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 15;" +
                "margin: 10,25,10,25;" +
                "background: rgba(0,0,0,0.3);" +
                "focusedBackground: rgba(0,0,0,0.4);" +
                "showRevealButton: true");
        txtPassword.setPreferredSize(new Dimension(450, 65));
        txtPassword.setMaximumSize(new Dimension(450, 65));

        // Login Button (Bigger & Vibrant)
        btnLogin = new JButton("Sign In");
        btnLogin.setFont(new Font("Inter", Font.BOLD, 18));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(new Color(51, 144, 255));
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 15;" +
                "borderWidth: 0;" +
                "focusWidth: 0");
        btnLogin.setPreferredSize(new Dimension(450, 65));
        btnLogin.setMaximumSize(new Dimension(450, 65));

        // Animations / Hover (FlatLaf handled basics, but we can set specific colors)
        btnLogin.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);

        // Enter Key Support
        getRootPane().setDefaultButton(btnLogin);

        // Event Handling
        btnLogin.addActionListener(e -> handleLogin());

        // Assemble components
        card.add(lblLogo);
        card.add(Box.createVerticalStrut(25));
        card.add(lblHeader);
        card.add(lblSub);
        card.add(Box.createVerticalStrut(40));
        card.add(txtUsername);
        card.add(Box.createVerticalStrut(20));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(35));
        card.add(btnLogin);

        mainPanel.add(card);
        add(mainPanel);
    }

    private void handleLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fields cannot be empty!", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userDAO.login(username, password);
        if (user != null) {
            // Role Check (1 = Admin, 2 = Cashier)
            if (user.getRoleId() == 1) {
                JOptionPane.showMessageDialog(this, "Welcome Admin, " + user.getUsername() + "!",
                        "Authentication Success",
                        JOptionPane.INFORMATION_MESSAGE);
                DashboardAdmin dashboard = new DashboardAdmin(user);
                dashboard.setVisible(true);
                this.dispose();
            } else if (user.getRoleId() == 2) {
                JOptionPane.showMessageDialog(this, "Welcome Cashier, " + user.getUsername() + "!",
                        "Authentication Success",
                        JOptionPane.INFORMATION_MESSAGE);
                DashboardKasir dashboard = new DashboardKasir(user);
                dashboard.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Role assigned to this user.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Profile not found or invalid Credentials.", "Authentication Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
