// src/main/java/com/example/addressbook/controller/MainViewController.java
package com.example.addressbook.controller;

import com.example.addressbook.MainApp;
import com.example.addressbook.model.Contact;
import com.example.addressbook.service.ContactService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane; // Используем AnchorPane как заглушку

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

    private MainApp mainApp;

    private static final int ITEMS_PER_PAGE = 20;
    private String currentSearchTerm = "";

    public MainViewController() {
        // Конструктор
    }

    @FXML
    private void initialize() {
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        phoneNumberColumn.setCellValueFactory(cellData -> cellData.getValue().phoneNumberProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());

        contactTable.setItems(contactData);

        contactTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showContactDetails(newValue));

        updateButtonStates(null);

        pagination.setPageFactory(this::createPage);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            currentSearchTerm = newVal;
            if (pagination.getCurrentPageIndex() != 0) {
                pagination.setCurrentPageIndex(0);
            } else {
                updatePaginationAndLoadData();
            }
        });

        // Эта строка также гарантирует, что при первом запуске пагинация корректно инициализируется
        // и вызывает createPage(0)
        updatePaginationAndLoadData();
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        // Для Alert'ов
        // Если primaryStage еще не показан при вызове initialize(),
        // то mainApp может быть null, когда showError/showAlert вызываются из updatePaginationAndLoadData.
        // Поэтому, если mainApp устанавливается здесь, можно еще раз обновить, если это необходимо
        // или передавать primaryStage в showError/showAlert другим способом.
        // В нашем случае, так как updatePaginationAndLoadData вызывается в конце initialize,
        // а setMainApp вызывается из MainApp после загрузки контроллера,
        // showError/showAlert в updatePaginationAndLoadData могут не иметь mainApp.getPrimaryStage().
        // Лучше передавать Stage напрямую в Alert или убедиться, что mainApp установлен до первого показа Alert.
        // Однако, для простоты, оставим как есть, но это потенциальное место для NullPointerException, если Alert показывается до setMainApp.
    }

    private Node createPage(int pageIndex) {
        // Загружаем данные для запрошенной страницы
        loadContactDataForPage(pageIndex);
        // Pagination требует, чтобы мы вернули Node, который будет "содержимым" страницы.
        // Поскольку наш TableView уже является частью основного макета и не должен
        // перемещаться или заменяться Pagination'ом, мы возвращаем
        // простой, невидимый или пустой Node. Это говорит Pagination, что
        // мы сами управляем отображением данных на основной сцене.
        return new AnchorPane(); // Возвращаем "заглушку"
    }

    private void updatePaginationAndLoadData() {
        try {
            int totalCount = contactService.getTotalContactsCount(currentSearchTerm);
            int pageCount = (totalCount + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE;
            if (pageCount == 0) { // Если нет результатов, отображаем 1 пустую страницу
                pageCount = 1;
            }

            // Сохраняем текущий индекс, если он допустим для нового количества страниц
            int currentPageIdx = pagination.getCurrentPageIndex();
            if (currentPageIdx >= pageCount) {
                currentPageIdx = Math.max(0, pageCount - 1);
            }

            pagination.setPageCount(pageCount); // Устанавливаем новое количество страниц

            // Если текущий индекс изменился из-за уменьшения pageCount,
            // или если мы просто хотим обновить страницу, устанавливаем его.
            // Это также вызовет createPage, если индекс действительно изменился.
            // Если индекс не изменился, createPage не будет вызван автоматически из setCurrentPageIndex,
            // поэтому мы вызываем loadContactDataForPage вручную.
            if (pagination.getCurrentPageIndex() != currentPageIdx) {
                pagination.setCurrentPageIndex(currentPageIdx);
            } else {
                // Если индекс не изменился (например, при поиске мы остались на первой странице,
                // или pageCount не изменился так, чтобы текущий индекс стал невалидным),
                // то createPage не будет вызван из setCurrentPageIndex.
                // В этом случае нужно загрузить данные для текущей страницы вручную.
                loadContactDataForPage(currentPageIdx);
            }

        } catch (SQLException e) {
            showError("Ошибка подсчета контактов", "Не удалось получить общее количество контактов: " + e.getMessage());
            pagination.setPageCount(1);
            contactData.clear();
        }
    }

    private void loadContactDataForPage(int pageIndex) {
        contactData.clear();
        try {
            List<Contact> contacts = contactService.getContacts(pageIndex, ITEMS_PER_PAGE, currentSearchTerm);
            contactData.addAll(contacts);
        } catch (SQLException e) {
            showError("Ошибка загрузки контактов", "Не удалось загрузить данные из базы: " + e.getMessage());
        }
    }

    private void showContactDetails(Contact contact) {
        updateButtonStates(contact);
    }

    private void updateButtonStates(Contact selectedContact) {
        boolean contactSelected = selectedContact != null;
        editContactButton.setDisable(!contactSelected);
        deleteContactButton.setDisable(!contactSelected);
        viewContactButton.setDisable(!contactSelected);
    }

    @FXML
    private void handleNewContact() {
        Contact tempContact = new Contact();
        boolean saveClicked = mainApp.showContactEditDialog(tempContact, "Создать контакт");
        if (saveClicked) {
            try {
                contactService.addContact(tempContact);
                updatePaginationAndLoadData();
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
                    updatePaginationAndLoadData();
                    showAlert(Alert.AlertType.INFORMATION, "Успех", "Контакт успешно обновлен.");
                } catch (SQLException e) {
                    showError("Ошибка редактирования контакта", "Не удалось обновить контакт: " + e.getMessage());
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Ничего не выбрано", "Пожалуйста, выберите контакт для редактирования.");
        }
    }

    @FXML
    private void handleDeleteContact() {
        Contact selectedContact = contactTable.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(mainApp != null ? mainApp.getPrimaryStage() : null);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Удалить контакт: " + selectedContact.getFirstName() + " " + (selectedContact.getLastName() != null ? selectedContact.getLastName() : ""));
            alert.setContentText("Вы уверены, что хотите удалить этот контакт?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    contactService.deleteContact(selectedContact.getId());
                    updatePaginationAndLoadData();
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

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        if (mainApp != null && mainApp.getPrimaryStage() != null) { // Проверка на null
            alert.initOwner(mainApp.getPrimaryStage());
        }
        alert.setTitle("Ошибка");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        if (mainApp != null && mainApp.getPrimaryStage() != null) { // Проверка на null
            alert.initOwner(mainApp.getPrimaryStage());
        }
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}