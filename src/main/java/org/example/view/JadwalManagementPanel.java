package org.example.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.example.dao.JadwalDAO;
import org.example.dao.KeretaDAO;
import org.example.dao.StasiunDAO;
import org.example.model.Jadwal;
import org.example.model.Kereta;
import org.example.model.Stasiun;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class JadwalManagementPanel extends JPanel {
    private final JadwalDAO jadwalDAO;
    private final KeretaDAO keretaDAO;
    private final StasiunDAO stasiunDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public JadwalManagementPanel() {
        this.jadwalDAO = new JadwalDAO();
        this.keretaDAO = new KeretaDAO();
        this.stasiunDAO = new StasiunDAO();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header Section
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel lblTitle = new JLabel("Kelola Jadwal Keberangkatan");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        // Search & Add Button
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(280, 40));
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Cari Kereta atau Stasiun...");
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                searchData();
            }
        });

        JButton btnAdd = new JButton("Tambah Jadwal");
        btnAdd.setFont(new Font("Inter", Font.BOLD, 14));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setBackground(new Color(51, 144, 255));
        btnAdd.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        btnAdd.setPreferredSize(new Dimension(160, 40));
        btnAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> showForm(null));

        actionPanel.add(txtSearch);
        actionPanel.add(btnAdd);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(actionPanel, BorderLayout.EAST);

        // Table Section
        String[] columns = {"ID", "Kereta", "Asal", "Tujuan", "Harga", "Berangkat", "Tiba", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(45);
        table.setFont(new Font("Inter", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 14));
        table.getTableHeader().setReorderingAllowed(false);
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JLabel) {
                    ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.putClientProperty(FlatClientProperties.STYLE, "arc: 20; border: 0,0,0,0");

        // Action Buttons (Bottom)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setOpaque(false);

        JButton btnEdit = new JButton("Edit Jadwal");
        btnEdit.setBackground(new Color(76, 175, 80));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setPreferredSize(new Dimension(130, 40));
        btnEdit.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) table.getValueAt(row, 0);
                // Need to find original Jadwal from DB to get IDs
                List<Jadwal> all = jadwalDAO.getAll();
                Jadwal selected = all.stream().filter(j -> j.getId() == id).findFirst().orElse(null);
                showForm(selected);
            } else {
                JOptionPane.showMessageDialog(this, "Pilih jadwal yang ingin diubah!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton btnDelete = new JButton("Hapus");
        btnDelete.setBackground(new Color(244, 67, 54));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setPreferredSize(new Dimension(100, 40));
        btnDelete.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        btnDelete.addActionListener(e -> deleteData());

        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDelete);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Jadwal> list = jadwalDAO.getAll();
        for (Jadwal j : list) {
            tableModel.addRow(new Object[]{
                j.getId(),
                j.getNamaKereta(),
                j.getNamaStasiunAwal(),
                j.getNamaStasiunTujuan(),
                "Rp " + String.format("%,d", j.getHargaTiket()),
                dateFormat.format(j.getWaktuBerangkat()),
                dateFormat.format(j.getWaktuTiba()),
                j.getStatus()
            });
        }
    }

    private void searchData() {
        String keyword = txtSearch.getText();
        tableModel.setRowCount(0);
        List<Jadwal> list = jadwalDAO.search(keyword);
        for (Jadwal j : list) {
            tableModel.addRow(new Object[]{
                j.getId(),
                j.getNamaKereta(),
                j.getNamaStasiunAwal(),
                j.getNamaStasiunTujuan(),
                "Rp " + String.format("%,d", j.getHargaTiket()),
                dateFormat.format(j.getWaktuBerangkat()),
                dateFormat.format(j.getWaktuTiba()),
                j.getStatus()
            });
        }
    }

    private void deleteData() {
        int row = table.getSelectedRow();
        if (row != -1) {
            int id = (int) table.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus jadwal ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (jadwalDAO.delete(id)) {
                    loadData();
                    JOptionPane.showMessageDialog(this, "Jadwal berhasil dihapus!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showForm(Jadwal jadwal) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            jadwal == null ? "Tambah Jadwal" : "Edit Jadwal", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Fetch Dynamic Data for Dropdowns
        List<Kereta> keretas = keretaDAO.getAll();
        List<Stasiun> stasiuns = stasiunDAO.getAll();

        JComboBox<Item> cbKereta = new JComboBox<>();
        keretas.forEach(k -> cbKereta.addItem(new Item(k.getId(), k.getNamaKereta())));

        JComboBox<Item> cbAsal = new JComboBox<>();
        JComboBox<Item> cbTujuan = new JComboBox<>();
        stasiuns.forEach(s -> {
            cbAsal.addItem(new Item(s.getId(), s.getNamaStasiun()));
            cbTujuan.addItem(new Item(s.getId(), s.getNamaStasiun()));
        });

        JTextField fHarga = new JTextField();
        fHarga.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nominal Rupiah");

        // Date/Time Spinners
        JSpinner sBerangkat = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorBerangkat = new JSpinner.DateEditor(sBerangkat, "yyyy-MM-dd HH:mm");
        sBerangkat.setEditor(editorBerangkat);
        sBerangkat.putClientProperty(FlatClientProperties.STYLE, "arc: 15");

        JSpinner sTiba = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorTiba = new JSpinner.DateEditor(sTiba, "yyyy-MM-dd HH:mm");
        sTiba.setEditor(editorTiba);
        sTiba.putClientProperty(FlatClientProperties.STYLE, "arc: 15");

        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Tersedia", "Penuh", "Berangkat", "Selesai", "Dibatalkan"});

        if (jadwal != null) {
            setSelectedId(cbKereta, jadwal.getKeretaId());
            setSelectedId(cbAsal, jadwal.getStasiunAwalId());
            setSelectedId(cbTujuan, jadwal.getStasiunTujuanId());
            fHarga.setText(String.valueOf(jadwal.getHargaTiket()));
            sBerangkat.setValue(new java.util.Date(jadwal.getWaktuBerangkat().getTime()));
            sTiba.setValue(new java.util.Date(jadwal.getWaktuTiba().getTime()));
            cbStatus.setSelectedItem(jadwal.getStatus());
        }

        panel.add(new JLabel("Pilih Kereta:"));
        panel.add(cbKereta);
        panel.add(new JLabel("Stasiun Asal:"));
        panel.add(cbAsal);
        panel.add(new JLabel("Stasiun Tujuan:"));
        panel.add(cbTujuan);
        panel.add(new JLabel("Harga Tiket:"));
        panel.add(fHarga);
        panel.add(new JLabel("Waktu Berangkat:"));
        panel.add(sBerangkat);
        panel.add(new JLabel("Waktu Tiba:"));
        panel.add(sTiba);
        panel.add(new JLabel("Status:"));
        panel.add(cbStatus);

        JButton btnSave = new JButton("Simpan Jadwal");
        btnSave.setFont(new Font("Inter", Font.BOLD, 14));
        btnSave.setBackground(new Color(51, 144, 255));
        btnSave.setForeground(Color.WHITE);
        btnSave.setPreferredSize(new Dimension(0, 50));
        btnSave.addActionListener(e -> {
            try {
                if (fHarga.getText().isEmpty()) {
                    throw new Exception("Lengkapi semua field!");
                }

                Jadwal j = (jadwal == null) ? new Jadwal() : jadwal;
                j.setKeretaId(((Item)cbKereta.getSelectedItem()).id);
                j.setStasiunAwalId(((Item)cbAsal.getSelectedItem()).id);
                j.setStasiunTujuanId(((Item)cbTujuan.getSelectedItem()).id);
                j.setHargaTiket(Integer.parseInt(fHarga.getText()));
                
                // Get Date from Spinner
                java.util.Date dBerangkat = (java.util.Date) sBerangkat.getValue();
                java.util.Date dTiba = (java.util.Date) sTiba.getValue();
                
                j.setWaktuBerangkat(new Timestamp(dBerangkat.getTime()));
                j.setWaktuTiba(new Timestamp(dTiba.getTime()));
                j.setStatus(cbStatus.getSelectedItem().toString());

                boolean success = (jadwal == null) ? jadwalDAO.insert(j) : jadwalDAO.update(j);
                if (success) {
                    loadData();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Jadwal Berhasil Disimpan!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Input Salah", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnSave, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Helper for ComboBox IDs
    private static class Item {
        int id;
        String name;
        Item(int id, String name) { this.id = id; this.name = name; }
        @Override
        public String toString() { return name; }
    }

    private void setSelectedId(JComboBox<Item> cb, int id) {
        for (int i = 0; i < cb.getItemCount(); i++) {
            if (cb.getItemAt(i).id == id) {
                cb.setSelectedIndex(i);
                break;
            }
        }
    }
}
