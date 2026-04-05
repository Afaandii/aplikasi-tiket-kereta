package org.example.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.example.model.User;

import javax.swing.*;
import java.awt.*;

public class DashboardKasir extends JFrame {
    private final User currentUser;
    private CardLayout cardLayout;
    private JPanel mainContent;

    public DashboardKasir(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("Rel Express - Cashier POS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 850);
        setLocationRelativeTo(null);

        // Sidebar
        JPanel sidebar = createSidebar();
        
        // Header
        JPanel header = createHeader();

        // Main Content Area (CardLayout)
        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);
        mainContent.setBackground(new Color(30, 31, 34));

        // Add Panels (Will create these next)
        mainContent.add(new BookingPanel(currentUser), "Booking");
        mainContent.add(new LaporanKasirPanel(currentUser), "Laporan");

        // Layout Assembly
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(header, BorderLayout.NORTH);
        rightPanel.add(mainContent, BorderLayout.CENTER);

        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBackground(new Color(43, 45, 48));
        sidebar.setLayout(new BorderLayout());

        // Brand Label
        JLabel lblBrand = new JLabel("REL EXPRESS POS");
        lblBrand.setFont(new Font("Inter", Font.BOLD, 22));
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setHorizontalAlignment(SwingConstants.CENTER);
        lblBrand.setBorder(BorderFactory.createEmptyBorder(40, 20, 60, 20));

        // Menu Section
        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new GridLayout(0, 1, 0, 5));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        menuPanel.add(createMenuButton("Booking Tiket", "Booking"));
        menuPanel.add(createMenuButton("Laporan Saya", "Laporan"));

        // Bottom Section (Logout)
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Inter", Font.BOLD, 15));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setBackground(new Color(255, 77, 77));
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        btnLogout.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        btnLogout.addActionListener(e -> logout());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        bottomPanel.add(btnLogout, BorderLayout.SOUTH);

        sidebar.add(lblBrand, BorderLayout.NORTH);
        sidebar.add(menuPanel, BorderLayout.CENTER);
        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton createMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Inter", Font.PLAIN, 14));
        btn.setForeground(new Color(220, 220, 220));
        btn.setBackground(new Color(50, 52, 55));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 15; borderWidth: 0");
        
        btn.addActionListener(e -> cardLayout.show(mainContent, cardName));
        
        return btn;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 31, 34));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50, 51, 54)));
        header.setPreferredSize(new Dimension(0, 80));
        header.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        JLabel lblTitle = new JLabel("Cashier POS Terminal");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblWelcome = new JLabel("Kasir: " + currentUser.getUsername());
        lblWelcome.setFont(new Font("Inter", Font.PLAIN, 13));
        lblWelcome.setForeground(new Color(180, 180, 180));

        header.add(lblTitle, BorderLayout.WEST);
        header.add(lblWelcome, BorderLayout.EAST);
        return header;
    }

    private void logout() {
        this.dispose();
        new LoginForm().setVisible(true);
    }
}
