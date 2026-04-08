package org.example.view;

import com.formdev.flatlaf.FlatClientProperties;
import org.example.dao.RoleDAO;
import org.example.dao.UserDAO;
import org.example.model.Role;
import org.example.model.User;
import org.example.utils.PasswordUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    public UserManagementPanel() {
        this.userDAO = new UserDAO();
        this.roleDAO = new RoleDAO();
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

        JLabel lblTitle = new JLabel("Kelola Akun Pengguna");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        // Search & Add Button
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(250, 40));
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Cari Username atau Email...");
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                searchData();
            }
        });

        JButton btnAdd = new JButton("Tambah User");
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
        String[] columns = {"No", "Username", "Email", "Role", "Tanggal Dibuat", "ID"};
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
        table.setShowGrid(true);
        table.setGridColor(new Color(60, 60, 60));
        
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

        JButton btnEdit = new JButton("Edit User");
        btnEdit.setBackground(new Color(76, 175, 80));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setPreferredSize(new Dimension(130, 40));
        btnEdit.putClientProperty(FlatClientProperties.STYLE, "arc: 15");
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) table.getModel().getValueAt(row, 5);
                List<User> all = userDAO.getAll();
                User selected = all.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
                showForm(selected);
            } else {
                JOptionPane.showMessageDialog(this, "Pilih user yang ingin diubah!", "Peringatan", JOptionPane.WARNING_MESSAGE);
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
        List<User> list = userDAO.getAll();
        int no = 1;
        for (User u : list) {
            if (u.getRoleId() == 1) continue;
            tableModel.addRow(new Object[]{
                no++,
                u.getUsername(),
                u.getEmail(),
                u.getRoleName(),
                u.getCreatedAt(),
                u.getId()
            });
        }
    }

    private void searchData() {
        String keyword = txtSearch.getText();
        tableModel.setRowCount(0);
        List<User> list = userDAO.search(keyword);
        int no = 1;
        for (User u : list) {
            if (u.getRoleId() == 1) continue;
            tableModel.addRow(new Object[]{
                no++,
                u.getUsername(),
                u.getEmail(),
                u.getRoleName(),
                u.getCreatedAt(),
                u.getId()
            });
        }
    }

    private void deleteData() {
        int row = table.getSelectedRow();
        if (row != -1) {
            int id = (int) table.getModel().getValueAt(row, 5);
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus user ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (userDAO.delete(id)) {
                    loadData();
                    JOptionPane.showMessageDialog(this, "User berhasil dihapus!");
                }
            }
        }
    }

    private void showForm(User user) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            user == null ? "Tambah User" : "Edit User", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 600); // Increased height to prevent cutoff
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JTextField fUsername = new JTextField();
        fUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Username");
        JTextField fEmail = new JTextField();
        fEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "email@gmail.com");
        JPasswordField fPassword = new JPasswordField();
        fPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Password");
        
        List<Role> roles = roleDAO.getAll();
        JComboBox<Role> cbRole = new JComboBox<>();
        roles.stream().filter(r -> r.getId() != 1).forEach(cbRole::addItem);

        if (user != null) {
            fUsername.setText(user.getUsername());
            fEmail.setText(user.getEmail());
            fPassword.setText("********"); // Dummy to indicate it exists
            // Set Selected Role
            for (int i = 0; i < cbRole.getItemCount(); i++) {
                if (((Role)cbRole.getItemAt(i)).getId() == user.getRoleId()) {
                    cbRole.setSelectedIndex(i);
                    break;
                }
            }
        }

        panel.add(new JLabel("Username:"));
        panel.add(fUsername);
        panel.add(new JLabel("Email:"));
        panel.add(fEmail);
        panel.add(new JLabel("Password:"));
        panel.add(fPassword);
        panel.add(new JLabel("Role:"));
        panel.add(cbRole);

        JButton btnSave = new JButton("Simpan");
        btnSave.setPreferredSize(new Dimension(0, 50));
        btnSave.setBackground(new Color(51, 144, 255));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> {
            String username = fUsername.getText().trim();
            String email = fEmail.getText().trim();
            String pass = new String(fPassword.getPassword());

            if (username.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Mohon isi semua field!");
                return;
            }

            User u = (user == null) ? new User() : user;
            u.setUsername(username);
            u.setEmail(email);
            u.setRoleId(((Role)cbRole.getSelectedItem()).getId());
            
            // Password logic
            if (user == null) {
                // New User: Password MUST be filled
                if (pass.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Password tidak boleh kosong untuk user baru!");
                    return;
                }
                u.setPassword(PasswordUtil.hash(pass));
            } else {
                // Edit User: Only update if changed from dummy
                if (!pass.equals("********") && !pass.isEmpty()) {
                    u.setPassword(PasswordUtil.hash(pass));
                }
            }

            boolean success = (user == null) ? userDAO.insert(u) : userDAO.update(u);
            if (success) {
                loadData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "User berhasil disimpan!");
            } else {
                JOptionPane.showMessageDialog(dialog, "Gagal menyimpan data user. Periksa kembali input atau koneksi database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnSave, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
