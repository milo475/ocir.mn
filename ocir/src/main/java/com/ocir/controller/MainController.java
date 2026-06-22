package com.ocir.controller;

import com.ocir.App;
import com.ocir.dao.*;
import com.ocir.model.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.File;
import java.util.List;

public class MainController {
    @FXML private TextField searchField;
    @FXML private TextField quickPostField;
    @FXML private VBox postsContainer;
    @FXML private VBox suggestedUsersContainer;
    @FXML private Label currentUserLabel;
    @FXML private Label profileNameLabel;
    @FXML private Label profileStatsLabel;

    private final PostDAO postDAO = new PostDAO();
    private final LikeDAO likeDAO = new LikeDAO();
    private final CommentDAO commentDAO = new CommentDAO();
    private final FollowDAO followDAO = new FollowDAO();
    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        User user = App.getCurrentUser();
        currentUserLabel.setText(user.getUsername());
        profileNameLabel.setText(user.getDisplayName() != null ? user.getDisplayName() : user.getUsername());
        int followers = followDAO.getFollowerCount(user.getId());
        int following = followDAO.getFollowingCount(user.getId());
        profileStatsLabel.setText(followers + " Followers  |  " + following + " Following");
        loadFeed();
        loadSuggestedUsers();
    }

    private void loadFeed() {
        postsContainer.getChildren().clear();
        List<Post> posts = postDAO.getFeedPosts(App.getCurrentUser().getId());
        for (Post post : posts) {
            postsContainer.getChildren().add(createPostCard(post));
        }
    }

    private VBox createPostCard(Post post) {
        VBox card = new VBox(8);
        card.getStyleClass().add("post-card");
        card.setPadding(new Insets(15));

        // Header: author + follow btn
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        // Profile image
        String profileImg = post.getAuthorProfileImage();
        if (profileImg != null && !profileImg.isEmpty()) {
            File imgFile = new File(profileImg);
            if (imgFile.exists()) {
                ImageView avatar = new ImageView(new Image(imgFile.toURI().toString()));
                avatar.setFitWidth(32);
                avatar.setFitHeight(32);
                avatar.setPreserveRatio(true);
                header.getChildren().add(avatar);
            }
        }

        Label authorLabel = new Label(post.getAuthorDisplayName() != null ? post.getAuthorDisplayName() : post.getAuthorUsername());
        authorLabel.getStyleClass().add("post-author");
        Label timeLabel = new Label(post.getCreatedAt().toString());
        timeLabel.getStyleClass().add("post-time");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(authorLabel, timeLabel, spacer);

        if (post.getUserId() != App.getCurrentUser().getId()) {
            boolean isFollowing = followDAO.isFollowing(App.getCurrentUser().getId(), post.getUserId());
            Button followBtn = new Button(isFollowing ? "Unfollow" : "Follow");
            followBtn.getStyleClass().add(isFollowing ? "btn-unfollow" : "btn-follow");
            followBtn.setOnAction(e -> {
                followDAO.toggleFollow(App.getCurrentUser().getId(), post.getUserId());
                loadFeed();
                loadSuggestedUsers();
                updateProfileStats();
            });
            header.getChildren().add(followBtn);
        } else {
            Button deleteBtn = new Button("🗑");
            deleteBtn.getStyleClass().add("btn-delete");
            deleteBtn.setOnAction(e -> { postDAO.deletePost(post.getId(), App.getCurrentUser().getId()); loadFeed(); });
            header.getChildren().add(deleteBtn);
        }

        // Content
        Label contentLabel = new Label(post.getContent());
        contentLabel.setWrapText(true);
        contentLabel.getStyleClass().add("post-content");

        // Like & Comment bar
        HBox actions = new HBox(15);
        actions.setAlignment(Pos.CENTER_LEFT);
        Button likeBtn = new Button((post.isLikedByCurrentUser() ? "❤️ " : "🤍 ") + post.getLikeCount() + " Like");
        likeBtn.getStyleClass().add("btn-action");
        likeBtn.setOnAction(e -> { likeDAO.toggleLike(post.getId(), App.getCurrentUser().getId()); loadFeed(); });
        Button commentBtn = new Button("💬 " + post.getCommentCount() + " Comment");
        commentBtn.getStyleClass().add("btn-action");
        actions.getChildren().addAll(likeBtn, commentBtn);

        card.getChildren().addAll(header, contentLabel, actions);

        // Comments section (toggle)
        VBox commentsBox = new VBox(5);
        commentsBox.setPadding(new Insets(10, 0, 0, 0));
        commentsBox.setVisible(false);
        commentsBox.setManaged(false);

        commentBtn.setOnAction(e -> {
            boolean show = !commentsBox.isVisible();
            commentsBox.setVisible(show);
            commentsBox.setManaged(show);
            if (show) loadComments(commentsBox, post.getId());
        });

        card.getChildren().add(commentsBox);
        return card;
    }

    private void loadComments(VBox commentsBox, int postId) {
        commentsBox.getChildren().clear();
        List<Comment> comments = commentDAO.getCommentsByPost(postId);
        for (Comment c : comments) {
            HBox row = new HBox(5);
            row.setAlignment(Pos.CENTER_LEFT);
            Label lbl = new Label(c.getAuthorUsername() + ": " + c.getContent());
            lbl.setWrapText(true);
            lbl.getStyleClass().add("comment-text");
            row.getChildren().add(lbl);
            if (c.getUserId() == App.getCurrentUser().getId()) {
                Button del = new Button("✕");
                del.getStyleClass().add("btn-delete-small");
                del.setOnAction(e -> { commentDAO.deleteComment(c.getId(), App.getCurrentUser().getId()); loadComments(commentsBox, postId); });
                row.getChildren().add(del);
            }
            commentsBox.getChildren().add(row);
        }
        // Add comment input
        HBox addBox = new HBox(5);
        TextField input = new TextField();
        input.setPromptText("Comment бичих...");
        input.getStyleClass().add("comment-input");
        HBox.setHgrow(input, Priority.ALWAYS);
        Button send = new Button("➤");
        send.getStyleClass().add("btn-small");
        send.setOnAction(e -> {
            if (!input.getText().trim().isEmpty()) {
                Comment nc = new Comment();
                nc.setPostId(postId);
                nc.setUserId(App.getCurrentUser().getId());
                nc.setContent(input.getText().trim());
                commentDAO.addComment(nc);
                input.clear();
                loadComments(commentsBox, postId);
            }
        });
        addBox.getChildren().addAll(input, send);
        commentsBox.getChildren().add(addBox);
    }

    private void loadSuggestedUsers() {
        suggestedUsersContainer.getChildren().clear();
        List<User> suggested = userDAO.getSuggestedUsers(App.getCurrentUser().getId());
        for (User u : suggested) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.getStyleClass().add("suggested-user");
            row.setPadding(new Insets(8));
            Label name = new Label(u.getDisplayName() != null ? u.getDisplayName() : u.getUsername());
            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);
            Button fb = new Button("Follow");
            fb.getStyleClass().add("btn-follow");
            fb.setOnAction(e -> { followDAO.toggleFollow(App.getCurrentUser().getId(), u.getId()); loadSuggestedUsers(); loadFeed(); updateProfileStats(); });
            row.getChildren().addAll(name, sp, fb);
            suggestedUsersContainer.getChildren().add(row);
        }
    }

    private void updateProfileStats() {
        int followers = followDAO.getFollowerCount(App.getCurrentUser().getId());
        int following = followDAO.getFollowingCount(App.getCurrentUser().getId());
        profileStatsLabel.setText(followers + " Followers  |  " + following + " Following");
    }

    @FXML private void handleQuickPost() {
        String content = quickPostField.getText().trim();
        if (!content.isEmpty()) {
            Post post = new Post();
            post.setUserId(App.getCurrentUser().getId());
            post.setContent(content);
            postDAO.createPost(post);
            quickPostField.clear();
            loadFeed();
        }
    }

    @FXML private void handleLogout() { App.setCurrentUser(null); App.navigateTo("view/login.fxml"); }
    @FXML private void showFeed() { loadFeed(); }
    @FXML private void showProfile() { App.navigateTo("view/profile.fxml"); }
    @FXML private void showCreatePost() { App.navigateTo("view/create_post.fxml"); }
    @FXML private void showFollowers() { App.navigateTo("view/profile.fxml"); }
}
