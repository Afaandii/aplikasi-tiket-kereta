package org.example.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.example.dao.StasiunDAO;
import org.example.model.Stasiun;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StasiunManagementPanel extends JPanel {
    private final StasiunDAO stasiunDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    public StasiunManagementPanel() {
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

        JLabel lblTitle = new JLabel("Kelola Data Stasiun Tujuan");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        // Search & Add Button
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(250, 40));
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Cari Kode, Nama, atau Kota...");
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                searchData();
            }
        });

        JButton btnAdd = new JButton("Tambah Stasiun");
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
        String[] columns = { "No", "Kode Stasiun", "Nama Stasiun", "Kota", "Terakhir Diupdate", "ID" };
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
        table.getColumnModel().getColumn(5).setMinWidth(0);
        table.getColumnModel().getColumn(5).setMaxWidth(0);
        table.getColumnModel().getColumn(5).setWidth(0);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
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

        JButton btnEdit = new JButton("Edit Terpilih");
        btnEdit.setBackground(new Color(76, 175, 80));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setPreferredSize(new Dimension(130, 40));
        btnEdit.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) table.getModel().getValueAt(row, 5);
                String kode = (String) table.getModel().getValueAt(row, 1);
                String nama = (String) table.getValueAt(row, 2);
                String kota = (String) table.getValueAt(row, 3);
                Stasiun s = new Stasiun(id, kode, nama, kota);
                showForm(s);
            } else {
                JOptionPane.showMessageDialog(this, "Pilih data yang ingin diubah!", "Peringatan",
                        JOptionPane.WARNING_MESSAGE);
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
        List<Stasiun> list = stasiunDAO.getAll();
        int no = 1;
        for (Stasiun s : list) {
            if (s.getNamaStasiun().equalsIgnoreCase("Stasiun Gubeng"))
                continue;
            tableModel.addRow(new Object[] {
                    no++,
                    s.getKodeStasiun(),
                    s.getNamaStasiun(),
                    s.getKota(),
                    s.getCreatedAt(),
                    s.getId()
            });
        }
    }

    private void searchData() {
        String keyword = txtSearch.getText();
        tableModel.setRowCount(0);
        List<Stasiun> list = stasiunDAO.search(keyword);
        int no = 1;
        for (Stasiun s : list) {
            if (s.getNamaStasiun().equalsIgnoreCase("Stasiun Gubeng"))
                continue;
            tableModel.addRow(new Object[] {
                    no++,
                    s.getKodeStasiun(),
                    s.getNamaStasiun(),
                    s.getKota(),
                    s.getCreatedAt(),
                    s.getId()
            });
        }
    }

    private void deleteData() {
        int row = table.getSelectedRow();
        if (row != -1) {
            int id = (int) table.getModel().getValueAt(row, 5);
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus stasiun ini?", "Konfirmasi Hapus",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (stasiunDAO.delete(id)) {
                    loadData();
                    JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showForm(Stasiun stasiun) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                stasiun == null ? "Tambah Stasiun" : "Edit Stasiun", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JTextField fKode = new JTextField();
        fKode.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Contoh: GMR, BD, SBY");
        JTextField fNama = new JTextField();
        fNama.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nama Stasiun");
        JTextField fKota = new JTextField();
        fKota.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nama Kota");

        if (stasiun != null) {
            fKode.setText(stasiun.getKodeStasiun());
            fNama.setText(stasiun.getNamaStasiun());
            fKota.setText(stasiun.getKota());
        }

        panel.add(new JLabel("Kode Stasiun:"));
        panel.add(fKode);
        panel.add(new JLabel("Nama Stasiun:"));
        panel.add(fNama);
        panel.add(new JLabel("Kota:"));
        panel.add(fKota);

        JButton btnSave = new JButton("Simpan");
        btnSave.setBackground(new Color(51, 144, 255));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> {
            if (fKode.getText().isEmpty() || fNama.getText().isEmpty() || fKota.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Semua Field harus diisi!");
                return;
            }

            Stasiun s = new Stasiun();
            if (stasiun != null)
                s.setId(stasiun.getId());
            s.setKodeStasiun(fKode.getText());
            s.setNamaStasiun(fNama.getText());
            s.setKota(fKota.getText());

            boolean success = (stasiun == null) ? stasiunDAO.insert(s) : stasiunDAO.update(s);
            if (success) {
                loadData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");
            } else {
                JOptionPane.showMessageDialog(dialog, "Gagal menyimpan data!");
            }
        });

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnSave, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
