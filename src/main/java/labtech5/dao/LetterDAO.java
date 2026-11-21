package labtech5.dao;

import labtech5.db.DatabaseConnection;
import labtech5.model.Letter;
import labtech5.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LetterDAO {

    public List<Letter> getAll() throws SQLException {
        List<Letter> letters = new ArrayList<>();
        String sql = """
                SELECT l.id, l.sender_id, u1.full_name AS sender_name, u1.birth_date AS sender_birth,
                       l.receiver_id, u2.full_name AS receiver_name, u2.birth_date AS receiver_birth,
                       l.subject, l.body, l.send_date
                FROM letters l
                JOIN users u1 ON l.sender_id = u1.id
                JOIN users u2 ON l.receiver_id = u2.id
                ORDER BY l.id
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User sender = new User(rs.getInt("sender_id"), rs.getString("sender_name"), rs.getString("sender_birth"));
                User receiver = new User(rs.getInt("receiver_id"), rs.getString("receiver_name"), rs.getString("receiver_birth"));

                letters.add(new Letter(
                        rs.getInt("id"),
                        sender,
                        receiver,
                        rs.getString("subject"),
                        rs.getString("body"),
                        rs.getString("send_date")
                ));
            }
        }
        return letters;
    }

    public void create(int senderId, int receiverId, String subject, String body, String sendDate) throws SQLException {
        String sql = "INSERT INTO letters (sender_id, receiver_id, subject, body, send_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, senderId);
            pstmt.setInt(2, receiverId);
            pstmt.setString(3, subject);
            pstmt.setString(4, body);
            pstmt.setString(5, sendDate);
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM letters WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}