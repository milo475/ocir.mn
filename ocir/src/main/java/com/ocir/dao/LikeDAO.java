package com.ocir.dao;

import com.ocir.util.DatabaseManager;
import java.sql.*;

public class LikeDAO {

    public boolean toggleLike(int postId, int userId) {
        if (isLiked(postId, userId)) {
            return unlike(postId, userId);
        } else {
            return like(postId, userId);
        }
    }

    public boolean isLiked(int postId, int userId) {
        String sql = "SELECT COUNT(*) FROM likes WHERE post_id=? AND user_id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean like(int postId, int userId) {
        String sql = "INSERT INTO likes (post_id, user_id) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean unlike(int postId, int userId) {
        String sql = "DELETE FROM likes WHERE post_id=? AND user_id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
