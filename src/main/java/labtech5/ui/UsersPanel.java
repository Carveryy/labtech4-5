package labtech5.ui;

import labtech5.dao.UserDAO;
import labtech5.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UsersPanel extends JPanel {
    private final UserDAO userDAO;
    private final Runnable onUsersChanged;

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField nameField;
    private final JTextField birthDateField;
    private Integer selectedUserId = null;

    public UsersPanel(UserDAO userDAO, Runnable onUsersChanged) {
        this.userDAO = userDAO;
        this.onUsersChanged = onUsersChanged;

        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"ID", "ПІБ", "Дата народження"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        inputPanel.setBorder(new EmptyBorder(10, 10, 5, 10));

        JPanel namePanel = new JPanel(new BorderLayout(5, 0));
        nameField = new JTextField();
        namePanel.add(new JLabel("ПІБ:"), BorderLayout.WEST);
        namePanel.add(nameField, BorderLayout.CENTER);

        JPanel datePanel = new JPanel(new BorderLayout(5, 0));
        birthDateField = new JTextField("YYYY-MM-DD");
        datePanel.add(new JLabel("Дата:"), BorderLayout.WEST);
        datePanel.add(birthDateField, BorderLayout.CENTER);

        inputPanel.add(namePanel);
        inputPanel.add(datePanel);

        JButton btnAdd = new JButton("Додати");
        JButton btnUpdate = new JButton("Оновити");
        JButton btnDelete = new JButton("Видалити");
        JButton btnRefresh = new JButton("Оновити таблицю");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputPanel, BorderLayout.CENTER);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> refreshData());
        btnAdd.addActionListener(e -> createUser());
        btnUpdate.addActionListener(e -> updateUser());
        btnDelete.addActionListener(e -> deleteUser());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    selectedUserId = (Integer) tableModel.getValueAt(row, 0);
                    nameField.setText(tableModel.getValueAt(row, 1).toString());
                    birthDateField.setText(tableModel.getValueAt(row, 2).toString());
                }
            }
        });
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        selectedUserId = null;
        try {
            List<User> users = userDAO.getAll();
            for (User u : users) {
                tableModel.addRow(new Object[]{u.getId(), u.getFullName(), u.getBirthDate()});
            }
            if (onUsersChanged != null) {
                onUsersChanged.run();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Помилка завантаження користувачів: " + ex.getMessage());
        }
    }

    private void createUser() {
        try {
            userDAO.create(nameField.getText().trim(), birthDateField.getText().trim());
            refreshData();
            clearFields();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Помилка додавання: " + ex.getMessage());
        }
    }

    private void updateUser() {
        if (selectedUserId == null) {
            JOptionPane.showMessageDialog(this, "Спочатку виберіть користувача в таблиці.");
            return;
        }
        try {
            userDAO.update(selectedUserId, nameField.getText().trim(), birthDateField.getText().trim());
            refreshData();
            clearFields();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Помилка оновлення: " + ex.getMessage());
        }
    }

    private void deleteUser() {
        if (selectedUserId == null) {
            JOptionPane.showMessageDialog(this, "Спочатку виберіть користувача в таблиці.");
            return;
        }
        try {
            userDAO.delete(selectedUserId);
            refreshData();
            clearFields();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Помилка видалення: " + ex.getMessage());
        }
    }

    private void clearFields() {
        selectedUserId = null;
        nameField.setText("");
        birthDateField.setText("YYYY-MM-DD");
        table.clearSelection();
    }
}