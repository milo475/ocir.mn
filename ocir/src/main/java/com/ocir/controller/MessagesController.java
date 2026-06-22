package com.ocir.controller;

import com.ocir.App;
import com.ocir.dao.MessageDAO;
import com.ocir.model.Message;
import com.ocir.model.User;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.File;
import java.util.List;

public class MessagesController {
    @FXML private VBox conversationsContainer;

    private final MessageDAO messageDAO = new MessageDAO();

    @FXML
    public void initialize() {
        loadConversations();
    }

    private void loadConversations() {
        conversationsContainer.getChildren().clear();
        List<User> users = messageDAO.getConversationUsers(App.getCurrentUser().getId());
        if (users.isEmpty()) {
            Label empty = new Label("Одоогоор мессеж байхгүй байна");
            empty.setStyle("-fx-text-fill: #8e8e8e; -fx-font-size: 14;");
            conversationsContainer.getChildren().add(empty);
            return;
        }
        for (User u : users) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12));
            row.getStyleClass().add("conversation-row");
            row.setStyle("-fx-cursor: hand;");

            // Avatar
            if (u.getProfileImage() != null && !u.getProfileImage().isEmpty()) {
                File f = new File(u.getProfileImage());
                if (f.exists()) {
                    ImageView av = new ImageView(new Image(f.toURI().toString()));
                    av.setFitWidth(44);
                    av.setFitHeight(44);
                    av.setPreserveRatio(true);
                    row.getChildren().add(av);
                } else {
                    row.getChildren().add(createEmojiAvatar());
                }
            } else {
                row.getChildren().add(createEmojiAvatar());
            }

            // Name and last message
            VBox info = new VBox(3);
            HBox.setHgrow(info, Priority.ALWAYS);
            Label name = new Label(u.getDisplayName() != null ? u.getDisplayName() : u.getUsername());
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
            info.getChildren().add(name);

            Message last = messageDAO.getLastMessage(App.getCurrentUser().getId(), u.getId());
            if (last != null) {
                String preview = last.getContent().length() > 40 ? last.getContent().substring(0, 40) + "..." : last.getContent();
                Label lastMsg = new Label(preview);
                lastMsg.setStyle("-fx-text-fill: #8e8e8e; -fx-font-size: 12;");
                info.getChildren().add(lastMsg);
            }
            row.getChildren().add(info);

            row.setOnMouseClicked(e -> {
                App.setViewedUser(u);
                App.navigateTo("view/chat.fxml");
            });
            conversationsContainer.getChildren().add(row);
        }
    }

    private Label createEmojiAvatar() {
        Label l = new Label("👤");
        l.setStyle("-fx-font-size: 28;");
        return l;
    }

    @FXML
    private void goBack() { App.navigateTo("view/main.fxml"); }
}
