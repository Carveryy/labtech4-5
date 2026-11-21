package labtech5.ui;

import labtech5.dao.LetterDAO;
import labtech5.dao.UserDAO;
import labtech5.model.Letter;
import labtech5.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LettersPanel extends JPanel {
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final LetterDAO letterDAO;
    private final UserDAO userDAO;

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JComboBox<User> senderCombo;
    private final JComboBox<User> receiverCombo;
    private final JTextField subjectField;
    private final JTextField bodyField;
    private final JTextField sendDateField;
    private Integer selectedLetterId = null;

    public LettersPanel(LetterDAO letterDAO, UserDAO userDAO) {
        this.letterDAO = letterDAO;
        this.userDAO = userDAO;

        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Відправник", "Одержувач", "Тема", "Текст", "Дата надсилання"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 15, 8));
        formPanel.setBorder(new EmptyBorder(10, 10, 5, 10));

        senderCombo = new JComboBox<>();
        receiverCombo = new JComboBox<>();
        subjectField = new JTextField();
        bodyField = new JTextField();
        sendDateField = new JTextField(LocalDateTime.now().format(DATE_TIME_FMT));

        formPanel.add(labeledField("Відправник:", senderCombo));
        formPanel.add(labeledField("Одержувач:", receiverCombo));
        formPanel.add(labeledField("Тема:", subjectField));
        formPanel.add(labeledField("Дата:", sendDateField));
        formPanel.add(labeledField("Текст листа:", bodyField));
        formPanel.add(new JLabel(""));

        JButton btnSend = new JButton("Надіслати");
        JButton btnDelete = new JButton("Видалити");
        JButton btnRefresh = new JButton("Оновити таблицю");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.add(btnSend);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(formPanel, BorderLayout.CENTER);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> refreshData());
        btnSend.addActionListener(e -> createLetter());
        btnDelete.addActionListener(e -> deleteLetter());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                selectedLetterId = (row >= 0) ? (Integer) tableModel.getValueAt(row, 0) : null;
            }
        });
    }

    private JPanel labeledField(String label, JComponent component) {
        JPanel p = new JPanel(new BorderLayout(5, 0));
        p.add(new JLabel(label), BorderLayout.WEST);
        p.add(component, BorderLayout.CENTER);
        return p;
    }

    public void reloadUsersCombo() {
        senderCombo.removeAllItems();
        receiverCombo.removeAllItems();
        try {
            for (User u : userDAO.getAll()) {
                senderCombo.addItem(u);
                receiverCombo.addItem(u);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Помилка завантаження списку користувачів: " + ex.getMessage());
        }
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        selectedLetterId = null;
        try {
            List<Letter> letters = letterDAO.getAll();
            for (Letter l : letters) {
                tableModel.addRow(new Object[]{
                        l.getId(),
                        l.getSender(),
                        l.getReceiver(),
                        l.getSubject(),
                        l.getBody(),
                        l.getSendDate()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Помилка завантаження листів: " + ex.getMessage());
        }
    }

    private void createLetter() {
        User sender = (User) senderCombo.getSelectedItem();
        User receiver = (User) receiverCombo.getSelectedItem();
        if (sender == null || receiver == null) {
            JOptionPane.showMessageDialog(this, "Додайте спочатку користувачів.");
            return;
        }
        try {
            letterDAO.create(sender.getId(), receiver.getId(),
                    subjectField.getText().trim(), bodyField.getText().trim(), sendDateField.getText().trim());
            refreshData();
            clearFields();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Помилка надсилання листа: " + ex.getMessage());
        }
    }

    private void deleteLetter() {
        if (selectedLetterId == null) {
            JOptionPane.showMessageDialog(this, "Спочатку виберіть лист у таблиці.");
            return;
        }
        try {
            letterDAO.delete(selectedLetterId);
            refreshData();
            clearFields();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Помилка видалення листа: " + ex.getMessage());
        }
    }

    private void clearFields() {
        selectedLetterId = null;
        subjectField.setText("");
        bodyField.setText("");
        sendDateField.setText(LocalDateTime.now().format(DATE_TIME_FMT));
        table.clearSelection();
    }
}