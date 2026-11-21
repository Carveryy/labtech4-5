package labtech5;

import labtech5.dao.LetterDAO;
import labtech5.dao.UserDAO;
import labtech5.ui.UsersPanel;
import labtech5.ui.LettersPanel;

import javax.swing.*;
import java.awt.*;

public class CrudApp extends JFrame {

    public CrudApp() {
        setTitle("CRUD Додаток — Поштова система");
        setSize(820, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        UserDAO userDAO = new UserDAO();
        LetterDAO letterDAO = new LetterDAO();

        LettersPanel lettersPanel = new LettersPanel(letterDAO, userDAO);
        UsersPanel usersPanel = new UsersPanel(userDAO, () -> {
            lettersPanel.reloadUsersCombo();
            lettersPanel.refreshData();
        });

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Користувачі", usersPanel);
        tabbedPane.addTab("Листи", lettersPanel);

        add(tabbedPane, BorderLayout.CENTER);

        usersPanel.refreshData();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CrudApp().setVisible(true));
    }
}