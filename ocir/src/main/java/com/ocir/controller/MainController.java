package com.ocir.controller;

import com.ocir.App;
import com.ocir.dao.*;
import com.ocir.model.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Popup;

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
    @FXML private Button notifBellBtn;
    @FXML private Label notifBadge;
    @FXML private StackPane notifBellPane;

    private final PostDAO postDAO = new PostDAO();
    private final LikeDAO likeDAO = new LikeDAO();
    private final CommentDAO commentDAO = new CommentDAO();
    private final FollowDAO followDAO = new FollowDAO();
    private final UserDAO userDAO = new UserDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private Popup notifPopup;

    @FXML
    public void initialize() {
        User user = App.getCurrentUser();
        currentUserLabel.setText(user.getUsername());
        profileNameLabel.setText(user.getDisplayName() != null ? user.getDisplayName() : user.getUsername());
        updateProfileStats();
        loadFeed();
        loadSuggestedUsers();
        searchField.setOnAction(e -> handleSearch());
        updateNotifBadge();

        // Replace bell button text with FontAwesome icon
        FontAwesomeIconView bellIcon = new FontAwesomeIconView(FontAwesomeIcon.BELL);
        bellIcon.setSize("18");
        bellIcon.setStyle("-fx-fill: #F5D5E0;");
        notifBellBtn.setText("");
        notifBellBtn.setGraphic(bellIcon);
    }

    private void updateNotifBadge() {
        int count = notificationDAO.getUnreadCount(App.getCurrentUser().getId());
        if (count > 0) {
            notifBadge.setText(String.valueOf(count));
            notifBadge.setVisible(true);
        } else {
            notifBadge.setText("");
            notifBadge.setVisible(false);
        }
    }

    @FXML
    private void toggleNotifications() {
        if (notifPopup != null && notifPopup.isShowing()) {
            notifPopup.hide();
            return;
        }
        notifPopup = new Popup();
        notifPopup.setAutoHide(true);

        VBox content = new VBox(8);
        content.setPadding(new Insets(18));
        content.setPrefWidth(380);
        content.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(66,13,75,0.25), 20, 0, 0, 5);");

        Label title = new Label("🔔 Мэдэгдлүүд");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #420D4B;");
        content.getChildren().add(title);

        List<Notification> notifs = notificationDAO.getNotifications(App.getCurrentUser().getId());
        if (notifs.isEmpty()) {
            Label empty = new Label("Мэдэгдэл байхгүй");
            empty.setStyle("-fx-text-fill: #888; -fx-font-size: 13; -fx-padding: 20 0 20 0;");
            content.getChildren().add(empty);
        } else {
            for (Notification n : notifs) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(10, 12, 10, 12));
                row.setStyle(n.isRead()
                    ? "-fx-background-color: #fafafa; -fx-background-radius: 8; -fx-cursor: hand;"
                    : "-fx-background-color: #F5D5E0; -fx-background-radius: 8; -fx-cursor: hand;");

                FontAwesomeIconView icon = getNotifIcon(n.getType());
                VBox textBox = new VBox(2);
                Label text = new Label(getNotifText(n));
                text.setStyle("-fx-font-size: 13; -fx-text-fill: #210635;");
                text.setWrapText(true);
                Label time = new Label(n.getCreatedAt().toString());
                time.setStyle("-fx-font-size: 10; -fx-text-fill: #6667AB;");
                textBox.getChildren().addAll(text, time);
                HBox.setHgrow(textBox, Priority.ALWAYS);

                row.getChildren().addAll(icon, textBox);

                // Click to navigate
                row.setOnMouseClicked(e -> {
                    notifPopup.hide();
                    navigateToNotification(n);
                });

                content.getChildren().add(row);
            }
        }

        notifPopup.getContent().add(content);
        var bounds = notifBellPane.localToScreen(notifBellPane.getBoundsInLocal());
        notifPopup.show(notifBellPane, bounds.getMinX() - 300, bounds.getMaxY() + 8);

        notificationDAO.markAllRead(App.getCurrentUser().getId());
        updateNotifBadge();
    }

    private void navigateToNotification(Notification n) {
        switch (n.getType()) {
            case "MESSAGE":
                User msgUser = userDAO.findById(n.getFromUserId());
                if (msgUser != null) { App.setViewedUser(msgUser); App.navigateTo("view/chat.fxml"); }
                break;
            case "FOLLOW":
                User follower = userDAO.findById(n.getFromUserId());
                if (follower != null) App.viewProfile(follower);
                break;
            case "LIKE":
            case "COMMENT":
                // Go to own profile to see the post
                App.navigateTo("view/profile.fxml");
                break;
        }
    }

    private FontAwesomeIconView getNotifIcon(String type) {
        FontAwesomeIconView icon;
        switch (type) {
            case "LIKE": icon = new FontAwesomeIconView(FontAwesomeIcon.HEART); icon.setStyle("-fx-fill: #e74c3c;"); break;
            case "COMMENT": icon = new FontAwesomeIconView(FontAwesomeIcon.COMMENT); icon.setStyle("-fx-fill: #6667AB;"); break;
            case "FOLLOW": icon = new FontAwesomeIconView(FontAwesomeIcon.USER_PLUS); icon.setStyle("-fx-fill: #7B337E;"); break;
            case "MESSAGE": icon = new FontAwesomeIconView(FontAwesomeIcon.ENVELOPE); icon.setStyle("-fx-fill: #420D4B;"); break;
            default: icon = new FontAwesomeIconView(FontAwesomeIcon.BELL); icon.setStyle("-fx-fill: #7B337E;"); break;
        }
        icon.setSize("14");
        return icon;
    }

    private String getNotifText(Notification n) {
        switch (n.getType()) {
            case "LIKE": return n.getFromUsername() + " таны постод лайк дарлаа";
            case "COMMENT": return n.getFromUsername() + " таны постод коммент бичлээ";
            case "FOLLOW": return n.getFromUsername() + " таныг дагалаа";
            case "MESSAGE": return n.getFromUsername() + " танд мессеж илгээлээ";
            default: return n.getFromUsername() + " шинэ мэдэгдэл";
        }
    }

    private void handleSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) { loadFeed(); return; }
        postsContainer.getChildren().clear();
        List<User> users = userDAO.searchUsers(query);
        if (!users.isEmpty()) {
            Label t = new Label("👤 Хэрэглэгчид");
            t.getStyleClass().add("section-title");
            postsContainer.getChildren().add(t);
            for (User u : users) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(8));
                row.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-cursor: hand;");
                Label name = new Label(u.getDisplayName() != null ? u.getDisplayName() : u.getUsername());
                name.setStyle("-fx-font-weight: bold;");
                Label uname = new Label("@" + u.getUsername());
                uname.getStyleClass().add("post-time");
                row.getChildren().addAll(name, uname);
                row.setOnMouseClicked(ev -> App.viewProfile(u));
                postsContainer.getChildren().add(row);
            }
        }
        List<Post> posts = postDAO.searchPosts(query, App.getCurrentUser().getId());
        if (!posts.isEmpty()) {
            Label t = new Label("📰 Постууд");
            t.getStyleClass().add("section-title");
            postsContainer.getChildren().add(t);
            for (Post post : posts) postsContainer.getChildren().add(createPostCard(post));
        }
        if (users.isEmpty() && posts.isEmpty()) {
            postsContainer.getChildren().add(new Label("Илэрц олдсонгүй"));
        }
    }

    private void loadFeed() {
        postsContainer.getChildren().clear();
        List<Post> posts = postDAO.getFeedPosts(App.getCurrentUser().getId());
        for (Post post : posts) postsContainer.getChildren().add(createPostCard(post));
    }

    private VBox createPostCard(Post post) {
        VBox card = new VBox(8);
        card.getStyleClass().add("post-card");
        card.setPadding(new Insets(15));

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        String profileImg = post.getAuthorProfileImage();
        if (profileImg != null && !profileImg.isEmpty()) {
            File imgFile = new File(profileImg);
            if (imgFile.exists()) {
                ImageView avatar = new ImageView(new Image(imgFile.toURI().toString()));
                avatar.setFitWidth(32); avatar.setFitHeight(32); avatar.setPreserveRatio(true);
                header.getChildren().add(avatar);
            }
        }

        Label authorLabel = new Label(post.getAuthorDisplayName() != null ? post.getAuthorDisplayName() : post.getAuthorUsername());
        authorLabel.getStyleClass().add("post-author");
        authorLabel.setStyle("-fx-cursor: hand;");
        authorLabel.setOnMouseClicked(e -> { User author = userDAO.findById(post.getUserId()); if (author != null) App.viewProfile(author); });
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
                boolean wasFollowing = followDAO.isFollowing(App.getCurrentUser().getId(), post.getUserId());
                followDAO.toggleFollow(App.getCurrentUser().getId(), post.getUserId());
                if (!wasFollowing) {
                    notificationDAO.createNotification(post.getUserId(), App.getCurrentUser().getId(), "FOLLOW", null);
                }
                loadFeed(); loadSuggestedUsers(); updateProfileStats();
            });
            header.getChildren().add(followBtn);
        } else {
            Button deleteBtn = new Button("🗑");
            deleteBtn.getStyleClass().add("btn-delete");
            deleteBtn.setOnAction(e -> { postDAO.deletePost(post.getId(), App.getCurrentUser().getId()); loadFeed(); });
            header.getChildren().add(deleteBtn);
        }

        Label contentLabel = new Label(post.getContent());
        contentLabel.setWrapText(true);
        contentLabel.getStyleClass().add("post-content");
        card.getChildren().addAll(header, contentLabel);

        if (post.getImagePath() != null && !post.getImagePath().isEmpty()) {
            File imgFile = new File(post.getImagePath());
            if (imgFile.exists()) {
                ImageView postImage = new ImageView(new Image(imgFile.toURI().toString()));
                postImage.setFitWidth(500); postImage.setPreserveRatio(true);
                card.getChildren().add(postImage);
            }
        }

        // Like & Comment bar with FontAwesome icons
        HBox actions = new HBox(15);
        actions.setAlignment(Pos.CENTER_LEFT);

        FontAwesomeIconView likeIcon = new FontAwesomeIconView(post.isLikedByCurrentUser() ? FontAwesomeIcon.HEART : FontAwesomeIcon.HEART_ALT);
        likeIcon.setSize("14");
        likeIcon.setStyle(post.isLikedByCurrentUser() ? "-fx-fill: #e74c3c;" : "-fx-fill: #7B337E;");
        Button likeBtn = new Button(" " + post.getLikeCount() + " Like");
        likeBtn.setGraphic(likeIcon);
        likeBtn.getStyleClass().add("btn-action");
        likeBtn.setOnAction(e -> {
            boolean wasLiked = post.isLikedByCurrentUser();
            likeDAO.toggleLike(post.getId(), App.getCurrentUser().getId());
            if (!wasLiked && post.getUserId() != App.getCurrentUser().getId()) {
                notificationDAO.createNotification(post.getUserId(), App.getCurrentUser().getId(), "LIKE", post.getId());
            }
            loadFeed();
        });

        FontAwesomeIconView commentIcon = new FontAwesomeIconView(FontAwesomeIcon.COMMENT);
        commentIcon.setSize("14");
        commentIcon.setStyle("-fx-fill: #6667AB;");
        Button commentBtn = new Button(" " + post.getCommentCount() + " Comment");
        commentBtn.setGraphic(commentIcon);
        commentBtn.getStyleClass().add("btn-action");
        actions.getChildren().addAll(likeBtn, commentBtn);
        card.getChildren().add(actions);

        VBox commentsBox = new VBox(5);
        commentsBox.setPadding(new Insets(10, 0, 0, 0));
        commentsBox.setVisible(false);
        commentsBox.setManaged(false);
        commentBtn.setOnAction(e -> {
            boolean show = !commentsBox.isVisible();
            commentsBox.setVisible(show); commentsBox.setManaged(show);
            if (show) loadComments(commentsBox, post.getId(), post.getUserId());
        });
        card.getChildren().add(commentsBox);
        return card;
    }

    private void loadComments(VBox commentsBox, int postId, int postOwnerId) {
        commentsBox.getChildren().clear();
        List<Comment> comments = commentDAO.getCommentsByPost(postId);
        for (Comment c : comments) {
            HBox row = new HBox(5);
            row.setAlignment(Pos.CENTER_LEFT);
            Label authorLbl = new Label(c.getAuthorUsername());
            authorLbl.setStyle("-fx-font-weight: bold; -fx-cursor: hand;");
            authorLbl.setOnMouseClicked(e -> { User u = userDAO.findById(c.getUserId()); if (u != null) App.viewProfile(u); });
            Label contentLbl = new Label(": " + c.getContent());
            contentLbl.setWrapText(true);
            contentLbl.getStyleClass().add("comment-text");
            row.getChildren().addAll(authorLbl, contentLbl);
            if (c.getUserId() == App.getCurrentUser().getId()) {
                Button del = new Button("✕");
                del.getStyleClass().add("btn-delete-small");
                del.setOnAction(e -> { commentDAO.deleteComment(c.getId(), App.getCurrentUser().getId()); loadComments(commentsBox, postId, postOwnerId); });
                row.getChildren().add(del);
            }
            commentsBox.getChildren().add(row);
        }
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
                if (postOwnerId != App.getCurrentUser().getId()) {
                    notificationDAO.createNotification(postOwnerId, App.getCurrentUser().getId(), "COMMENT", postId);
                }
                input.clear();
                loadComments(commentsBox, postId, postOwnerId);
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
            fb.setOnAction(e -> {
                followDAO.toggleFollow(App.getCurrentUser().getId(), u.getId());
                notificationDAO.createNotification(u.getId(), App.getCurrentUser().getId(), "FOLLOW", null);
                loadSuggestedUsers(); loadFeed(); updateProfileStats();
            });
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
            Post post = new Post(); post.setUserId(App.getCurrentUser().getId()); post.setContent(content);
            postDAO.createPost(post); quickPostField.clear(); loadFeed();
        }
    }

    @FXML private void handleLogout() { App.setCurrentUser(null); App.navigateTo("view/login.fxml"); }
    @FXML private void showFeed() { loadFeed(); }
    @FXML private void showProfile() { App.navigateTo("view/profile.fxml"); }
    @FXML private void showCreatePost() { App.navigateTo("view/create_post.fxml"); }
    @FXML private void showFollowers() { App.navigateTo("view/profile.fxml"); }
    @FXML private void showMessages() { App.navigateTo("view/messages.fxml"); }
}
