package com.ocir.controller;

import com.ocir.App;
import com.ocir.dao.MessageDAO;
import com.ocir.dao.NotificationDAO;
import com.ocir.model.Message;
import com.ocir.model.User;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.File;
import java.util.List;

public class ChatController {
    @FXML private Label chatUserName;
    @FXML private ImageView chatUserImage;
    @FXML private VBox messagesContainer;
    @FXML private TextField messageInput;
    @FXML private ScrollPane chatScrollPane;

    private final MessageDAO messageDAO = new MessageDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private User chatUser;

    @FXML
    public void initialize() {
        chatUser = App.getViewedUser();
        chatUserName.setText(chatUser.getDisplayName() != null ? chatUser.getDisplayName() : chatUser.getUsername());
        if (chatUser.getProfileImage() != null && !chatUser.getProfileImage().isEmpty()) {
            File f = new File(chatUser.getProfileImage());
            if (f.exists()) {
                chatUserImage.setImage(new Image(f.toURI().toString()));
                chatUserImage.setVisible(true);
                chatUserImage.setManaged(true);
            }
        }
        loadMessages();
    }

    private void loadMessages() {
        messagesContainer.getChildren().clear();
        List<Message> messages = messageDAO.getConversation(App.getCurrentUser().getId(), chatUser.getId());
        for (Message m : messages) {
            boolean isMine = m.getSenderId() == App.getCurrentUser().getId();
            HBox row = new HBox();
            row.setAlignment(isMine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            row.setPadding(new Insets(2, 0, 2, 0));

            Label bubble = new Label(m.getContent());
            bubble.setWrapText(true);
            bubble.setMaxWidth(400);
            bubble.setPadding(new Insets(8, 12, 8, 12));
            bubble.getStyleClass().add(isMine ? "chat-bubble-mine" : "chat-bubble-other");

            row.getChildren().add(bubble);
            messagesContainer.getChildren().add(row);
        }
        chatScrollPane.layout();
        chatScrollPane.setVvalue(1.0);
    }

    @FXML
    private void sendMessage() {
        String text = messageInput.getText().trim();
        if (!text.isEmpty()) {
            messageDAO.sendMessage(App.getCurrentUser().getId(), chatUser.getId(), text);
            notificationDAO.createNotification(chatUser.getId(), App.getCurrentUser().getId(), "MESSAGE", null);
            messageInput.clear();
            loadMessages();
        }
    }

    @FXML
    private void goBack() { App.navigateTo("view/messages.fxml"); }
}
