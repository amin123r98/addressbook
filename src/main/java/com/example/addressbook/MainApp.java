// src/main/java/com/example/addressbook/MainApp.java
package com.example.addressbook;

import com.example.addressbook.controller.ContactDialogController;
import com.example.addressbook.controller.MainViewController;
import com.example.addressbook.dao.DatabaseManager;
import com.example.addressbook.model.Contact;
import javafx.application.Application; // Убедитесь, что этот импорт есть
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class MainApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("AddressBook App");

        // ... загрузка иконки ...

        DatabaseManager.initializeDatabase();
        // initRootLayout(); // Можно убрать, если primaryStage.show() только в showMainView
        showMainView();
    }

    // initRootLayout() можно удалить, если он пуст или делает только show()

    public void showMainView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            // Путь к FXML остается прежним, так как он в пакетной структуре
            loader.setLocation(MainApp.class.getResource("fxml/MainView.fxml"));
            BorderPane mainView = (BorderPane) loader.load();

            MainViewController controller = loader.getController();
            controller.setMainApp(this);

            Scene scene = new Scene(mainView);

            // --- ПОДКЛЮЧАЕМ СТИЛИ (НОВЫЙ ПУТЬ) ---
            try {
                String cssPath = MainApp.class.getResource("/css/styles.css").toExternalForm(); // ИЗМЕНЕН ПУТЬ
                scene.getStylesheets().add(cssPath);
            } catch (NullPointerException e) {
                System.err.println("Не удалось загрузить CSS файл: styles.css. Убедитесь, что он находится в src/main/resources/css/");
                e.printStackTrace();
            }
            // --- КОНЕЦ ПОДКЛЮЧЕНИЯ СТИЛЕЙ ---

            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean showContactEditDialog(Contact contact, String title) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("fxml/ContactDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Scene dialogScene = new Scene(page);
            // --- ПОДКЛЮЧАЕМ СТИЛИ К ДИАЛОГУ (НОВЫЙ ПУТЬ) ---
            try {
                String cssPath = MainApp.class.getResource("/css/styles.css").toExternalForm(); // ИЗМЕНЕН ПУТЬ
                dialogScene.getStylesheets().add(cssPath);
            } catch (NullPointerException e) {
                System.err.println("Не удалось загрузить CSS для диалога: styles.css.");
            }
            // --- КОНЕЦ ПОДКЛЮЧЕНИЯ СТИЛЕЙ К ДИАЛОГУ ---

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.setScene(dialogScene);

            // ... загрузка иконки диалога ...

            ContactDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setContact(contact);
            controller.setReadOnlyMode(false);

            dialogStage.showAndWait();

            return controller.isSaveClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void showContactViewDialog(Contact contact) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("fxml/ContactDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Scene dialogScene = new Scene(page);
            // --- ПОДКЛЮЧАЕМ СТИЛИ К ДИАЛОГУ (НОВЫЙ ПУТЬ) ---
            try {
                String cssPath = MainApp.class.getResource("/css/styles.css").toExternalForm(); // ИЗМЕНЕН ПУТЬ
                dialogScene.getStylesheets().add(cssPath);
            } catch (NullPointerException e) {
                System.err.println("Не удалось загрузить CSS для диалога просмотра: styles.css.");
            }
            // --- КОНЕЦ ПОДКЛЮЧЕНИЯ СТИЛЕЙ К ДИАЛОГУ ---

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Просмотр контакта");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.setScene(dialogScene);

            // ... загрузка иконки диалога ...

            ContactDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setContact(contact);
            controller.setReadOnlyMode(true);

            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
