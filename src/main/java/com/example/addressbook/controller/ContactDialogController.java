// src/main/java/com/example/addressbook/controller/ContactDialogController.java
package com.example.addressbook.controller;

import com.example.addressbook.model.Contact;
import com.example.addressbook.util.ValidationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class ContactDialogController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneNumberField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private TextField companyField;
    @FXML private DatePicker birthDatePicker;
    @FXML private TextArea notesArea;
    @FXML private Button saveButton; // Для установки режима "только чтение"

    private Stage dialogStage;
    private Contact contact;
    private boolean saveClicked = false;
    private boolean readOnlyMode = false;

    @FXML
    private void initialize() {
        // Можно добавить слушатели для форматирования или ограничения ввода, если нужно
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setContact(Contact contact) {
        this.contact = contact;

        firstNameField.setText(contact.getFirstName());
        lastNameField.setText(contact.getLastName());
        phoneNumberField.setText(contact.getPhoneNumber());
        emailField.setText(contact.getEmail());
        addressField.setText(contact.getAddress());
        companyField.setText(contact.getCompany());
        birthDatePicker.setValue(contact.getBirthDate());
        notesArea.setText(contact.getNotes());
    }

    public void setReadOnlyMode(boolean readOnly) {
        this.readOnlyMode = readOnly;
        firstNameField.setEditable(!readOnly);
        lastNameField.setEditable(!readOnly);
        phoneNumberField.setEditable(!readOnly);
        emailField.setEditable(!readOnly);
        addressField.setEditable(!readOnly);
        companyField.setEditable(!readOnly);
        birthDatePicker.setEditable(!readOnly); // DatePicker не имеет setEditable, но можно задизейблить
        birthDatePicker.setDisable(readOnly);
        notesArea.setEditable(!readOnly);
        saveButton.setVisible(!readOnly); // Скрываем кнопку Сохранить
        if(readOnly) {
            dialogStage.setTitle("Просмотр контакта");
        }
    }


    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            contact.setFirstName(firstNameField.getText());
            contact.setLastName(lastNameField.getText());
            contact.setPhoneNumber(phoneNumberField.getText());
            contact.setEmail(emailField.getText());
            contact.setAddress(addressField.getText());
            contact.setCompany(companyField.getText());
            contact.setBirthDate(birthDatePicker.getValue());
            contact.setNotes(notesArea.getText());

            saveClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        StringBuilder errorMessage = new StringBuilder();

        if (!ValidationUtil.isValidFirstName(firstNameField.getText())) {
            errorMessage.append("Некорректное имя (от 3 до 50 символов).\n");
        }
        if (!ValidationUtil.isValidLastName(lastNameField.getText())) {
            errorMessage.append("Некорректная фамилия (до 50 символов).\n");
        }
        if (!ValidationUtil.isValidEmail(emailField.getText())) {
            errorMessage.append("Некорректный email.\n");
        }
        if (!ValidationUtil.isValidPhoneNumber(phoneNumberField.getText())) {
            errorMessage.append("Некорректный номер телефона (до 20 символов, цифры, +, -, (, )).\n");
        }
        if (!ValidationUtil.isValidAddress(addressField.getText())) {
            errorMessage.append("Некорректный адрес (до 255 символов).\n");
        }
        if (!ValidationUtil.isValidCompany(companyField.getText())) {
            errorMessage.append("Некорректное название компании (до 100 символов).\n");
        }
        if (!ValidationUtil.isValidNotes(notesArea.getText())) {
            errorMessage.append("Заметки слишком длинные (до 255 символов).\n");
        }

        // Проверка даты рождения (опционально, например, не может быть в будущем)
        if (birthDatePicker.getValue() != null && birthDatePicker.getValue().isAfter(LocalDate.now())) {
            errorMessage.append("Дата рождения не может быть в будущем.\n");
        }


        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Показать сообщение об ошибке.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Некорректные данные");
            alert.setHeaderText("Пожалуйста, исправьте некорректные поля");
            alert.setContentText(errorMessage.toString());

            alert.showAndWait();
            return false;
        }
    }
}