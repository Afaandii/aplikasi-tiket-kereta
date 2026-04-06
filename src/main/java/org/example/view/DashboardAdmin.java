package org.example.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.example.model.User;

import javax.swing.*;
import java.awt.*;

public class DashboardAdmin extends JFrame {
    private final User currentUser;
    private CardLayout cardLayout;
    private JPanel mainContent;
    private final org.example.dao.DashboardDAO dashboardDAO;

    public DashboardAdmin(User user) {
        this.currentUser = user;
        this.dashboardDAO = new org.example.dao.DashboardDAO();
        initComponents();
    }

    private void initComponents() {
        setTitle("Rel Express - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Sidebar
        JPanel sidebar = createSidebar();
        
        // Header
        JPanel header = createHeader();

        // Main Content Area (CardLayout)
        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);
        mainContent.setBackground(new Color(30, 31, 34));

        // Add Panels
        mainContent.add(createOverviewPanel(), "Overview");
        mainContent.add(new StasiunManagementPanel(), "Stasiun");
        mainContent.add(new KeretaManagementPanel(), "Kereta");
        mainContent.add(new GerbongManagementPanel(), "Gerbong");
        mainContent.add(new JadwalManagementPanel(), "Jadwal");
        mainContent.add(new UserManagementPanel(), "User");
        mainContent.add(new LaporanAdminPanel(), "Laporan");

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
        JLabel lblBrand = new JLabel("REL EXPRESS ADMIN");
        lblBrand.setFont(new Font("Inter", Font.BOLD, 22));
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setHorizontalAlignment(SwingConstants.CENTER);
        lblBrand.setBorder(BorderFactory.createEmptyBorder(40, 20, 60, 20));

        // Menu Section
        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new GridLayout(0, 1, 0, 5));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        menuPanel.add(createMenuButton("Dashboard", "Overview"));
        menuPanel.add(createMenuButton("Stasiun", "Stasiun"));
        menuPanel.add(createMenuButton("Kereta", "Kereta"));
        menuPanel.add(createMenuButton("Gerbong & Kursi", "Gerbong"));
        menuPanel.add(createMenuButton("Jadwal", "Jadwal"));
        menuPanel.add(createMenuButton("Users", "User"));
        menuPanel.add(createMenuButton("Laporan Keuangan", "Laporan"));

        // Bottom Section (Logout)
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Inter", Font.BOLD, 15));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setBackground(new Color(255, 77, 77)); // Viberant Red
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

        JLabel lblTitle = new JLabel("Admin Dashboard");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblWelcome = new JLabel("Selamat Datang, " + currentUser.getUsername());
        lblWelcome.setFont(new Font("Inter", Font.PLAIN, 13));
        lblWelcome.setForeground(new Color(180, 180, 180));

        header.add(lblTitle, BorderLayout.WEST);
        header.add(lblWelcome, BorderLayout.EAST);
        return header;
    }

    private JPanel createOverviewPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel lblHeading = new JLabel("Dashboard Overview");
        lblHeading.setFont(new Font("Inter", Font.BOLD, 22));
        lblHeading.setForeground(Color.WHITE);
        lblHeading.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JPanel grid = new JPanel(new GridLayout(0, 3, 20, 20));
        grid.setOpaque(false);

        java.util.Map<String, Object> stats = dashboardDAO.getSummaryStats();

        grid.add(createStatCard("Total Stasiun", String.valueOf(stats.get("total_stasiun")), new Color(36, 123, 222)));
        grid.add(createStatCard("Total Kereta", String.valueOf(stats.get("total_kereta")), new Color(76, 175, 80)));
        grid.add(createStatCard("Total Jadwal", String.valueOf(stats.get("total_jadwal")), new Color(156, 39, 176)));
        grid.add(createStatCard("Total Gerbong", String.valueOf(stats.get("total_gerbong")), new Color(244, 67, 54)));
        grid.add(createStatCard("Total Users", String.valueOf(stats.get("total_user")), new Color(255, 152, 0)));
        grid.add(createStatCard("Total Pendapatan", formatCurrency((long) stats.get("total_pendapatan")), new Color(0, 188, 212)));

        p.add(lblHeading, BorderLayout.NORTH);
        p.add(grid, BorderLayout.CENTER);
        return p;
    }

    private String formatCurrency(long amount) {
        if (amount >= 1_000_000) {
            return String.format("Rp %.1f jt", (double) amount / 1_000_000).replace(".0", "");
        } else if (amount >= 1_000) {
            return String.format("Rp %.1f rb", (double) amount / 1_000).replace(".0", "");
        }
        return "Rp " + amount;
    }

    private JPanel createStatCard(String label, String value, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(new Color(43, 45, 48));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 20");

        // Top Accent Bar
        JPanel bar = new JPanel();
        bar.setBackground(accentColor);
        bar.setPreferredSize(new Dimension(0, 4));
        
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        lblLabel.setForeground(new Color(180, 180, 180));
        lblLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Inter", Font.BOLD, 32));
        lblValue.setForeground(Color.WHITE);

        card.add(bar, BorderLayout.NORTH);
        card.add(lblLabel, BorderLayout.CENTER);
        card.add(lblValue, BorderLayout.SOUTH);

        return card;
    }

    private void logout() {
        this.dispose();
        new LoginForm().setVisible(true);
    }
}
