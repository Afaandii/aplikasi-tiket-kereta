package org.example.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.example.dao.TransaksiDAO;
import org.example.model.Transaksi;
import org.example.model.User;
import raven.datetime.DatePicker;

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
    private JFormattedTextField txtStartDate, txtEndDate;
    private DatePicker datePickerStart, datePickerEnd;
    private JComboBox<String> cbMetode;

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

        lblTotalTiket = createStatLabel("Total Tiket Terjual", "0 Tiket", new Color(36, 123, 222));
        lblTotalPendapatan = createStatLabel("Total Uang Masuk", "Rp 0", new Color(76, 175, 80));

        statsPanel.add(lblTotalTiket);
        statsPanel.add(lblTotalPendapatan);

        // --- CENTER: TABLE & FILTERS ---
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createTitledBorder("Riwayat Transaksi Saya"));

        // Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterBar.setOpaque(false);

        // Start Date
        txtStartDate = new JFormattedTextField();
        datePickerStart = new DatePicker();
        datePickerStart.setEditor(txtStartDate);
        datePickerStart.setCloseAfterSelected(true);
        datePickerStart.setDateFormat("yyyy-MM-dd");

        // End Date
        txtEndDate = new JFormattedTextField();
        datePickerEnd = new DatePicker();
        datePickerEnd.setEditor(txtEndDate);
        datePickerEnd.setCloseAfterSelected(true);
        datePickerEnd.setDateFormat("yyyy-MM-dd");

        JButton btnFilter = new JButton("Filter");
        btnFilter.setBackground(new Color(51, 144, 255));
        btnFilter.setForeground(Color.WHITE);
        btnFilter.addActionListener(e -> loadLaporan());

        cbMetode = new JComboBox<>(new String[] { "Semua", "Tunai", "QRIS", "Debit" });
        cbMetode.addActionListener(e -> loadLaporan()); // Auto filter on change

        filterBar.add(new JLabel("Dari Tanggal:"));
        filterBar.add(txtStartDate);
        filterBar.add(new JLabel("Sampai Tanggal:"));
        filterBar.add(txtEndDate);
        filterBar.add(btnFilter);
        filterBar.add(new JLabel("Metode Pembayaran:"));
        filterBar.add(cbMetode);

        tablePanel.add(filterBar, BorderLayout.NORTH);

        String[] cols = { "Kode Transaksi", "Booking", "Customer", "Tiket", "Total Bayar", "Metode", "Waktu" };
        modelLaporan = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblLaporan = new JTable(modelLaporan);
        setupTable(tblLaporan);
        tablePanel.add(new JScrollPane(tblLaporan), BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        JButton btnRefresh = new JButton("Refresh Laporan");
        btnRefresh.setBackground(new Color(51, 144, 255));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.addActionListener(e -> {
            datePickerStart.setSelectedDate(null);
            datePickerEnd.setSelectedDate(null);
            cbMetode.setSelectedIndex(0);
            loadLaporan();
        });
        footer.add(btnRefresh, BorderLayout.EAST);
        footer.add(new JLabel("Laporan Performa Shift"), BorderLayout.WEST);

        add(statsPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
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
        JLabel l = new JLabel("<html><div style='text-align:center;'><b>" + title + "</b><br><span style='font-size:24px; color:rgb("
                + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ");'>" + value + "</span></div></html>");
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setOpaque(true);
        l.setBackground(new Color(43, 45, 48));
        l.setBorder(BorderFactory.createLineBorder(color, 2));
        l.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        return l;
    }

    private void loadLaporan() {
        modelLaporan.setRowCount(0);
        String start = txtStartDate.getText();
        String end = txtEndDate.getText();
        String method = cbMetode.getSelectedItem().toString();

        List<Transaksi> list = transaksiDAO.getFilteredTransactions(currentUser.getId(), start, end, method);

        long totalRevenue = 0;
        int totalTiket = 0;

        for (Transaksi t : list) {
            modelLaporan.addRow(new Object[] {
                    t.getKodeTransaksi(), t.getKodeBooking(), t.getNamaCustomer(),
                    t.getJumlahTiket(), "Rp " + String.format("%, d", t.getTotalBayar()),
                    t.getMetodePembayaran(), t.getCreatedAt()
            });
            totalRevenue += t.getTotalBayar();
            totalTiket += t.getJumlahTiket();
        }

        lblTotalTiket.setText("<html><div style='text-align:center;'><b>Total Tiket Terjual</b><br><span style='font-size:24px; color:#247BDE;'>"
                + totalTiket + " Tiket</span></div></html>");
        lblTotalPendapatan.setText("<html><div style='text-align:center;'><b>Total Uang Masuk</b><br><span style='font-size:24px; color:#4CAF50;'>Rp "
                + String.format("%, d", totalRevenue) + "</span></div></html>");
    }
}
