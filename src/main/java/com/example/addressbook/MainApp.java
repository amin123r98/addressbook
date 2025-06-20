// src/main/java/com/example/addressbook/MainApp.java
package com.example.addressbook;

import com.example.addressbook.controller.ContactDialogController;
import com.example.addressbook.controller.MainViewController;
import com.example.addressbook.dao.DatabaseManager;
import com.example.addressbook.model.Contact;
import javafx.application.Application;
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
    private BorderPane rootLayout;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("AddressBook App");

        // Установка иконки приложения (опционально)
        try (InputStream iconStream = MainApp.class.getResourceAsStream("/com/example/addressbook/icons/address_book_icon.png")) {
            if (iconStream != null) {
                this.primaryStage.getIcons().add(new Image(iconStream));
            } else {
                System.err.println("Иконка не найдена. Убедитесь, что /com/example/addressbook/icons/address_book_icon.png существует в ресурсах.");
            }
        } catch (Exception e) {
            System.err.println("Ошибка загрузки иконки: " + e.getMessage());
        }


        // Инициализация базы данных
        DatabaseManager.initializeDatabase();

        initRootLayout();
        showMainView();
    }

    /**
     * Инициализирует корневой макет.
     */
    public void initRootLayout() {
        try {
            // Загружаем корневой макет из fxml файла.
            // В данном примере у нас нет отдельного RootLayout.fxml,
            // MainView.fxml будет основным. Если бы был, код был бы таким:
            // FXMLLoader loader = new FXMLLoader();
            // loader.setLocation(MainApp.class.getResource("fxml/RootLayout.fxml"));
            // rootLayout = (BorderPane) loader.load();
            // Scene scene = new Scene(rootLayout);
            // primaryStage.setScene(scene);

            // Пока просто создаем сцену и показываем
            primaryStage.show();

        } catch (Exception e) { // Заменил IOException на Exception, так как FXMLLoader.load() может кидать его, но тут мы его не используем пока
            e.printStackTrace();
        }
    }

    /**
     * Показывает основное окно приложения.
     */
    public void showMainView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("fxml/MainView.fxml"));
            BorderPane mainView = (BorderPane) loader.load(); // Используем BorderPane как корневой элемент MainView

            // Даём контроллеру доступ к главному приложению.
            MainViewController controller = loader.getController();
            controller.setMainApp(this);

            Scene scene = new Scene(mainView);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Открывает диалоговое окно для редактирования деталей указанного контакта.
     * Если пользователь нажимает OK, изменения сохраняются в предоставленном объекте контакта
     * и возвращается true.
     *
     * @param contact объект контакта, который должен быть отредактирован
     * @param title Заголовок окна
     * @return true, если пользователь нажал OK, иначе false.
     */
    public boolean showContactEditDialog(Contact contact, String title) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("fxml/ContactDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            try (InputStream iconStream = MainApp.class.getResourceAsStream("/com/example/addressbook/icons/address_book_icon.png")) {
                if (iconStream != null) {
                    dialogStage.getIcons().add(new Image(iconStream));
                }
            }


            ContactDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setContact(contact);
            controller.setReadOnlyMode(false); // Убедимся, что это режим редактирования

            dialogStage.showAndWait();

            return controller.isSaveClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Открывает диалоговое окно для просмотра деталей указанного контакта.
     * @param contact объект контакта для просмотра
     */
    public void showContactViewDialog(Contact contact) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("fxml/ContactDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Просмотр контакта"); // Заголовок по умолчанию
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            try (InputStream iconStream = MainApp.class.getResourceAsStream("/com/example/addressbook/icons/address_book_icon.png")) {
                if (iconStream != null) {
                    dialogStage.getIcons().add(new Image(iconStream));
                }
            }


            ContactDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setContact(contact);
            controller.setReadOnlyMode(true); // Устанавливаем режим "только чтение"

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