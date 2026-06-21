package com.ocir.controller;

import com.ocir.App;
import com.ocir.dao.*;
import com.ocir.model.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class ProfileController {
    @FXML private Label displayNameLabel, usernameLabel, postCountLabel, followerCountLabel, followingCountLabel, bioLabel;
    @FXML private VBox editSection, userPostsContainer, followersContainer, followingContainer;
    @FXML private TextField editNameField;
    @FXML private TextArea editBioField;
    @FXML private Button editBtn;

    private final UserDAO userDAO = new UserDAO();
    private final PostDAO postDAO = new PostDAO();
    private final FollowDAO followDAO = new FollowDAO();
    private final LikeDAO likeDAO = new LikeDAO();

    @FXML
    public void initialize() {
        User user = App.getCurrentUser();
        displayNameLabel.setText(user.getDisplayName() != null ? user.getDisplayName() : user.getUsername());
        usernameLabel.setText("@" + user.getUsername());
        bioLabel.setText(user.getBio() != null ? user.getBio() : "");
        postCountLabel.setText(postDAO.getUserPosts(user.getId(), user.getId()).size() + " Posts");
        followerCountLabel.setText(followDAO.getFollowerCount(user.getId()) + " Followers");
        followingCountLabel.setText(followDAO.getFollowingCount(user.getId()) + " Following");
        loadUserPosts();
        loadFollowLists();
    }

    private void loadUserPosts() {
        userPostsContainer.getChildren().clear();
        List<Post> posts = postDAO.getUserPosts(App.getCurrentUser().getId(), App.getCurrentUser().getId());
        for (Post p : posts) {
            VBox card = new VBox(5);
            card.getStyleClass().add("post-card");
            card.setPadding(new Insets(10));
            Label content = new Label(p.getContent());
            content.setWrapText(true);
            Label stats = new Label("❤️ " + p.getLikeCount() + "  💬 " + p.getCommentCount());
            stats.getStyleClass().add("post-time");
            HBox actions = new HBox(10);
            Button del = new Button("Устгах");
            del.getStyleClass().add("btn-delete");
            del.setOnAction(e -> { postDAO.deletePost(p.getId(), App.getCurrentUser().getId()); loadUserPosts(); });
            actions.getChildren().add(del);
            card.getChildren().addAll(content, stats, actions);
            userPostsContainer.getChildren().add(card);
        }
    }

    private void loadFollowLists() {
        followersContainer.getChildren().clear();
        followingContainer.getChildren().clear();
        for (User u : followDAO.getFollowers(App.getCurrentUser().getId())) {
            followersContainer.getChildren().add(new Label(u.getDisplayName() != null ? u.getDisplayName() : u.getUsername()));
        }
        for (User u : followDAO.getFollowing(App.getCurrentUser().getId())) {
            followingContainer.getChildren().add(new Label(u.getDisplayName() != null ? u.getDisplayName() : u.getUsername()));
        }
    }

    @FXML
    private void toggleEdit() {
        boolean show = !editSection.isVisible();
        editSection.setVisible(show);
        editSection.setManaged(show);
        if (show) {
            User user = App.getCurrentUser();
            editNameField.setText(user.getDisplayName() != null ? user.getDisplayName() : "");
            editBioField.setText(user.getBio() != null ? user.getBio() : "");
        }
    }

    @FXML
    private void saveProfile() {
        User user = App.getCurrentUser();
        user.setDisplayName(editNameField.getText().trim());
        user.setBio(editBioField.getText().trim());
        userDAO.updateProfile(user);
        editSection.setVisible(false);
        editSection.setManaged(false);
        displayNameLabel.setText(user.getDisplayName());
        bioLabel.setText(user.getBio());
    }

    @FXML
    private void goBack() { App.navigateTo("view/main.fxml"); }
}
