package com.ocir.controller;

import com.ocir.App;
import com.ocir.dao.PostDAO;
import com.ocir.model.Post;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class CreatePostController {
    @FXML private TextArea contentField;
    @FXML private Label errorLabel;
    @FXML private Label imageLabel;
    @FXML private ImageView imagePreview;

    private final PostDAO postDAO = new PostDAO();
    private String selectedImagePath;

    @FXML
    private void selectImage() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Зураг сонгох");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File file = fc.showOpenDialog(contentField.getScene().getWindow());
        if (file != null) {
            try {
                Path dir = Paths.get(System.getProperty("user.home"), ".ocir", "posts");
                Files.createDirectories(dir);
                String fileName = System.currentTimeMillis() + "_" + file.getName();
                Path dest = dir.resolve(fileName);
                Files.copy(file.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
                selectedImagePath = dest.toString();
                imageLabel.setText(file.getName());
                imagePreview.setImage(new Image(dest.toUri().toString()));
                imagePreview.setVisible(true);
                imagePreview.setManaged(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
