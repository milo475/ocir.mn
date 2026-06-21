package com.ocir.dao;

import com.ocir.model.User;
import com.ocir.util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FollowDAO {

    public boolean toggleFollow(int followerId, int followingId) {
        if (isFollowing(followerId, followingId)) {
            return unfollow(followerId, followingId);
        } else {
            return follow(followerId, followingId);
        }
    }

    public boolean isFollowing(int followerId, int followingId) {
        String sql = "SELECT COUNT(*) FROM follows WHERE follower_id=? AND following_id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, followerId);
            ps.setInt(2, followingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getFollowerCount(int userId) {
        return getCount("SELECT COUNT(*) FROM follows WHERE following_id=?", userId);
    }

    public int getFollowingCount(int userId) {
        return getCount("SELECT COUNT(*) FROM follows WHERE follower_id=?", userId);
    }

    public List<User> getFollowers(int userId) {
        String sql = "SELECT u.* FROM users u JOIN follows f ON u.id=f.follower_id WHERE f.following_id=?";
        return queryUsers(sql, userId);
    }

    public List<User> getFollowing(int userId) {
        String sql = "SELECT u.* FROM users u JOIN follows f ON u.id=f.following_id WHERE f.follower_id=?";
        return queryUsers(sql, userId);
    }

    private boolean follow(int followerId, int followingId) {
        String sql = "INSERT INTO follows (follower_id, following_id) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, followerId);
            ps.setInt(2, followingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean unfollow(int followerId, int followingId) {
        String sql = "DELETE FROM follows WHERE follower_id=? AND following_id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, followerId);
            ps.setInt(2, followingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getCount(String sql, int userId) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private List<User> queryUsers(String sql, int userId) {
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
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
}
