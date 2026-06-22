package com.ocir.dao;

import com.ocir.model.Message;
import com.ocir.model.User;
import com.ocir.util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public boolean sendMessage(int senderId, int receiverId, String content) {
        String sql = "INSERT INTO messages (sender_id, receiver_id, content) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            ps.setString(3, content);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Message> getConversation(int userId1, int userId2) {
        String sql = "SELECT m.*, u.username, u.display_name FROM messages m " +
                "JOIN users u ON m.sender_id=u.id " +
                "WHERE (m.sender_id=? AND m.receiver_id=?) OR (m.sender_id=? AND m.receiver_id=?) " +
                "ORDER BY m.created_at ASC";
        List<Message> messages = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId1);
            ps.setInt(2, userId2);
            ps.setInt(3, userId2);
            ps.setInt(4, userId1);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Message m = new Message();
                m.setId(rs.getInt("id"));
                m.setSenderId(rs.getInt("sender_id"));
                m.setReceiverId(rs.getInt("receiver_id"));
                m.setContent(rs.getString("content"));
                m.setCreatedAt(rs.getTimestamp("created_at"));
                m.setSenderUsername(rs.getString("username"));
                m.setSenderDisplayName(rs.getString("display_name"));
                messages.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public List<User> getConversationUsers(int userId) {
        String sql = "SELECT DISTINCT u.* FROM users u " +
                "JOIN messages m ON (u.id=m.sender_id OR u.id=m.receiver_id) " +
                "WHERE (m.sender_id=? OR m.receiver_id=?) AND u.id!=? " +
                "ORDER BY (SELECT MAX(created_at) FROM messages WHERE " +
                "(sender_id=? AND receiver_id=u.id) OR (sender_id=u.id AND receiver_id=?)) DESC";
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);
            ps.setInt(4, userId);
            ps.setInt(5, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setDisplayName(rs.getString("display_name"));
                u.setProfileImage(rs.getString("profile_image"));
                users.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public Message getLastMessage(int userId1, int userId2) {
        String sql = "SELECT m.*, u.username, u.display_name FROM messages m " +
                "JOIN users u ON m.sender_id=u.id " +
                "WHERE (m.sender_id=? AND m.receiver_id=?) OR (m.sender_id=? AND m.receiver_id=?) " +
                "ORDER BY m.created_at DESC LIMIT 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId1);
            ps.setInt(2, userId2);
            ps.setInt(3, userId2);
            ps.setInt(4, userId1);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Message m = new Message();
                m.setId(rs.getInt("id"));
                m.setSenderId(rs.getInt("sender_id"));
                m.setReceiverId(rs.getInt("receiver_id"));
                m.setContent(rs.getString("content"));
                m.setCreatedAt(rs.getTimestamp("created_at"));
                m.setSenderUsername(rs.getString("username"));
                m.setSenderDisplayName(rs.getString("display_name"));
                return m;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
