// src/main/java/com/example/addressbook/controller/MainViewController.java
package com.example.addressbook.controller;

import com.example.addressbook.MainApp;
import com.example.addressbook.model.Contact;
import com.example.addressbook.service.ContactService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MainViewController {

    @FXML private TableView<Contact> contactTable;
    @FXML private TableColumn<Contact, String> firstNameColumn;
    @FXML private TableColumn<Contact, String> lastNameColumn;
    @FXML private TableColumn<Contact, String> phoneNumberColumn;
    @FXML private TableColumn<Contact, String> emailColumn;

    @FXML private TextField searchField;
    @FXML private Pagination pagination;

    @FXML private Button newContactButton;
    @FXML private Button editContactButton;
    @FXML private Button deleteContactButton;
    @FXML private Button viewContactButton;


    private final ContactService contactService = new ContactService();
    private ObservableList<Contact> contactData = FXCollections.observableArrayList();

    private MainApp mainApp; // Ссылка на главный класс приложения

    private static final int ITEMS_PER_PAGE = 20; // FR1.1.2

    public MainViewController() {
        // Конструктор вызывается до FXML инициализации
    }

    @FXML
    private void initialize() {
        // Инициализация колонок таблицы
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        phoneNumberColumn.setCellValueFactory(cellData -> cellData.getValue().phoneNumberProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());

        // Начальная загрузка данных
        loadContactData();

        // Настройка слушателя для выбора в таблице
        contactTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showContactDetails(newValue));

        // По умолчанию кнопки редактирования/удаления неактивны
        updateButtonStates(null);

        // TODO: Настройка пагинации
        // pagination.setPageFactory(this::createPage);
        // TODO: Настройка поиска
        // searchField.textProperty().addListener((obs, oldVal, newVal) -> handleSearch());

        // TODO: Настройка сортировки (пока только по умолчанию через клик по заголовку)
        // TableView поддерживает сортировку "из коробки", если PropertyValueFactory используется.
        // Для сложной серверной сортировки нужно будет переопределять.
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        // После установки mainApp, можно загрузить данные или обновить UI
        loadContactData();
    }

    private void loadContactData() {
        contactData.clear();
        try {
            List<Contact> contacts = contactService.getAllContacts(); // Пока без пагинации
            contactData.addAll(contacts);
            contactTable.setItems(contactData);
        } catch (SQLException e) {
            showError("Ошибка загрузки контактов", "Не удалось загрузить данные из базы: " + e.getMessage());
        }
    }

    private void showContactDetails(Contact contact) {
        updateButtonStates(contact);
        // В будущем здесь можно отображать детали в отдельной панели, если потребуется
    }

    private void updateButtonStates(Contact selectedContact) {
        boolean contactSelected = selectedContact != null;
        editContactButton.setDisable(!contactSelected);
        deleteContactButton.setDisable(!contactSelected);
        viewContactButton.setDisable(!contactSelected);
    }

    @FXML
    private void handleNewContact() {
        Contact tempContact = new Contact(); // Пустой контакт для создания
        boolean saveClicked = mainApp.showContactEditDialog(tempContact, "Создать контакт");
        if (saveClicked) {
            try {
                contactService.addContact(tempContact);
                loadContactData(); // Обновить таблицу
                showAlert(Alert.AlertType.INFORMATION, "Успех", "Контакт успешно создан.");
            } catch (SQLException e) {
                showError("Ошибка создания контакта", "Не удалось сохранить контакт: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleEditContact() {
        Contact selectedContact = contactTable.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            boolean saveClicked = mainApp.showContactEditDialog(selectedContact, "Редактировать контакт");
            if (saveClicked) {
                try {
                    contactService.updateContact(selectedContact);
                    loadContactData(); // Обновить таблицу
                    showAlert(Alert.AlertType.INFORMATION, "Успех", "Контакт успешно обновлен.");
                } catch (SQLException e) {
                    showError("Ошибка редактирования контакта", "Не удалось обновить контакт: " + e.getMessage());
                }
            }
        } else {
            // Этого не должно произойти, если кнопка задизейблена правильно
            showAlert(Alert.AlertType.WARNING, "Ничего не выбрано", "Пожалуйста, выберите контакт для редактирования.");
        }
    }

    @FXML
    private void handleDeleteContact() {
        Contact selectedContact = contactTable.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Удалить контакт: " + selectedContact.getFirstName() + " " + (selectedContact.getLastName() != null ? selectedContact.getLastName() : ""));
            alert.setContentText("Вы уверены, что хотите удалить этот контакт?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    contactService.deleteContact(selectedContact.getId());
                    loadContactData(); // Обновить таблицу
                    showAlert(Alert.AlertType.INFORMATION, "Успех", "Контакт успешно удален.");
                } catch (SQLException e) {
                    showError("Ошибка удаления контакта", "Не удалось удалить контакт: " + e.getMessage());
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Ничего не выбрано", "Пожалуйста, выберите контакт для удаления.");
        }
    }

    @FXML
    private void handleViewContact() {
        Contact selectedContact = contactTable.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            mainApp.showContactViewDialog(selectedContact);
        } else {
            showAlert(Alert.AlertType.WARNING, "Ничего не выбрано", "Пожалуйста, выберите контакт для просмотра.");
        }
    }

    // TODO: handleSearch()
    // TODO: createPage(int pageIndex) для пагинации

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}