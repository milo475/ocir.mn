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

public class UserProfileController {
    @FXML private Label displayNameLabel, usernameLabel, postCountLabel, followerCountLabel, followingCountLabel, bioLabel, profileEmojiLabel;
    @FXML private VBox userPostsContainer;
    @FXML private ImageView profileImageView;
    @FXML private Button followBtn;

    private final UserDAO userDAO = new UserDAO();
    private final PostDAO postDAO = new PostDAO();
    private final FollowDAO followDAO = new FollowDAO();
    private final LikeDAO likeDAO = new LikeDAO();
    private User user;

    @FXML
    public void initialize() {
        user = App.getViewedUser();
        displayNameLabel.setText(user.getDisplayName() != null ? user.getDisplayName() : user.getUsername());
        usernameLabel.setText("@" + user.getUsername());
        bioLabel.setText(user.getBio() != null ? user.getBio() : "");
        postCountLabel.setText(postDAO.getUserPosts(user.getId(), App.getCurrentUser().getId()).size() + " Posts");
        followerCountLabel.setText(followDAO.getFollowerCount(user.getId()) + " Followers");
        followingCountLabel.setText(followDAO.getFollowingCount(user.getId()) + " Following");
        loadProfileImage();
        updateFollowBtn();
        loadUserPosts();
    }

    private void loadProfileImage() {
        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
            File imgFile = new File(user.getProfileImage());
            if (imgFile.exists()) {
                profileImageView.setImage(new Image(imgFile.toURI().toString()));
                profileImageView.setVisible(true);
                profileImageView.setManaged(true);
                profileEmojiLabel.setVisible(false);
                profileEmojiLabel.setManaged(false);
            }
        }
    }

    private void updateFollowBtn() {
        boolean following = followDAO.isFollowing(App.getCurrentUser().getId(), user.getId());
        followBtn.setText(following ? "Unfollow" : "Follow");
        followBtn.getStyleClass().removeAll("btn-follow", "btn-unfollow");
        followBtn.getStyleClass().add(following ? "btn-unfollow" : "btn-follow");
    }

    @FXML
    private void toggleFollow() {
        followDAO.toggleFollow(App.getCurrentUser().getId(), user.getId());
        updateFollowBtn();
        followerCountLabel.setText(followDAO.getFollowerCount(user.getId()) + " Followers");
    }

    private void loadUserPosts() {
        userPostsContainer.getChildren().clear();
        List<Post> posts = postDAO.getUserPosts(user.getId(), App.getCurrentUser().getId());
        for (Post p : posts) {
            VBox card = new VBox(5);
            card.getStyleClass().add("post-card");
            card.setPadding(new Insets(10));
            Label content = new Label(p.getContent());
            content.setWrapText(true);
            Label stats = new Label("❤️ " + p.getLikeCount() + "  💬 " + p.getCommentCount());
            stats.getStyleClass().add("post-time");
            card.getChildren().addAll(content, stats);
            userPostsContainer.getChildren().add(card);
        }
    }

    @FXML
    private void goBack() { App.navigateTo("view/main.fxml"); }
}
