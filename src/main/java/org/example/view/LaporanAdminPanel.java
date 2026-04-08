package org.example.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.example.dao.TransaksiDAO;
import org.example.dao.UserDAO;
import org.example.model.Transaksi;
import raven.datetime.DatePicker;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LaporanAdminPanel extends JPanel {
    private final TransaksiDAO transaksiDAO;
    private final UserDAO userDAO;
    private JTable tblLaporan;
    private DefaultTableModel modelLaporan;
    private JLabel lblTotalTiket, lblTotalOmzet, lblTotalLaba;
    private JFormattedTextField txtStartDate, txtEndDate;
    private DatePicker datePickerStart, datePickerEnd;
    private JComboBox<ComboItem> cbKasir;
    private JComboBox<String> cbMetode;

    public LaporanAdminPanel() {
        this.transaksiDAO = new TransaksiDAO();
        this.userDAO = new UserDAO();
        initComponents();
        loadLaporan();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- TOP: STAT CARDS ---
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setPreferredSize(new Dimension(0, 120));

        lblTotalTiket = createStatLabel("Total Tiket Terjual", "0 Tiket", new Color(36, 123, 222));
        lblTotalOmzet = createStatLabel("Total Omzet", "Rp 0", new Color(76, 175, 80));
        lblTotalLaba = createStatLabel("Estimasi Laba", "Rp 0", new Color(156, 39, 176));

        statsPanel.add(lblTotalTiket);
        statsPanel.add(lblTotalOmzet);
        statsPanel.add(lblTotalLaba);

        // --- CENTER: TABLE & FILTERS ---
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createTitledBorder("Seluruh Riwayat Transaksi"));

        // Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterBar.setOpaque(false);

        // Date Pickers
        txtStartDate = new JFormattedTextField();
        datePickerStart = new DatePicker();
        datePickerStart.setEditor(txtStartDate);
        datePickerStart.setCloseAfterSelected(true);
        datePickerStart.setDateFormat("yyyy-MM-dd");

        txtEndDate = new JFormattedTextField();
        datePickerEnd = new DatePicker();
        datePickerEnd.setEditor(txtEndDate);
        datePickerEnd.setCloseAfterSelected(true);
        datePickerEnd.setDateFormat("yyyy-MM-dd");

        JButton btnFilter = new JButton("Filter");
        btnFilter.setBackground(new Color(51, 144, 255));
        btnFilter.setForeground(Color.WHITE);
        btnFilter.addActionListener(e -> loadLaporan());

        // Kasir Filter
        cbKasir = new JComboBox<>();
        cbKasir.addItem(new ComboItem(0, "Semua Kasir"));
        userDAO.getByRoleId(2).forEach(u -> {
            if (u.getRoleId() == 2) {
                cbKasir.addItem(new ComboItem(u.getId(), u.getUsername()));
            }
        });
        cbKasir.addActionListener(e -> loadLaporan());

        // Metode Filter
        cbMetode = new JComboBox<>(new String[] { "Semua", "Tunai", "QRIS" });
        cbMetode.addActionListener(e -> loadLaporan());

        filterBar.add(new JLabel("Dari Tanggal:"));
        filterBar.add(txtStartDate);
        filterBar.add(new JLabel("Sampai Tanggal:"));
        filterBar.add(txtEndDate);
        filterBar.add(btnFilter);
        filterBar.add(new JLabel("Kasir:"));
        filterBar.add(cbKasir);
        filterBar.add(new JLabel("Metode:"));
        filterBar.add(cbMetode);

        tablePanel.add(filterBar, BorderLayout.NORTH);

        String[] cols = { "No", "Kode TRX", "Booking", "Customer", "Kasir", "Tiket", "Total Bayar", "Metode", "Waktu", "ID" };
        modelLaporan = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblLaporan = new JTable(modelLaporan);
        setupTable(tblLaporan);
        tablePanel.add(new JScrollPane(tblLaporan), BorderLayout.CENTER);
        tblLaporan.getColumnModel().getColumn(9).setMinWidth(0);
        tblLaporan.getColumnModel().getColumn(9).setMaxWidth(0);
        tblLaporan.getColumnModel().getColumn(9).setWidth(0);

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        JButton btnRefresh = new JButton("Reset & Refresh");
        btnRefresh.setBackground(new Color(43, 45, 48));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.addActionListener(e -> {
            datePickerStart.setSelectedDate(null);
            datePickerEnd.setSelectedDate(null);
            cbKasir.setSelectedIndex(0);
            cbMetode.setSelectedIndex(0);
            loadLaporan();
        });
        footer.add(btnRefresh, BorderLayout.EAST);
        footer.add(new JLabel("Laporan Keuangan Global"), BorderLayout.WEST);

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

        // Adjust column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(5).setPreferredWidth(60);
    }

    private JLabel createStatLabel(String title, String value, Color color) {
        JLabel l = new JLabel(
                "<html><div style='text-align:center;'><b>" + title + "</b><br><span style='font-size:24px; color:rgb("
                        + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ");'>" + value
                        + "</span></div></html>");
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setOpaque(true);
        l.setBackground(new Color(43, 45, 48));
        l.setBorder(BorderFactory.createLineBorder(color, 2));
        l.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        return l;
    }

    private void loadLaporan() {
        modelLaporan.setRowCount(0);
        String start = txtStartDate.getText() != null ? txtStartDate.getText().trim() : "";
        String end = txtEndDate.getText() != null ? txtEndDate.getText().trim() : "";

        // Pastikan hanya mem-parsing jika formatnya adalah YYYY-MM-DD
        if (!start.matches("\\d{4}-\\d{2}-\\d{2}")) start = "";
        if (!end.matches("\\d{4}-\\d{2}-\\d{2}")) end = "";

        String method = cbMetode.getSelectedItem().toString();
        ComboItem selectedKasir = (ComboItem) cbKasir.getSelectedItem();

        List<Transaksi> list = transaksiDAO.getFilteredTransactions(
                selectedKasir != null ? selectedKasir.id : 0,
                start, end, method);

        long totalOmzet = 0;
        int totalTiket = 0;
        int no = 1;

        for (Transaksi t : list) {
            modelLaporan.addRow(new Object[] {
                    no++, t.getKodeTransaksi(), t.getKodeBooking(), t.getNamaCustomer(),
                    t.getUsername(), t.getJumlahTiket(), "Rp " + String.format("%, d", t.getTotalBayar()),
                    t.getMetodePembayaran(), t.getCreatedAt(), t.getId()
            });
            totalOmzet += t.getTotalBayar();
            totalTiket += t.getJumlahTiket();
        }

        // Profit Assumption: 100% since no cost is provided, or we can use a
        // placeholder percentage
        long totalLaba = totalOmzet; // Defaulting to 100% of revenue as profit (service industry)

        lblTotalTiket.setText(
                "<html><div style='text-align:center;'><b>Total Tiket Terjual</b><br><span style='font-size:24px; color:#247BDE;'>"
                        + totalTiket + " Tiket</span></div></html>");
        lblTotalOmzet.setText(
                "<html><div style='text-align:center;'><b>Total Omzet</b><br><span style='font-size:24px; color:#4CAF50;'>Rp "
                        + String.format("%, d", totalOmzet) + "</span></div></html>");
        lblTotalLaba.setText(
                "<html><div style='text-align:center;'><b>Estimasi Laba</b><br><span style='font-size:24px; color:#9C27B0;'>Rp "
                        + String.format("%, d", totalLaba) + "</span></div></html>");
    }

    private static class ComboItem {
        int id;
        String name;

        ComboItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
