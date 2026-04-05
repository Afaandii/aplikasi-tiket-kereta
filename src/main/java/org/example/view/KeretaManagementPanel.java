package org.example.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.example.dao.KeretaDAO;
import org.example.dao.KelasKeretaDAO;
import org.example.model.Kereta;
import org.example.model.KelasKereta;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class KeretaManagementPanel extends JPanel {
    private final KeretaDAO keretaDAO;
    private final KelasKeretaDAO kelasKeretaDAO;
    
    private JTable keretaTable;
    private DefaultTableModel keretaTableModel;
    private JTextField txtSearchKereta;
    
    private JTable kelasTable;
    private DefaultTableModel kelasTableModel;
    private JTextField txtSearchKelas;

    public KeretaManagementPanel() {
        this.keretaDAO = new KeretaDAO();
        this.kelasKeretaDAO = new KelasKeretaDAO();
        initComponents();
        loadKeretaData();
        loadKelasData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty(FlatClientProperties.STYLE, "tabType:card; arc: 20");
        
        tabbedPane.addTab("Daftar Kereta", createKeretaPanel());
        tabbedPane.addTab("Kategori Kelas", createKelasKeretaPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // --- KERETA TAB ---
    private JPanel createKeretaPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel title = new JLabel("Kelola Data Kereta");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        txtSearchKereta = new JTextField();
        txtSearchKereta.setPreferredSize(new Dimension(200, 35));
        txtSearchKereta.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Cari...");
        txtSearchKereta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { searchKereta(); }
        });

        JButton btnAdd = new JButton("Tambah Kereta");
        btnAdd.setBackground(new Color(51, 144, 255));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> showKeretaForm(null));

        actions.add(txtSearchKereta);
        actions.add(btnAdd);
        header.add(title, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        // Table
        String[] cols = {"ID", "Kode Kereta", "Nama Kereta", "Tanggal Dibuat", "Tanggal Diupdate"};
        keretaTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        keretaTable = new JTable(keretaTableModel);
        setupTable(keretaTable);

        // Bottom Actions
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setOpaque(false);

        JButton btnEdit = new JButton("Edit");
        btnEdit.setBackground(new Color(76, 175, 80));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.addActionListener(e -> {
            int row = keretaTable.getSelectedRow();
            if (row != -1) {
                int id = (int) keretaTable.getValueAt(row, 0);
                Kereta k = keretaDAO.getAll().stream().filter(x -> x.getId() == id).findFirst().orElse(null);
                showKeretaForm(k);
            }
        });

        JButton btnDel = new JButton("Hapus");
        btnDel.setBackground(new Color(244, 67, 54));
        btnDel.setForeground(Color.WHITE);
        btnDel.addActionListener(e -> deleteKereta());

        bottom.add(btnEdit);
        bottom.add(btnDel);

        panel.add(header, BorderLayout.NORTH);
        panel.add(new JScrollPane(keretaTable), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    // --- KELAS TAB ---
    private JPanel createKelasKeretaPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel title = new JLabel("Kelola Kategori Kelas");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        txtSearchKelas = new JTextField();
        txtSearchKelas.setPreferredSize(new Dimension(200, 35));
        txtSearchKelas.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Cari...");
        txtSearchKelas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { searchKelas(); }
        });

        JButton btnAdd = new JButton("Tambah Kelas");
        btnAdd.setBackground(new Color(51, 144, 255));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> showKelasForm(null));

        actions.add(txtSearchKelas);
        actions.add(btnAdd);
        header.add(title, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        // Table
        String[] cols = {"ID", "Nama Kelas", "Tanggal Dibuat", "Tanggal Diupdate"};
        kelasTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        kelasTable = new JTable(kelasTableModel);
        setupTable(kelasTable);

        // Bottom Actions
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setOpaque(false);

        JButton btnEdit = new JButton("Edit");
        btnEdit.setBackground(new Color(76, 175, 80));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.addActionListener(e -> {
            int row = kelasTable.getSelectedRow();
            if (row != -1) {
                int id = (int) kelasTable.getValueAt(row, 0);
                KelasKereta k = kelasKeretaDAO.getAll().stream().filter(x -> x.getId() == id).findFirst().orElse(null);
                showKelasForm(k);
            }
        });

        JButton btnDel = new JButton("Hapus");
        btnDel.setBackground(new Color(244, 67, 54));
        btnDel.setForeground(Color.WHITE);
        btnDel.addActionListener(e -> deleteKelas());

        bottom.add(btnEdit);
        bottom.add(btnDel);

        panel.add(header, BorderLayout.NORTH);
        panel.add(new JScrollPane(kelasTable), BorderLayout.CENTER);
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

    // --- LOGIC KERETA ---
    private void loadKeretaData() {
        keretaTableModel.setRowCount(0);
        keretaDAO.getAll().forEach(k -> keretaTableModel.addRow(new Object[]{
            k.getId(), k.getKodeKereta(), k.getNamaKereta(), k.getCreatedAt(), k.getUpdatedAt()
        }));
    }

    private void searchKereta() {
        keretaTableModel.setRowCount(0);
        keretaDAO.search(txtSearchKereta.getText()).forEach(k -> keretaTableModel.addRow(new Object[]{
            k.getId(), k.getKodeKereta(), k.getNamaKereta(), k.getCreatedAt(), k.getUpdatedAt()
        }));
    }

    private void deleteKereta() {
        int row = keretaTable.getSelectedRow();
        if (row != -1) {
            int id = (int) keretaTable.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this, "Hapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (keretaDAO.delete(id)) {
                    loadKeretaData();
                    JOptionPane.showMessageDialog(this, "Berhasil dihapus");
                }
            }
        }
    }

    private void showKeretaForm(Kereta k) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), k == null ? "Tambah" : "Edit", true);
        dialog.setSize(350, 300);
        dialog.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridLayout(0, 1, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JTextField fKode = new JTextField(k != null ? k.getKodeKereta() : "");
        JTextField fNama = new JTextField(k != null ? k.getNamaKereta() : "");

        p.add(new JLabel("Kode Kereta:")); p.add(fKode);
        p.add(new JLabel("Nama Kereta:")); p.add(fNama);

        JButton btn = new JButton("Simpan");
        btn.addActionListener(e -> {
            Kereta ker = (k == null) ? new Kereta() : k;
            ker.setKodeKereta(fKode.getText());
            ker.setNamaKereta(fNama.getText());
            if (k == null ? keretaDAO.insert(ker) : keretaDAO.update(ker)) {
                loadKeretaData();
                dialog.dispose();
            }
        });

        dialog.add(p, BorderLayout.CENTER);
        dialog.add(btn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // --- LOGIC KELAS ---
    private void loadKelasData() {
        kelasTableModel.setRowCount(0);
        kelasKeretaDAO.getAll().forEach(k -> kelasTableModel.addRow(new Object[]{
            k.getId(), k.getNamaKelasKereta(), k.getCreatedAt(), k.getUpdatedAt()
        }));
    }

    private void searchKelas() {
        kelasTableModel.setRowCount(0);
        kelasKeretaDAO.search(txtSearchKelas.getText()).forEach(k -> kelasTableModel.addRow(new Object[]{
            k.getId(), k.getNamaKelasKereta(), k.getCreatedAt(), k.getUpdatedAt()
        }));
    }

    private void deleteKelas() {
        int row = kelasTable.getSelectedRow();
        if (row != -1) {
            int id = (int) kelasTable.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this, "Hapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (kelasKeretaDAO.delete(id)) {
                    loadKelasData();
                    JOptionPane.showMessageDialog(this, "Berhasil dihapus");
                }
            }
        }
    }

    private void showKelasForm(KelasKereta k) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), k == null ? "Tambah" : "Edit", true);
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridLayout(0, 1, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JTextField fNama = new JTextField(k != null ? k.getNamaKelasKereta() : "");

        p.add(new JLabel("Nama Kelas:")); p.add(fNama);

        JButton btn = new JButton("Simpan");
        btn.addActionListener(e -> {
            KelasKereta kel = (k == null) ? new KelasKereta() : k;
            kel.setNamaKelasKereta(fNama.getText());
            if (k == null ? kelasKeretaDAO.insert(kel) : kelasKeretaDAO.update(kel)) {
                loadKelasData();
                dialog.dispose();
            }
        });

        dialog.add(p, BorderLayout.CENTER);
        dialog.add(btn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
