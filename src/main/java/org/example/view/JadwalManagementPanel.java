package org.example.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.example.dao.*;
import org.example.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;

public class JadwalManagementPanel extends JPanel {
    private final JadwalDAO jadwalDAO;
    private final JadwalHargaDAO hargaDAO;
    private final JadwalKursiDAO kursiDAO;
    private final KeretaDAO keretaDAO;
    private final StasiunDAO stasiunDAO;
    private final KelasKeretaDAO kelasDAO;

    private JTable tblJadwal;
    private DefaultTableModel modelJadwal;
    private JTextField txtSearchJadwal;

    private JTable tblHarga;
    private DefaultTableModel modelHarga;
    private JLabel lblHargaTitle;

    private JTable tblKursi;
    private DefaultTableModel modelKursi;
    private JLabel lblKursiTitle;

    public JadwalManagementPanel() {
        this.jadwalDAO = new JadwalDAO();
        this.hargaDAO = new JadwalHargaDAO();
        this.kursiDAO = new JadwalKursiDAO();
        this.keretaDAO = new KeretaDAO();
        this.stasiunDAO = new StasiunDAO();
        this.kelasDAO = new KelasKeretaDAO();

        initComponents();
        loadJadwal();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty(FlatClientProperties.STYLE, "tabType:card; arc: 20");

        tabbedPane.addTab("Kelola Jadwal", createJadwalPanel());
        tabbedPane.addTab("Jadwal Harga", createHargaPanel());
        tabbedPane.addTab("Jadwal Kursi", createKursiPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // --- JADWAL TAB ---
    private JPanel createJadwalPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel title = new JLabel("Kelola Jadwal Keberangkatan");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        txtSearchJadwal = new JTextField();
        txtSearchJadwal.setPreferredSize(new Dimension(200, 35));
        txtSearchJadwal.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Cari...");
        txtSearchJadwal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                searchJadwal();
            }
        });

        JButton btnAdd = new JButton("Tambah Jadwal");
        btnAdd.setBackground(new Color(51, 144, 255));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> showJadwalForm(null));

        actions.add(txtSearchJadwal);
        actions.add(btnAdd);
        header.add(title, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        // Table
        String[] cols = { "ID", "Kereta", "Asal", "Tujuan", "Berangkat", "Tiba", "Status" };
        modelJadwal = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblJadwal = new JTable(modelJadwal);
        setupTable(tblJadwal);
        tblJadwal.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadHarga();
                loadKursi();
            }
        });

        // Bottom Actions
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setOpaque(false);

        JButton btnEdit = new JButton("Edit");
        btnEdit.setBackground(new Color(76, 175, 80));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.addActionListener(e -> {
            int row = tblJadwal.getSelectedRow();
            if (row != -1) {
                int id = (int) tblJadwal.getValueAt(row, 0);
                Jadwal j = jadwalDAO.getAll().stream().filter(x -> x.getId() == id).findFirst().orElse(null);
                showJadwalForm(j);
            }
        });

        JButton btnDel = new JButton("Hapus");
        btnDel.setBackground(new Color(244, 67, 54));
        btnDel.setForeground(Color.WHITE);
        btnDel.addActionListener(e -> deleteJadwal());

        bottom.add(btnEdit);
        bottom.add(btnDel);

        panel.add(header, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblJadwal), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    // --- HARGA TAB ---
    private JPanel createHargaPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lblHargaTitle = new JLabel("Pilih Jadwal untuk mengelola harga");
        lblHargaTitle.setFont(new Font("Inter", Font.BOLD, 18));
        lblHargaTitle.setForeground(Color.WHITE);
        lblHargaTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Table
        String[] cols = { "ID", "Kelas", "Harga Tiket", "Update Terakhir" };
        modelHarga = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblHarga = new JTable(modelHarga);
        setupTable(tblHarga);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setOpaque(false);

        JButton btnAdd = new JButton("Atur Harga");
        btnAdd.setBackground(new Color(51, 144, 255));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> showHargaForm());

        JButton btnDel = new JButton("Hapus Harga");
        btnDel.setBackground(new Color(244, 67, 54));
        btnDel.setForeground(Color.WHITE);
        btnDel.addActionListener(e -> deleteHarga());

        bottom.add(btnAdd);
        bottom.add(btnDel);

        panel.add(lblHargaTitle, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblHarga), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    // --- KURSI TAB ---
    private JPanel createKursiPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lblKursiTitle = new JLabel("Pilih Jadwal untuk melihat status kursi");
        lblKursiTitle.setFont(new Font("Inter", Font.BOLD, 18));
        lblKursiTitle.setForeground(Color.WHITE);
        lblKursiTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Table
        String[] cols = { "ID", "Baris", "Kode Kursi", "Status" };
        modelKursi = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblKursi = new JTable(modelKursi);
        setupTable(tblKursi);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setOpaque(false);

        JButton btnUpdateStatus = new JButton("Ubah Status (Tersedia/Dipesan)");
        btnUpdateStatus.setBackground(new Color(76, 175, 80));
        btnUpdateStatus.setForeground(Color.WHITE);
        btnUpdateStatus.addActionListener(e -> updateKursiStatus());

        bottom.add(btnUpdateStatus);

        panel.add(lblKursiTitle, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblKursi), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private void setupTable(JTable table) {
        table.setRowHeight(40);
        table.setFont(new Font("Inter", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 14));
        table.setShowGrid(true);
        table.setGridColor(new Color(60, 60, 60));
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, renderer);
    }

    // --- LOGIC JADWAL ---
    private void loadJadwal() {
        modelJadwal.setRowCount(0);
        jadwalDAO.getAll().forEach(j -> modelJadwal.addRow(new Object[] {
                j.getId(), j.getNamaKereta(), j.getNamaStasiunAsal(), j.getNamaStasiunTujuan(),
                j.getWaktuBerangkat(), j.getWaktuTiba(), j.getStatus()
        }));
    }

    private void searchJadwal() {
        modelJadwal.setRowCount(0);
        jadwalDAO.search(txtSearchJadwal.getText()).forEach(j -> modelJadwal.addRow(new Object[] {
                j.getId(), j.getNamaKereta(), j.getNamaStasiunAsal(), j.getNamaStasiunTujuan(),
                j.getWaktuBerangkat(), j.getWaktuTiba(), j.getStatus()
        }));
    }

    private void deleteJadwal() {
        int row = tblJadwal.getSelectedRow();
        if (row != -1) {
            int id = (int) tblJadwal.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this,
                    "Hapus jadwal ini? Semua harga dan status kursi terkait akan hilang.", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                // Cascading delete is handled in DB or manually here
                hargaDAO.getByJadwalId(id).forEach(h -> hargaDAO.delete(h.getId()));
                kursiDAO.deleteByJadwalId(id);
                if (jadwalDAO.delete(id)) {
                    loadJadwal();
                    JOptionPane.showMessageDialog(this, "Berhasil dihapus");
                }
            }
        }
    }

    private void showJadwalForm(Jadwal j) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                j == null ? "Tambah Jadwal" : "Edit Jadwal", true);
        dialog.setSize(450, 550);
        dialog.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridLayout(0, 1, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JComboBox<ComboItem> cbKereta = new JComboBox<>();
        keretaDAO.getAll().forEach(k -> cbKereta.addItem(new ComboItem(k.getId(), k.getNamaKereta())));

        int gubengId = -1;
        String asalName = "Stasiun Gubeng";
        java.util.List<Stasiun> allStasiun = stasiunDAO.getAll();
        for (Stasiun s : allStasiun) {
            if (s.getNamaStasiun().equalsIgnoreCase("Stasiun Gubeng")) {
                gubengId = s.getId();
            }
            if (j != null && s.getId() == j.getStasiunAsalId()) {
                asalName = s.getNamaStasiun();
            }
        }
        final int finalAsalId = (j != null) ? j.getStasiunAsalId() : gubengId;
        
        JTextField textAsal = new JTextField(asalName);
        textAsal.setEnabled(false);
        textAsal.setDisabledTextColor(Color.GRAY);

        JComboBox<ComboItem> cbTujuan = new JComboBox<>();
        allStasiun.forEach(s -> cbTujuan.addItem(new ComboItem(s.getId(), s.getNamaStasiun())));

        if (j != null) {
            for (int i = 0; i < cbKereta.getItemCount(); i++) {
                if (cbKereta.getItemAt(i).id == j.getKeretaId()) {
                    cbKereta.setSelectedIndex(i);
                    break;
                }
            }
            for (int i = 0; i < cbTujuan.getItemCount(); i++) {
                if (cbTujuan.getItemAt(i).id == j.getStasiunTujuanId()) {
                    cbTujuan.setSelectedIndex(i);
                    break;
                }
            }
        }

        JTextField fBerangkat = new JTextField(j != null ? j.getWaktuBerangkat().toString() : "2024-01-01 08:00:00");
        JTextField fTiba = new JTextField(j != null ? j.getWaktuTiba().toString() : "2024-01-01 10:00:00");

        JComboBox<String> cbStatus = new JComboBox<>(new String[] { "Aktif", "Dibatalkan", "Selesai" });
        if (j != null)
            cbStatus.setSelectedItem(j.getStatus());

        p.add(new JLabel("Kereta:"));
        p.add(cbKereta);
        p.add(new JLabel("Stasiun Asal:"));
        p.add(textAsal);
        p.add(new JLabel("Stasiun Tujuan:"));
        p.add(cbTujuan);
        p.add(new JLabel("Waktu Berangkat (YYYY-MM-DD HH:MM:SS):"));
        p.add(fBerangkat);
        p.add(new JLabel("Waktu Tiba (YYYY-MM-DD HH:MM:SS):"));
        p.add(fTiba);
        p.add(new JLabel("Status:"));
        p.add(cbStatus);

        JButton btn = new JButton("Simpan");
        btn.addActionListener(e -> {
            try {
                Jadwal jadwal = (j == null) ? new Jadwal() : j;
                jadwal.setKeretaId(((ComboItem) cbKereta.getSelectedItem()).id);
                jadwal.setStasiunAsalId(finalAsalId);
                jadwal.setStasiunTujuanId(((ComboItem) cbTujuan.getSelectedItem()).id);
                jadwal.setWaktuBerangkat(Timestamp.valueOf(fBerangkat.getText()));
                jadwal.setWaktuTiba(Timestamp.valueOf(fTiba.getText()));
                jadwal.setStatus(cbStatus.getSelectedItem().toString());

                if (j == null) {
                    int id = jadwalDAO.insert(jadwal);
                    if (id != -1) {
                        kursiDAO.initializeSeats(id, jadwal.getKeretaId());
                        loadJadwal();
                        dialog.dispose();
                    }
                } else {
                    if (jadwalDAO.update(jadwal)) {
                        loadJadwal();
                        dialog.dispose();
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Format waktu salah! Gunakan: YYYY-MM-DD HH:MM:SS");
            }
        });

        dialog.add(p, BorderLayout.CENTER);
        dialog.add(btn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // --- LOGIC HARGA ---
    private void loadHarga() {
        int row = tblJadwal.getSelectedRow();
        if (row != -1) {
            int jId = (int) tblJadwal.getValueAt(row, 0);
            String kereta = (String) tblJadwal.getValueAt(row, 1);
            lblHargaTitle.setText("Harga Tiket Jadwal ID: " + jId + " (" + kereta + ")");
            modelHarga.setRowCount(0);
            hargaDAO.getByJadwalId(jId).forEach(h -> modelHarga.addRow(new Object[] {
                    h.getId(), h.getNamaKelas(), h.getHargaTiket(), h.getUpdatedAt()
            }));
        } else {
            lblHargaTitle.setText("Pilih Jadwal untuk mengelola harga");
            modelHarga.setRowCount(0);
        }
    }

    private void deleteHarga() {
        int row = tblHarga.getSelectedRow();
        if (row != -1) {
            int id = (int) tblHarga.getValueAt(row, 0);
            if (hargaDAO.delete(id)) {
                loadHarga();
            }
        }
    }

    private void showHargaForm() {
        int rowJ = tblJadwal.getSelectedRow();
        if (rowJ == -1)
            return;
        int jId = (int) tblJadwal.getValueAt(rowJ, 0);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Atur Harga", true);
        dialog.setSize(350, 300);
        dialog.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridLayout(0, 1, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JComboBox<ComboItem> cbKelas = new JComboBox<>();
        kelasDAO.getAll().forEach(k -> cbKelas.addItem(new ComboItem(k.getId(), k.getNamaKelasKereta())));

        JTextField fHarga = new JTextField();
        p.add(new JLabel("Pilih Kelas:"));
        p.add(cbKelas);
        p.add(new JLabel("Harga Tiket:"));
        p.add(fHarga);

        JButton btn = new JButton("Simpan");
        btn.addActionListener(e -> {
            try {
                JadwalHarga jh = new JadwalHarga();
                jh.setJadwalId(jId);
                jh.setKelasId(((ComboItem) cbKelas.getSelectedItem()).id);
                jh.setHargaTiket(Integer.parseInt(fHarga.getText()));
                if (hargaDAO.insert(jh)) {
                    loadHarga();
                    dialog.dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Harga harus berupa angka!");
            }
        });

        dialog.add(p, BorderLayout.CENTER);
        dialog.add(btn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // --- LOGIC KURSI ---
    private void loadKursi() {
        int row = tblJadwal.getSelectedRow();
        if (row != -1) {
            int jId = (int) tblJadwal.getValueAt(row, 0);
            lblKursiTitle.setText("Status Kursi Jadwal ID: " + jId);
            modelKursi.setRowCount(0);
            kursiDAO.getByJadwalId(jId).forEach(k -> modelKursi.addRow(new Object[] {
                    k.getId(), k.getBarisKursi(), k.getKodeKursi(), k.getStatus()
            }));
        } else {
            lblKursiTitle.setText("Pilih Jadwal untuk melihat status kursi");
            modelKursi.setRowCount(0);
        }
    }

    private void updateKursiStatus() {
        int row = tblKursi.getSelectedRow();
        if (row != -1) {
            int id = (int) tblKursi.getValueAt(row, 0);
            String currentStatus = (String) tblKursi.getValueAt(row, 3);
            String newStatus = currentStatus.equalsIgnoreCase("tersedia") ? "dipesan" : "tersedia";
            if (kursiDAO.updateStatus(id, newStatus)) {
                loadKursi();
            }
        }
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
