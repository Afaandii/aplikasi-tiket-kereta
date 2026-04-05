package org.example.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.example.dao.*;
import org.example.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingPanel extends JPanel {
    private final User currentUser;
    private final JadwalDAO jadwalDAO;
    private final StasiunDAO stasiunDAO;
    private final JadwalKursiDAO kursiDAO;
    private final JadwalHargaDAO hargaDAO;
    private final TransaksiDAO transaksiDAO;

    private JTable tblJadwal;
    private DefaultTableModel modelJadwal;
    private JComboBox<ComboItem> cbAsal, cbTujuan;
    private JPanel seatMapPanel;
    private JLabel lblSelectedSeats, lblTotalPrice;
    
    private List<JadwalKursi> selectedSeats = new ArrayList<>();
    private Jadwal selectedJadwal = null;
    private int ticketPrice = 0;

    public BookingPanel(User user) {
        this.currentUser = user;
        this.jadwalDAO = new JadwalDAO();
        this.stasiunDAO = new StasiunDAO();
        this.kursiDAO = new JadwalKursiDAO();
        this.hargaDAO = new JadwalHargaDAO();
        this.transaksiDAO = new TransaksiDAO();

        initComponents();
        loadStasiun();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- TOP: SEARCH ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        searchPanel.setOpaque(false);
        
        cbAsal = new JComboBox<>();
        cbTujuan = new JComboBox<>();
        JButton btnSearch = new JButton("Cari Jadwal");
        btnSearch.setBackground(new Color(51, 144, 255));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> searchJadwal());

        searchPanel.add(new JLabel("Asal:")); searchPanel.add(cbAsal);
        searchPanel.add(new JLabel("Tujuan:")); searchPanel.add(cbTujuan);
        searchPanel.add(btnSearch);

        // --- CENTER: SPLIT PANE ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setOpaque(false);

        // LEFT: Schedule List
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createTitledBorder("Pilih Jadwal"));

        String[] cols = {"ID", "Kereta", "Waktu Berangkat", "Status"};
        modelJadwal = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tblJadwal = new JTable(modelJadwal);
        setupTable(tblJadwal);
        tblJadwal.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectJadwal();
        });
        leftPanel.add(new JScrollPane(tblJadwal), BorderLayout.CENTER);

        // RIGHT: Seat Map & Checkout
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createTitledBorder("Pilih Kursi & Checkout"));

        seatMapPanel = new JPanel(new GridLayout(0, 4, 10, 10)); // 4 columns (A, B, C, D)
        seatMapPanel.setOpaque(false);
        JScrollPane seatScroll = new JScrollPane(seatMapPanel);
        seatScroll.setOpaque(false);
        seatScroll.getViewport().setOpaque(false);

        JPanel checkoutInfo = new JPanel(new GridLayout(0, 1, 5, 5));
        checkoutInfo.setOpaque(false);
        checkoutInfo.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        lblSelectedSeats = new JLabel("Kursi Terpilih: -");
        lblSelectedSeats.setFont(new Font("Inter", Font.BOLD, 14));
        lblSelectedSeats.setForeground(Color.WHITE);

        lblTotalPrice = new JLabel("Total Harga: Rp 0");
        lblTotalPrice.setFont(new Font("Inter", Font.BOLD, 20));
        lblTotalPrice.setForeground(new Color(76, 175, 80));

        JButton btnCheckout = new JButton("Bayar & Cetak Tiket");
        btnCheckout.setFont(new Font("Inter", Font.BOLD, 16));
        btnCheckout.setBackground(new Color(76, 175, 80));
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        btnCheckout.addActionListener(e -> showCheckoutDialog());

        checkoutInfo.add(lblSelectedSeats);
        checkoutInfo.add(lblTotalPrice);
        checkoutInfo.add(Box.createVerticalStrut(10));
        checkoutInfo.add(btnCheckout);

        rightPanel.add(seatScroll, BorderLayout.CENTER);
        rightPanel.add(checkoutInfo, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        add(searchPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private void setupTable(JTable table) {
        table.setRowHeight(40);
        table.setFont(new Font("Inter", Font.PLAIN, 14));
        table.setShowGrid(true);
        table.setGridColor(new Color(60, 60, 60));
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, center);
    }

    private void loadStasiun() {
        stasiunDAO.getAll().forEach(s -> {
            cbAsal.addItem(new ComboItem(s.getId(), s.getNamaStasiun()));
            cbTujuan.addItem(new ComboItem(s.getId(), s.getNamaStasiun()));
        });
    }

    private void searchJadwal() {
        modelJadwal.setRowCount(0);
        resetSelections();
        
        Object a = cbAsal.getSelectedItem();
        Object t = cbTujuan.getSelectedItem();
        
        if (a == null || t == null) return;

        int asalId = ((ComboItem) a).id;
        int tujuanId = ((ComboItem) t).id;
        
        List<Jadwal> results = jadwalDAO.getByRoute(asalId, tujuanId);
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada jadwal aktif untuk rute ini.");
            return;
        }

        results.forEach(j -> modelJadwal.addRow(new Object[]{
            j.getId(), j.getNamaKereta(), j.getWaktuBerangkat(), j.getStatus()
        }));
    }

    private void selectJadwal() {
        int row = tblJadwal.getSelectedRow();
        if (row != -1) {
            int id = (int) tblJadwal.getValueAt(row, 0);
            selectedJadwal = jadwalDAO.getAll().stream().filter(j -> j.getId() == id).findFirst().orElse(null);
            
            // Get price (assume first class found for simplicity, or add class selection)
            List<JadwalHarga> prices = hargaDAO.getByJadwalId(id);
            if (!prices.isEmpty()) {
                ticketPrice = prices.get(0).getHargaTiket();
            } else {
                ticketPrice = 0;
                JOptionPane.showMessageDialog(this, "Harga untuk jadwal ini belum diatur!");
            }
            
            loadSeatMap(id);
            resetSelections();
        }
    }

    private void loadSeatMap(int jadwalId) {
        seatMapPanel.removeAll();
        List<JadwalKursi> list = kursiDAO.getByJadwalId(jadwalId);
        
        for (JadwalKursi k : list) {
            JButton btnSeat = new JButton(k.getKodeKursi());
            btnSeat.setPreferredSize(new Dimension(60, 60));
            btnSeat.setFont(new Font("Inter", Font.BOLD, 12));
            btnSeat.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

            if (k.getStatus().equals("Terisi")) {
                btnSeat.setBackground(new Color(80, 80, 80));
                btnSeat.setForeground(new Color(150, 150, 150));
                btnSeat.setEnabled(false);
            } else {
                btnSeat.setBackground(new Color(50, 52, 55));
                btnSeat.setForeground(Color.WHITE);
                btnSeat.addActionListener(e -> toggleSeatSelection(btnSeat, k));
            }
            seatMapPanel.add(btnSeat);
        }
        seatMapPanel.revalidate();
        seatMapPanel.repaint();
    }

    private void toggleSeatSelection(JButton btn, JadwalKursi k) {
        if (selectedSeats.contains(k)) {
            selectedSeats.remove(k);
            btn.setBackground(new Color(50, 52, 55));
        } else {
            selectedSeats.add(k);
            btn.setBackground(new Color(76, 175, 80));
        }
        updateCheckoutInfo();
    }

    private void updateCheckoutInfo() {
        if (selectedSeats.isEmpty()) {
            lblSelectedSeats.setText("Kursi Terpilih: -");
            lblTotalPrice.setText("Total Harga: Rp 0");
        } else {
            StringBuilder sb = new StringBuilder("Kursi Terpilih: ");
            for (int i = 0; i < selectedSeats.size(); i++) {
                sb.append(selectedSeats.get(i).getKodeKursi()).append(i == selectedSeats.size() - 1 ? "" : ", ");
            }
            lblSelectedSeats.setText(sb.toString());
            lblTotalPrice.setText("Total Harga: Rp " + String.format("%, d", (long) selectedSeats.size() * ticketPrice));
        }
    }

    private void resetSelections() {
        selectedSeats.clear();
        updateCheckoutInfo();
    }

    private void showCheckoutDialog() {
        if (selectedJadwal == null || selectedSeats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih jadwal dan kursi terlebih dahulu!");
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Finalisasi Pembayaran", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridLayout(0, 1, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JTextField fNama = new JTextField();
        JComboBox<String> cbMetode = new JComboBox<>(new String[]{"Tunai", "QRIS", "Debit"});

        p.add(new JLabel("Nama Penumpang:")); p.add(fNama);
        p.add(new JLabel("Metode Pembayaran:")); p.add(cbMetode);
        p.add(new JLabel("Total yang harus dibayar:"));
        JLabel lblFinalTotal = new JLabel("Rp " + String.format("%, d", (long) selectedSeats.size() * ticketPrice));
        lblFinalTotal.setFont(new Font("Inter", Font.BOLD, 24));
        lblFinalTotal.setForeground(new Color(76, 175, 80));
        p.add(lblFinalTotal);

        JButton btnFinal = new JButton("Konfirmasi & Bayar");
        btnFinal.addActionListener(e -> {
            if (fNama.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Nama harus diisi!");
                return;
            }

            Transaksi t = new Transaksi();
            t.setUserId(currentUser.getId());
            t.setKodeTransaksi("TRX-" + System.currentTimeMillis());
            t.setKodeBooking(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            t.setNamaCustomer(fNama.getText());
            t.setTotalBayar((long) selectedSeats.size() * ticketPrice);
            t.setJumlahTiket(selectedSeats.size());
            t.setStatus("Lunas");
            t.setMetodePembayaran(cbMetode.getSelectedItem().toString());

            List<DetailTransaksi> details = new ArrayList<>();
            for (JadwalKursi k : selectedSeats) {
                DetailTransaksi dt = new DetailTransaksi();
                dt.setJadwalKursiId(k.getId());
                dt.setHarga(ticketPrice);
                details.add(dt);
            }

            if (transaksiDAO.saveFullTransaction(t, details)) {
                JOptionPane.showMessageDialog(dialog, "Transaksi Berhasil!\nKode Booking: " + t.getKodeBooking());
                dialog.dispose();
                loadSeatMap(selectedJadwal.getId());
                resetSelections();
            } else {
                JOptionPane.showMessageDialog(dialog, "Gagal memproses transaksi!");
            }
        });

        dialog.add(p, BorderLayout.CENTER);
        dialog.add(btnFinal, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private static class ComboItem {
        int id; String name;
        ComboItem(int id, String name) { this.id = id; this.name = name; }
        public String toString() { return name; }
    }
}
