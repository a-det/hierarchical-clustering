package org.openjfx.controller;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import org.openjfx.connection.ClientOperations;

import java.io.IOException;


/**
 * DBController e' il controller per la schermata della connessione al database.
 * Gestisce l'interazione dell'utente con i campi di testo, i pulsanti e le
 * etichette presenti nella schermata.
 */
public class DBController {

  // Componenti della schermata 
  @FXML
  private ImageView logo;
  @FXML
  private TextField userField;
  @FXML
  private PasswordField passField;
  @FXML
  private Button connectionButton;
  @FXML
  private TextField DB;
  @FXML
  private TextField tableName;
  @FXML
  private Label connStatus;

  /**
   * Il metodo initialize() viene chiamato automaticamente per inizializzare
   * il controller dopo che il file fxml e' stato caricato.
   * Imposta i listener per il pulsante di connessione.
   */
  @FXML
  private void initialize() {
    connectionButton.setOnAction(event -> dbConnection());
  }

  /*
   * Tenta di stabilire la connessione con il database e, in caso di successo,
   * provvede al cambio di scena.
   */
  private void dbConnection() {

    String db = DB.getText();
    if (db.equals("")) {
      db = DB.getPromptText();
    }

    String username = userField.getText();
    if (username.equals("")) {
      username = userField.getPromptText();
    }

    String table = tableName.getText();
    if (table.equals("")) {
      table = tableName.getPromptText();
    }

    String password = passField.getText();
    
    connStatus.setText(ClientOperations.getInstance().initDBConnection(db, username, password, table));
    if (connStatus.getText().equals("Dati tabella caricati correttamente")) {
      try { // Cambio scena
        Parent menu = FXMLLoader.load(getClass().getResource("/org/openjfx/fxml/menu.fxml"));
        Stage stage = (Stage) connStatus.getScene().getWindow();
        stage.setScene(new Scene(menu, connStatus.getScene().getWidth(), connStatus.getScene().getHeight()));
        stage.getScene().setUserData("menu");
      } catch (IOException e) {
        connStatus.setText("Errore durante il cambio di scena FXML");
      }
    }

  }
}