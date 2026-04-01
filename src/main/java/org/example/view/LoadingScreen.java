package org.example.view;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class LoadingScreen extends JWindow {

    private JProgressBar progressBar;
    private JLabel lblStatus;

    public LoadingScreen() {
        initComponents();
    }

    private void initComponents() {
        setSize(500, 350);
        setLocationRelativeTo(null);

        // Custom Gradient Background Panel
        JPanel contentPane = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(15, 12, 41), getWidth(), getHeight(), new Color(48, 43, 99));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPane.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 30), 1));
        setContentPane(contentPane);

        // Center Panel for Logo and Text
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Logo
        JLabel lblLogo = new JLabel();
        try {
            URL logoUrl = getClass().getResource("/logo-rel-expres.png");
            if (logoUrl != null) {
                ImageIcon icon = new ImageIcon(logoUrl);
                Image img = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
                lblLogo.setIcon(new ImageIcon(img));
            } else {
                lblLogo.setText("REL EXPRESS");
                lblLogo.setFont(new Font("Inter", Font.BOLD, 36));
                lblLogo.setForeground(Color.WHITE);
            }
        } catch (Exception e) {
            lblLogo.setText("REL EXPRESS");
        }
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblAppName = new JLabel("Train Ticket System");
        lblAppName.setFont(new Font("Inter", Font.BOLD, 18));
        lblAppName.setForeground(new Color(220, 220, 220));
        lblAppName.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(lblLogo);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(lblAppName);
        centerPanel.add(Box.createVerticalGlue());

        // Bottom Panel for Progress
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 40, 50));

        lblStatus = new JLabel("Initializing Application...");
        lblStatus.setFont(new Font("Inter", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(180, 180, 180));
        lblStatus.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        progressBar = new JProgressBar(0, 100);
        progressBar.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 20;" +
                "height: 6;" +
                "background: rgba(0,0,0,0.3);" +
                "foreground: rgb(36, 123, 222);" +
                "paintOutsideClip: true");
        
        bottomPanel.add(lblStatus, BorderLayout.NORTH);
        bottomPanel.add(progressBar, BorderLayout.CENTER);

        contentPane.add(centerPanel, BorderLayout.CENTER);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);
    }

    public void startLoading(Runnable onComplete) {
        setVisible(true);
        
        // Simulation loading process
        Thread thread = new Thread(() -> {
            try {
                String[] statuses = {
                    "Loading Resources...",
                    "Checking Database Connection...",
                    "Initializing UI Components...",
                    "Almost ready...",
                    "Opening Login Page..."
                };

                for (int i = 0; i <= 100; i++) {
                    final int progress = i;
                    
                    if (i == 20) lblStatus.setText(statuses[0]);
                    if (i == 40) lblStatus.setText(statuses[1]);
                    if (i == 60) lblStatus.setText(statuses[2]);
                    if (i == 80) lblStatus.setText(statuses[3]);
                    if (i == 95) lblStatus.setText(statuses[4]);

                    SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
                    Thread.sleep(30); // Speed of loading
                }
                
                Thread.sleep(500); // Small pause at the end
                SwingUtilities.invokeLater(() -> {
                    dispose();
                    onComplete.run();
                });
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}
