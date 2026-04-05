package org.example.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.example.dao.TransaksiDAO;
import org.example.model.Transaksi;
import org.example.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LaporanKasirPanel extends JPanel {
    private final User currentUser;
    private final TransaksiDAO transaksiDAO;
    private JTable tblLaporan;
    private DefaultTableModel modelLaporan;
    private JLabel lblTotalTiket, lblTotalPendapatan;

    public LaporanKasirPanel(User user) {
        this.currentUser = user;
        this.transaksiDAO = new TransaksiDAO();
        initComponents();
        loadLaporan();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- TOP: STAT CARDS ---
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setPreferredSize(new Dimension(0, 120));

        lblTotalTiket = createStatLabel("Total Tiket Terjual Hari Ini", "0 Tiket", new Color(36, 123, 222));
        lblTotalPendapatan = createStatLabel("Uang Masuk Shift Ini", "Rp 0", new Color(76, 175, 80));

        statsPanel.add(lblTotalTiket);
        statsPanel.add(lblTotalPendapatan);

        // --- CENTER: TABLE ---
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createTitledBorder("Riwayat Transaksi Saya"));

        String[] cols = {"Kode Transaksi", "Booking", "Customer", "Tiket", "Total Bayar", "Metode", "Waktu"};
        modelLaporan = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tblLaporan = new JTable(modelLaporan);
        setupTable(tblLaporan);
        tablePanel.add(new JScrollPane(tblLaporan), BorderLayout.CENTER);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JButton btnRefresh = new JButton("Refresh Laporan");
        btnRefresh.setBackground(new Color(51, 144, 255));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.addActionListener(e -> loadLaporan());
        header.add(btnRefresh, BorderLayout.EAST);
        header.add(new JLabel("Laporan Performa Shift"), BorderLayout.WEST);

        add(statsPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(header, BorderLayout.SOUTH);
    }

    private void setupTable(JTable table) {
        table.setRowHeight(40);
        table.setFont(new Font("Inter", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 14));
        table.setShowGrid(true);
        table.setGridColor(new Color(60, 60, 60));
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, center);
    }

    private JLabel createStatLabel(String title, String value, Color color) {
        JLabel l = new JLabel("<html><div style='text-align:center;'><b>" + title + "</b><br><span style='font-size:24px; color:rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ");'>" + value + "</span></div></html>");
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setOpaque(true);
        l.setBackground(new Color(43, 45, 48));
        l.setBorder(BorderFactory.createLineBorder(color, 2));
        l.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        return l;
    }

    private void loadLaporan() {
        modelLaporan.setRowCount(0);
        List<Transaksi> list = transaksiDAO.getByUserId(currentUser.getId());
        
        long totalRevenue = 0;
        int totalTiket = 0;

        for (Transaksi t : list) {
            modelLaporan.addRow(new Object[]{
                t.getKodeTransaksi(), t.getKodeBooking(), t.getNamaCustomer(),
                t.getJumlahTiket(), "Rp " + String.format("%, d", t.getTotalBayar()),
                t.getMetodePembayaran(), t.getCreatedAt()
            });
            totalRevenue += t.getTotalBayar();
            totalTiket += t.getJumlahTiket();
        }

        lblTotalTiket.setText("<html><div style='text-align:center;'><b>Total Tiket Terjual Hari Ini</b><br><span style='font-size:24px; color:#247BDE;'>" + totalTiket + " Tiket</span></div></html>");
        lblTotalPendapatan.setText("<html><div style='text-align:center;'><b>Uang Masuk Shift Ini</b><br><span style='font-size:24px; color:#4CAF50;'>Rp " + String.format("%, d", totalRevenue) + "</span></div></html>");
    }
}
