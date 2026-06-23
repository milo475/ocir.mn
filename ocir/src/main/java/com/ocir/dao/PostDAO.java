package com.ocir.dao;

import com.ocir.model.Post;
import com.ocir.util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {

    public boolean createPost(Post post) {
        String sql = "INSERT INTO posts (user_id, content, image_path) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, post.getUserId());
            ps.setString(2, post.getContent());
            ps.setString(3, post.getImagePath());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static final int PAGE_SIZE = 10;

    public List<Post> getFeedPosts(int currentUserId) {
        return getFeedPosts(currentUserId, 0);
    }

    public List<Post> getFeedPosts(int currentUserId, int offset) {
        String sql = "SELECT p.*, u.username, u.display_name, u.profile_image, " +
                "(SELECT COUNT(*) FROM likes WHERE post_id=p.id) as like_count, " +
                "(SELECT COUNT(*) FROM comments WHERE post_id=p.id) as comment_count, " +
                "(SELECT COUNT(*) FROM likes WHERE post_id=p.id AND user_id=?) as liked " +
                "FROM posts p JOIN users u ON p.user_id=u.id ORDER BY p.created_at DESC LIMIT ? OFFSET ?";
        List<Post> posts = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ps.setInt(2, PAGE_SIZE);
            ps.setInt(3, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) posts.add(mapPost(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    public List<Post> getUserPosts(int userId, int currentUserId) {
        return getUserPosts(userId, currentUserId, 0);
    }

    public List<Post> getUserPosts(int userId, int currentUserId, int offset) {
        String sql = "SELECT p.*, u.username, u.display_name, u.profile_image, " +
                "(SELECT COUNT(*) FROM likes WHERE post_id=p.id) as like_count, " +
                "(SELECT COUNT(*) FROM comments WHERE post_id=p.id) as comment_count, " +
                "(SELECT COUNT(*) FROM likes WHERE post_id=p.id AND user_id=?) as liked " +
                "FROM posts p JOIN users u ON p.user_id=u.id WHERE p.user_id=? ORDER BY p.created_at DESC LIMIT ? OFFSET ?";
        List<Post> posts = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ps.setInt(2, userId);
            ps.setInt(3, PAGE_SIZE);
            ps.setInt(4, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) posts.add(mapPost(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    public boolean deletePost(int postId, int userId) {
        String sql = "DELETE FROM posts WHERE id=? AND user_id=?";
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

    public boolean updatePost(int postId, int userId, String content) {
        String sql = "UPDATE posts SET content=? WHERE id=? AND user_id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, content);
            ps.setInt(2, postId);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Post> searchPosts(String query, int currentUserId) {
        String sql = "SELECT p.*, u.username, u.display_name, u.profile_image, " +
                "(SELECT COUNT(*) FROM likes WHERE post_id=p.id) as like_count, " +
                "(SELECT COUNT(*) FROM comments WHERE post_id=p.id) as comment_count, " +
                "(SELECT COUNT(*) FROM likes WHERE post_id=p.id AND user_id=?) as liked " +
                "FROM posts p JOIN users u ON p.user_id=u.id WHERE p.content LIKE ? ORDER BY p.created_at DESC LIMIT 20";
        List<Post> posts = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ps.setString(2, "%" + query + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) posts.add(mapPost(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    private Post mapPost(ResultSet rs) throws SQLException {
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
        return p;
    }
}
