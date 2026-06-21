package com.ocir.controller;

import com.ocir.App;
import com.ocir.dao.PostDAO;
import com.ocir.model.Post;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;

public class CreatePostController {
    @FXML private TextArea contentField;
    @FXML private Label errorLabel;
    @FXML private Label imageLabel;

    private final PostDAO postDAO = new PostDAO();
    private String selectedImagePath;

    @FXML
    private void selectImage() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Зураг сонгох");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File file = fc.showOpenDialog(contentField.getScene().getWindow());
        if (file != null) {
            selectedImagePath = file.getAbsolutePath();
            imageLabel.setText(file.getName());
        }
    }

    @FXML
    private void handlePost() {
        String content = contentField.getText().trim();
        if (content.isEmpty() && selectedImagePath == null) {
            errorLabel.setText("Текст эсвэл зураг нэмнэ үү");
            return;
        }
        Post post = new Post();
        post.setUserId(App.getCurrentUser().getId());
        post.setContent(content);
        post.setImagePath(selectedImagePath);
        if (postDAO.createPost(post)) {
            App.navigateTo("view/main.fxml");
        } else {
            errorLabel.setText("Пост үүсгэхэд алдаа гарлаа");
        }
    }

    @FXML
    private void goBack() { App.navigateTo("view/main.fxml"); }
}
