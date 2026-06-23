package com.ocir.dao;

import com.ocir.model.Post;
import com.ocir.util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookmarkDAO {

    public boolean toggleBookmark(int userId, int postId) {
        if (isBookmarked(userId, postId)) {
            String sql = "DELETE FROM bookmarks WHERE user_id=? AND post_id=?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId); ps.setInt(2, postId);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) { e.printStackTrace(); }
        } else {
            String sql = "INSERT INTO bookmarks (user_id, post_id) VALUES (?, ?)";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId); ps.setInt(2, postId);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }

    public boolean isBookmarked(int userId, int postId) {
        String sql = "SELECT COUNT(*) FROM bookmarks WHERE user_id=? AND post_id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, postId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<Post> getBookmarkedPosts(int userId) {
        String sql = "SELECT p.*, u.username, u.display_name, u.profile_image, " +
                "(SELECT COUNT(*) FROM likes WHERE post_id=p.id) as like_count, " +
                "(SELECT COUNT(*) FROM comments WHERE post_id=p.id) as comment_count, " +
                "(SELECT COUNT(*) FROM likes WHERE post_id=p.id AND user_id=?) as liked " +
                "FROM bookmarks b JOIN posts p ON b.post_id=p.id JOIN users u ON p.user_id=u.id " +
                "WHERE b.user_id=? ORDER BY b.created_at DESC";
        List<Post> posts = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Post p = new Post();
                p.setId(rs.getInt("id"));
                p.setUserId(rs.getInt("user_id"));
                p.setContent(rs.getString("content"));
                p.setImagePath(rs.getString("image_path"));
                p.setCreatedAt(rs.getTimestamp("created_at"));
                p.setAuthorUsername(rs.getString("username"));
                p.setAuthorDisplayName(rs.getString("display_name"));
                p.setAuthorProfileImage(rs.getString("profile_image"));
                p.setLikeCount(rs.getInt("like_count"));
                p.setCommentCount(rs.getInt("comment_count"));
                p.setLikedByCurrentUser(rs.getInt("liked") > 0);
                posts.add(p);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return posts;
    }
}
