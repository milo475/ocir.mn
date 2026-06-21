package com.ocir.model;

import java.sql.Timestamp;

public class Post {
    private int id;
    private int userId;
    private String content;
    private String imagePath;
    private Timestamp createdAt;
    private String authorUsername;
    private String authorDisplayName;
    private String authorProfileImage;
    private int likeCount;
    private int commentCount;
    private boolean likedByCurrentUser;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    public String getAuthorDisplayName() { return authorDisplayName; }
    public void setAuthorDisplayName(String authorDisplayName) { this.authorDisplayName = authorDisplayName; }
    public String getAuthorProfileImage() { return authorProfileImage; }
    public void setAuthorProfileImage(String authorProfileImage) { this.authorProfileImage = authorProfileImage; }
    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
    public boolean isLikedByCurrentUser() { return likedByCurrentUser; }
    public void setLikedByCurrentUser(boolean likedByCurrentUser) { this.likedByCurrentUser = likedByCurrentUser; }
}
