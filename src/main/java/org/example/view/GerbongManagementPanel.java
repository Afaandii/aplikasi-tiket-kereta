package org.example.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.example.dao.GerbongDAO;
import org.example.dao.KelasKeretaDAO;
import org.example.dao.KeretaDAO;
import org.example.dao.KursiDAO;
import org.example.model.Gerbong;
import org.example.model.KelasKereta;
import org.example.model.Kereta;
import org.example.model.Kursi;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GerbongManagementPanel extends JPanel {
    private final GerbongDAO gerbongDAO;
    private final KursiDAO kursiDAO;
    private final KeretaDAO keretaDAO;
    private final KelasKeretaDAO kelasDAO;

    private JTable tableGerbong;
    private DefaultTableModel modelGerbong;
    private JTable tableKursi;
    private DefaultTableModel modelKursi;
    private JLabel lblKursiTitle;

    public GerbongManagementPanel() {
        this.gerbongDAO = new GerbongDAO();
        this.kursiDAO = new KursiDAO();
        this.keretaDAO = new KeretaDAO();
        this.kelasDAO = new KelasKeretaDAO();
        initComponents();
        loadGerbong();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // SPLIT PANE
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        // TOP: GERBONG MANAGEMENT
        JPanel pnlGerbong = new JPanel(new BorderLayout());
        pnlGerbong.setOpaque(false);

        JPanel pnlHeaderGerbong = new JPanel(new BorderLayout());
        pnlHeaderGerbong.setOpaque(false);
        pnlHeaderGerbong.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel lblTitle = new JLabel("Daftar Gerbong");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JButton btnAdd = new JButton("Tambah Gerbong");
        btnAdd.setBackground(new Color(51, 144, 255));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        btnAdd.addActionListener(e -> showForm(null));

        JButton btnStok = new JButton("Stok Gerbong");
        btnStok.setBackground(new Color(76, 175, 80));
        btnStok.setForeground(Color.WHITE);
        btnStok.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        btnStok.addActionListener(e -> showStokDialog());

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setOpaque(false);
        pnlButtons.add(btnStok);
        pnlButtons.add(btnAdd);

        pnlHeaderGerbong.add(lblTitle, BorderLayout.WEST);
        pnlHeaderGerbong.add(pnlButtons, BorderLayout.EAST);

        String[] colsG = { "No", "Nomor Gerbong", "Kereta", "Kelas", "Tanggal Dibuat", "ID" };
        modelGerbong = new DefaultTableModel(colsG, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tableGerbong = new JTable(modelGerbong);
        tableGerbong.setRowHeight(40);
        tableGerbong.getColumnModel().getColumn(5).setMinWidth(0);
        tableGerbong.getColumnModel().getColumn(5).setMaxWidth(0);
        tableGerbong.getColumnModel().getColumn(5).setWidth(0);
        tableGerbong.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                loadKursi();
        });

        JScrollPane spG = new JScrollPane(tableGerbong);
        spG.putClientProperty(FlatClientProperties.STYLE, "arc: 15; border: 0,0,0,0");

        pnlGerbong.add(pnlHeaderGerbong, BorderLayout.NORTH);
        pnlGerbong.add(spG, BorderLayout.CENTER);

        // BOTTOM: KURSI MANAGEMENT
        JPanel pnlKursi = new JPanel(new BorderLayout());
        pnlKursi.setOpaque(false);
        pnlKursi.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        lblKursiTitle = new JLabel("Pilih Gerbong untuk melihat kursi");
        lblKursiTitle.setFont(new Font("Inter", Font.BOLD, 16));
        lblKursiTitle.setForeground(new Color(180, 180, 180));

        String[] colsK = { "No", "Baris", "Kode Kursi", "ID" };
        modelKursi = new DefaultTableModel(colsK, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tableKursi = new JTable(modelKursi);
        tableKursi.setRowHeight(35);
        tableKursi.getColumnModel().getColumn(3).setMinWidth(0);
        tableKursi.getColumnModel().getColumn(3).setMaxWidth(0);
        tableKursi.getColumnModel().getColumn(3).setWidth(0);

        JScrollPane spK = new JScrollPane(tableKursi);
        spK.putClientProperty(FlatClientProperties.STYLE, "arc: 15; border: 0,0,0,0");

        pnlKursi.add(lblKursiTitle, BorderLayout.NORTH);
        pnlKursi.add(spK, BorderLayout.CENTER);

        splitPane.setTopComponent(pnlGerbong);
        splitPane.setBottomComponent(pnlKursi);

        add(splitPane, BorderLayout.CENTER);

        // Sidebar for Actions
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlActions.setOpaque(false);

        JButton btnDelete = new JButton("Hapus Gerbong");
        btnDelete.setBackground(new Color(244, 67, 54));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        btnDelete.addActionListener(e -> deleteGerbong());

        pnlActions.add(btnDelete);
        add(pnlActions, BorderLayout.SOUTH);
    }

    private void loadGerbong() {
        modelGerbong.setRowCount(0);
        List<Gerbong> list = gerbongDAO.getAll();
        int no = 1;
        for (Gerbong g : list) {
            modelGerbong.addRow(new Object[] {
                    no++,
                    g.getNomorGerbong(),
                    g.getNamaKereta(),
                    g.getNamaKelas(),
                    g.getCreatedAt(),
                    g.getId()
            });
        }
    }

    private void loadKursi() {
        int row = tableGerbong.getSelectedRow();
        if (row != -1) {
            int gId = (int) tableGerbong.getModel().getValueAt(row, 5);
            String name = (String) tableGerbong.getModel().getValueAt(row, 1);
            lblKursiTitle.setText("Kursi di Gerbong: " + name);
            lblKursiTitle.setForeground(Color.WHITE);

            modelKursi.setRowCount(0);
            List<Kursi> list = kursiDAO.getByGerbongId(gId);
            int no = 1;
            for (Kursi k : list) {
                modelKursi.addRow(new Object[] { no++, k.getBarisKursi(), k.getKodeKursi(), k.getId() });
            }
        } else {
            lblKursiTitle.setText("Pilih Gerbong untuk melihat kursi");
            modelKursi.setRowCount(0);
        }
    }

    private void deleteGerbong() {
        int row = tableGerbong.getSelectedRow();
        if (row != -1) {
            int id = (int) tableGerbong.getModel().getValueAt(row, 5);
            int confirm = JOptionPane.showConfirmDialog(this, "Hapus gerbong ini beserta semua kursinya?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                kursiDAO.deleteByGerbongId(id);
                gerbongDAO.delete(id);
                loadGerbong();
                loadKursi();
            }
        }
    }

    private void showForm(Gerbong g) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Manajemen Gerbong", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);

        JPanel pnl = new JPanel(new GridLayout(0, 1, 10, 10));
        pnl.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        List<Kereta> keretas = keretaDAO.getAll();
        JComboBox<ComboItem> cbKereta = new JComboBox<>();
        keretas.forEach(k -> cbKereta.addItem(new ComboItem(k.getId(), k.getNamaKereta())));

        List<KelasKereta> kelass = kelasDAO.getAll();
        JComboBox<ComboItem> cbKelas = new JComboBox<>();
        kelass.forEach(k -> cbKelas.addItem(new ComboItem(k.getId(), k.getNamaKelasKereta())));

        JTextField fNomor = new JTextField();
        fNomor.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Contoh: Eksekutif-1");

        JTextField fKapasitas = new JTextField();
        fKapasitas.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Jumlah Kursi (Misal: 50)");

        pnl.add(new JLabel("Pilih Kereta:"));
        pnl.add(cbKereta);
        pnl.add(new JLabel("Pilih Kelas:"));
        pnl.add(cbKelas);
        pnl.add(new JLabel("Nomor Gerbong:"));
        pnl.add(fNomor);
        pnl.add(new JLabel("Kapasitas Kursi (Auto Generate):"));
        pnl.add(fKapasitas);

        JButton btnSave = new JButton("Simpan & Generate Kursi");
        btnSave.setBackground(new Color(51, 144, 255));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> {
            try {
                if (fNomor.getText().isEmpty() || fKapasitas.getText().isEmpty())
                    throw new Exception("Data tidak lengkap!");

                Gerbong newG = new Gerbong();
                newG.setKeretaId(((ComboItem) cbKereta.getSelectedItem()).id);
                newG.setKelasId(((ComboItem) cbKelas.getSelectedItem()).id);
                newG.setNomorGerbong(fNomor.getText());

                int newId = gerbongDAO.insert(newG);
                if (newId != -1) {
                    int cap = Integer.parseInt(fKapasitas.getText());
                    List<Kursi> listKursi = generateSeats(newId, cap);
                    kursiDAO.insertBatch(listKursi);
                    loadGerbong();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Gerbong & " + cap + " Kursi berhasil dibuat!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        dialog.add(pnl, BorderLayout.CENTER);
        dialog.add(btnSave, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showStokDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Manajemen Stok Gerbong", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        String[] cols = { "ID", "Nama Kelas", "Total Stok" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 2; // Hanya stok yang bisa diedit
            }
        };

        List<KelasKereta> list = kelasDAO.getAll();
        for (KelasKereta k : list) {
            model.addRow(new Object[] { k.getId(), k.getNamaKelasKereta(), k.getStokGerbong() });
        }

        JTable table = new JTable(model);
        table.setRowHeight(35);

        JButton btnSave = new JButton("Simpan Perubahan Stok");
        btnSave.setBackground(new Color(51, 144, 255));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> {
            try {
                if (table.isEditing())
                    table.getCellEditor().stopCellEditing();

                for (int i = 0; i < model.getRowCount(); i++) {
                    int id = (int) model.getValueAt(i, 0);
                    String nama = (String) model.getValueAt(i, 1);
                    int stok = Integer.parseInt(model.getValueAt(i, 2).toString());

                    KelasKereta k = new KelasKereta();
                    k.setId(id);
                    k.setNamaKelasKereta(nama);
                    k.setStokGerbong(stok);
                    kelasDAO.update(k);
                }
                JOptionPane.showMessageDialog(dialog, "Stok berhasil diperbarui!");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: Pastikan input stok berupa angka.");
            }
        });

        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.add(btnSave, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private List<Kursi> generateSeats(int gerbongId, int capacity) {
        List<Kursi> list = new ArrayList<>();
        char[] letters = { 'A', 'B', 'C', 'D' };
        int totalSeats = 0;
        int row = 1;

        while (totalSeats < capacity) {
            for (char letter : letters) {
                if (totalSeats >= capacity)
                    break;
                Kursi k = new Kursi();
                k.setGerbongId(gerbongId);
                k.setBarisKursi(row);
                k.setKodeKursi(row + String.valueOf(letter));
                list.add(k);
                totalSeats++;
            }
            row++;
        }
        return list;
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
