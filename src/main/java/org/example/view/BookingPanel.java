package org.example.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.example.dao.*;
import org.example.model.*;
import raven.datetime.DatePicker;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
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
    private final GerbongDAO gerbongDAO;

    private JTable tblJadwal;
    private DefaultTableModel modelJadwal;
    private JComboBox<ComboItem> cbTujuan;
    private JFormattedTextField txtTanggal;
    private DatePicker datePicker;

    private JComboBox<ComboItem> cbGerbong;
    private JPanel seatMapPanel;

    private JLabel lblSelectedSeats, lblTotalPrice;
    private JTextField fNamaCustomer;
    private JComboBox<String> cbMetodePembayaran;

    private List<JadwalKursi> selectedSeats = new ArrayList<>();
    private Jadwal selectedJadwal = null;
    private int ticketPrice = 0;
    private int idStasiunGubeng = -1;

    public BookingPanel(User user) {
        this.currentUser = user;
        this.jadwalDAO = new JadwalDAO();
        this.stasiunDAO = new StasiunDAO();
        this.kursiDAO = new JadwalKursiDAO();
        this.hargaDAO = new JadwalHargaDAO();
        this.transaksiDAO = new TransaksiDAO();
        this.gerbongDAO = new GerbongDAO();

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

        cbTujuan = new JComboBox<>();
        txtTanggal = new JFormattedTextField();
        txtTanggal.setPreferredSize(new Dimension(150, 30));
        datePicker = new DatePicker();
        datePicker.setEditor(txtTanggal);
        datePicker.setCloseAfterSelected(true);
        datePicker.setDateFormat("yyyy-MM-dd");
        datePicker.setSelectedDate(LocalDate.now());

        JButton btnSearch = new JButton("Cari Jadwal");
        btnSearch.setBackground(new Color(51, 144, 255));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> searchJadwal());

        JLabel lblOrigin = new JLabel("Keberangkatan: Stasiun Gubeng");
        lblOrigin.setFont(new Font("Inter", Font.BOLD, 14));
        lblOrigin.setForeground(new Color(220, 220, 220));

        searchPanel.add(lblOrigin);
        searchPanel.add(Box.createHorizontalStrut(15));
        searchPanel.add(new JLabel("Tujuan:"));
        searchPanel.add(cbTujuan);
        searchPanel.add(new JLabel("Tanggal:"));
        searchPanel.add(txtTanggal);
        searchPanel.add(btnSearch);

        // --- CENTER: SPLIT PANE ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(450);
        splitPane.setOpaque(false);

        // LEFT: Schedule List
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createTitledBorder("Pilih Jadwal"));

        String[] cols = { "Nama Kereta", "Jam Berangkat", "Status" };
        modelJadwal = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblJadwal = new JTable(modelJadwal);
        setupTable(tblJadwal);
        tblJadwal.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                selectJadwal();
        });
        leftPanel.add(new JScrollPane(tblJadwal), BorderLayout.CENTER);

        // RIGHT: Seat Map & Checkout
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createTitledBorder("Pilih Kursi & Checkout"));

        // Right Top: Gerbong Selection
        JPanel gerbongPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gerbongPanel.setOpaque(false);
        cbGerbong = new JComboBox<>();
        cbGerbong.addActionListener(e -> {
            if (cbGerbong.getSelectedItem() != null && selectedJadwal != null) {
                int gerbongId = ((ComboItem) cbGerbong.getSelectedItem()).id;
                loadSeatMap(selectedJadwal.getId(), gerbongId);
            }
        });
        gerbongPanel.add(new JLabel("Gerbong / Kelas:"));
        gerbongPanel.add(cbGerbong);

        // Right Center: Seat Map
        seatMapPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        seatMapPanel.setOpaque(false);
        JScrollPane seatScroll = new JScrollPane(seatMapPanel);
        seatScroll.setOpaque(false);
        seatScroll.getViewport().setOpaque(false);

        // Right Bottom: Checkout Form
        JPanel checkoutInfo = new JPanel(new GridBagLayout());
        checkoutInfo.setOpaque(false);
        checkoutInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        fNamaCustomer = new JTextField();
        fNamaCustomer.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nama Lengkap Penumpang");
        cbMetodePembayaran = new JComboBox<>(new String[] { "Tunai", "QRIS" });

        lblSelectedSeats = new JLabel("Kursi Terpilih: -");
        lblSelectedSeats.setFont(new Font("Inter", Font.BOLD, 13));
        lblSelectedSeats.setForeground(Color.WHITE);

        lblTotalPrice = new JLabel("Total Harga: Rp 0");
        lblTotalPrice.setFont(new Font("Inter", Font.BOLD, 18));
        lblTotalPrice.setForeground(new Color(76, 175, 80));

        JButton btnCheckout = new JButton("Bayar & Cetak Tiket");
        btnCheckout.setFont(new Font("Inter", Font.BOLD, 14));
        btnCheckout.setBackground(new Color(76, 175, 80));
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.putClientProperty(FlatClientProperties.STYLE, "arc: 12");
        btnCheckout.addActionListener(e -> processCheckout());

        // Row 1: Nama
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        checkoutInfo.add(new JLabel("Nama:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        checkoutInfo.add(fNamaCustomer, gbc);

        // Row 2: Metode
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        checkoutInfo.add(new JLabel("Metode:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        checkoutInfo.add(cbMetodePembayaran, gbc);

        // Row 3: Info & Total
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        checkoutInfo.add(lblSelectedSeats, gbc);
        gbc.gridy = 3;
        checkoutInfo.add(lblTotalPrice, gbc);

        // Row 4: Button
        gbc.gridy = 4;
        gbc.insets = new Insets(15, 5, 5, 5);
        checkoutInfo.add(btnCheckout, gbc);

        rightPanel.add(gerbongPanel, BorderLayout.NORTH);
        rightPanel.add(seatScroll, BorderLayout.CENTER);
        rightPanel.add(checkoutInfo, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        add(searchPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private void setupTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(new Font("Inter", Font.PLAIN, 13));
        table.setShowGrid(true);
        table.setGridColor(new Color(60, 60, 60));
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, center);
    }

    private void loadStasiun() {
        stasiunDAO.getAll().forEach(s -> {
            if (s.getNamaStasiun().equalsIgnoreCase("Stasiun Gubeng")) {
                idStasiunGubeng = s.getId();
            } else {
                cbTujuan.addItem(new ComboItem(s.getId(), s.getNamaStasiun()));
            }
        });

        if (idStasiunGubeng == -1) {
            JOptionPane.showMessageDialog(this,
                    "Peringatan: 'Stasiun Gubeng' tidak terdeteksi di database. Fitur mungkin terkendala.");
        }
    }

    private void searchJadwal() {
        modelJadwal.setRowCount(0);
        resetSelections();
        tblJadwal.clearSelection();
        cbGerbong.removeAllItems();

        Object t = cbTujuan.getSelectedItem();
        String tgl = txtTanggal.getText();

        if (idStasiunGubeng == -1 || t == null || tgl == null || tgl.isEmpty()) {
            return;
        }

        int tujuanId = ((ComboItem) t).id;
        // Search by route and exactly the selected departure date
        List<Jadwal> results = jadwalDAO.getByRouteAndDate(idStasiunGubeng, tujuanId, tgl);
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada jadwal untuk tanggal " + tgl + " ke tujuan tersebut.");
            return;
        }

        // Store ID inside table secretly or we can just fetch using row index if we
        // keep a List
        // For simplicity, we can fetch by namaKereta+jam or maintain the 'results' list
        // Let's add ID as column 0 and hide it, or just keep it in table
        modelJadwal.setColumnCount(4); // We add hidden col for ID
        ListSelectionModel lsm = tblJadwal.getSelectionModel();
        tblJadwal.getColumnModel().getColumn(3).setMinWidth(0);
        tblJadwal.getColumnModel().getColumn(3).setMaxWidth(0);
        tblJadwal.getColumnModel().getColumn(3).setWidth(0);

        results.forEach(j -> {
            // format time beautifully if needed, here just string
            String jam = j.getWaktuBerangkat().toString().substring(11, 16); // HH:mm
            modelJadwal.addRow(new Object[] {
                    j.getNamaKereta(), jam, j.getStatus(), j.getId()
            });
        });
    }

    private void selectJadwal() {
        int row = tblJadwal.getSelectedRow();
        if (row != -1) {
            int id = (int) tblJadwal.getModel().getValueAt(row, 3); // Get hidden ID
            selectedJadwal = jadwalDAO.getAll().stream().filter(j -> j.getId() == id).findFirst().orElse(null);

            // Get standard price
            List<JadwalHarga> prices = hargaDAO.getByJadwalId(id);
            if (!prices.isEmpty()) {
                ticketPrice = prices.get(0).getHargaTiket();
            } else {
                ticketPrice = 0;
            }

            resetSelections();

            // Load gerbongs
            cbGerbong.removeAllItems();
            List<Gerbong> gerbongs = gerbongDAO.getByKeretaId(selectedJadwal.getKeretaId());
            for (Gerbong g : gerbongs) {
                cbGerbong.addItem(new ComboItem(g.getId(), g.getNamaKelas() + " - " + g.getNomorGerbong()));
            }
        }
    }

    private void loadSeatMap(int jadwalId, int gerbongId) {
        seatMapPanel.removeAll();
        // Clear selected seats because we changed logical view,
        // but if kasir wants multi-gerbong, we shouldn't clear here.
        // For simplicity, we allow keeping selections across gerbong.

        List<JadwalKursi> list = kursiDAO.getByJadwalAndGerbong(jadwalId, gerbongId);

        for (JadwalKursi k : list) {
            JButton btnSeat = new JButton(k.getKodeKursi());
            btnSeat.setPreferredSize(new Dimension(50, 50));
            btnSeat.setFont(new Font("Inter", Font.BOLD, 11));
            btnSeat.putClientProperty(FlatClientProperties.STYLE, "arc: 8");

            if (k.getStatus().equalsIgnoreCase("dipesan") || k.getStatus().equalsIgnoreCase("TIDAK TERSEDIA")) {
                btnSeat.setBackground(new Color(80, 80, 80));
                btnSeat.setForeground(new Color(150, 150, 150));
                btnSeat.setEnabled(false);
            } else {
                // Check if already selected in our memory
                boolean isSelected = selectedSeats.stream().anyMatch(s -> s.getId() == k.getId());
                if (isSelected) {
                    btnSeat.setBackground(new Color(76, 175, 80));
                } else {
                    btnSeat.setBackground(new Color(50, 52, 55));
                }

                btnSeat.setForeground(Color.WHITE);
                btnSeat.addActionListener(e -> toggleSeatSelection(btnSeat, k));
            }
            seatMapPanel.add(btnSeat);
        }
        seatMapPanel.revalidate();
        seatMapPanel.repaint();
    }

    private void toggleSeatSelection(JButton btn, JadwalKursi k) {
        JadwalKursi matched = selectedSeats.stream().filter(s -> s.getId() == k.getId()).findFirst().orElse(null);
        if (matched != null) {
            selectedSeats.remove(matched);
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
            StringBuilder sb = new StringBuilder("<html>Kursi Terpilih: ");
            for (int i = 0; i < selectedSeats.size(); i++) {
                sb.append(selectedSeats.get(i).getKodeKursi()).append(i == selectedSeats.size() - 1 ? "" : ", ");
            }
            sb.append("</html>");
            lblSelectedSeats.setText(sb.toString());
            lblTotalPrice
                    .setText("Total Harga: Rp " + String.format("%, d", (long) selectedSeats.size() * ticketPrice));
        }
    }

    private void resetSelections() {
        selectedSeats.clear();
        updateCheckoutInfo();
        fNamaCustomer.setText("");
    }

    private void processCheckout() {
        if (selectedJadwal == null || selectedSeats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih jadwal, gerbong, dan minimal 1 kursi terlebih dahulu!");
            return;
        }

        if (ticketPrice == 0) {
            JOptionPane.showMessageDialog(this, "Peringatan: Harga tiket kereta ini belum diset (Rp 0).");
        }

        String nama = fNamaCustomer.getText().trim();
        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama penumpang wajib diisi!");
            fNamaCustomer.requestFocus();
            return;
        }

        Transaksi t = new Transaksi();
        t.setUserId(currentUser.getId());
        String timePart = String.valueOf(System.currentTimeMillis());
        t.setKodeTransaksi("TRX-" + timePart.substring(timePart.length() - 8));
        t.setKodeBooking("BKG-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        t.setNamaCustomer(nama);
        t.setTotalBayar((long) selectedSeats.size() * ticketPrice);
        t.setJumlahTiket(selectedSeats.size());
        t.setStatus("paid");
        t.setMetodePembayaran(cbMetodePembayaran.getSelectedItem().toString());

        List<DetailTransaksi> details = new ArrayList<>();
        for (JadwalKursi k : selectedSeats) {
            DetailTransaksi dt = new DetailTransaksi();
            dt.setJadwalKursiId(k.getId());
            dt.setHarga(ticketPrice);
            details.add(dt);
        }

        if (transaksiDAO.saveFullTransaction(t, details)) {
            showReceiptDialog(t);

            // Reload the seat map for the current gerbong
            if (cbGerbong.getSelectedItem() != null) {
                int gerbongId = ((ComboItem) cbGerbong.getSelectedItem()).id;
                loadSeatMap(selectedJadwal.getId(), gerbongId);
            }
            resetSelections();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal memproses transaksi! Silakan periksa log aplikasi.");
        }
    }

    private void showReceiptDialog(Transaksi t) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Struk Tiket", true);
        dialog.setSize(480, 550);
        dialog.setLocationRelativeTo(this);
        
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.setBackground(Color.WHITE);
        
        StringBuilder sb = new StringBuilder();
        sb.append("<html><div style='font-family: monospace; width: 320px;'>");
        sb.append("<h2 style='text-align:center; margin-bottom: 5px;'>REL EXPRESS</h2>");
        sb.append("<p style='text-align:center; margin-top: 0;'>BUKTI PEMBAYARAN</p>");
        sb.append("<hr>");
        sb.append("<b>No Transaksi :</b> ").append(t.getKodeTransaksi()).append("<br>");
        sb.append("<b>Kode Booking :</b> ").append(t.getKodeBooking()).append("<br>");
        sb.append("<b>Waktu        :</b> ").append(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("<br>");
        sb.append("<b>Kasir        :</b> ").append(currentUser.getUsername()).append("<br>");
        sb.append("<hr>");
        sb.append("<b>Kereta       :</b> ").append(selectedJadwal.getNamaKereta()).append("<br>");
        sb.append("<b>Rute         :</b> ").append(selectedJadwal.getNamaStasiunAsal()).append(" - ").append(selectedJadwal.getNamaStasiunTujuan()).append("<br>");
        sb.append("<b>Berangkat    :</b> ").append(selectedJadwal.getWaktuBerangkat().toString().substring(0, 16)).append("<br>");
        sb.append("<b>Penumpang    :</b> ").append(t.getNamaCustomer()).append("<br>");
        sb.append("<b>Daftar Kursi :</b><br>");
        for (JadwalKursi k : selectedSeats) {
            String gInfo = k.getNomorGerbong() != null ? k.getNomorGerbong() : "Gerbong";
            sb.append("- ").append(gInfo).append(" / ").append(k.getKodeKursi()).append("<br>");
        }
        sb.append("<hr>");
        sb.append("<b>Total Tiket  :</b> ").append(t.getJumlahTiket()).append(" Tiket<br>");
        sb.append("<b>Total Bayar  :</b> Rp ").append(String.format("%,d", t.getTotalBayar())).append("<br>");
        sb.append("<b>Metode Bayar :</b> ").append(t.getMetodePembayaran()).append("<br>");
        sb.append("<hr>");
        sb.append("<p style='text-align:center;'>Terima kasih dan selamat menikmati<br>perjalanan Anda!</p>");
        sb.append("</div></html>");
        
        JLabel lblReceipt = new JLabel(sb.toString());
        lblReceipt.setForeground(Color.BLACK);
        
        JScrollPane scroll = new JScrollPane(lblReceipt);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        JButton btnClose = new JButton("Tutup & Selesai");
        btnClose.setFont(new Font("Inter", Font.BOLD, 14));
        btnClose.setBackground(new Color(76, 175, 80));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "arc: 12");
        btnClose.addActionListener(e -> dialog.dispose());
        
        p.add(scroll, BorderLayout.CENTER);
        p.add(btnClose, BorderLayout.SOUTH);
        
        dialog.add(p);
        dialog.setVisible(true);
    }

    private static class ComboItem {
        int id;
        String name;

        ComboItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }
}