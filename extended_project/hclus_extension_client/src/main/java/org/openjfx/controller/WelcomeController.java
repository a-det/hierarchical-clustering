package org.openjfx.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.text.Text;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextField;
import org.openjfx.connection.ClientOperations;

import java.io.IOException;

/**
 * WelcomeController e' il controller per la schermata della connessione con il
 * server.
 * Gestisce l'interazione dell'utente con i campi di testo, i pulsanti e le
 * etichette presenti nella schermata.
 */
public class WelcomeController {

  // Componenti della schermata
  @FXML
  private ImageView logo;
  @FXML
  private Text descriptionText;
  @FXML
  private Button inizia;
  @FXML
  private TextField ipField;
  @FXML
  private TextField portField;
  @FXML
  private Label alertText;
  @FXML
  private Text welcomeText;

  /**
   * Il metodo initialize() viene chiamato automaticamente per inizializzare
   * il controller dopo che il file fxml e' stato caricato.
   * Imposta il listener per il pulsante di start.
   */
  @FXML
  private void initialize() {
    // Imposta il logo
    Image logoImage = new Image("file:src/main/resources/org/openjfx/img/icon.png");
    logo.setImage(logoImage);

    inizia.setOnAction(event -> startApplication());
  }

  /**
   * Tenta di stabilire la connessione con il server e, in caso di successo,
   * provvede al cambio di scena.
   */
  private void startApplication() {
    try {
      Integer port = 0;

      String ip = ipField.getText();
      if (ip.equals("")) {
        ip = ipField.getPromptText().split(":")[1];
      }

      if (portField.getText().equals("")) {
        port = Integer.parseInt(portField.getPromptText().split(":")[1]);
      } else {
        port = Integer.parseInt(portField.getText());
      }

      ClientOperations.getInstance().clientInizialization(ip, port);
      alertText.setText("Connessione effettuata con successo");

      try { // Cambio scena
        Parent accessDBRoot = FXMLLoader.load(getClass().getResource("/org/openjfx/fxml/DBAccess.fxml"));
        Stage stage = (Stage) inizia.getScene().getWindow();
        stage.setScene(new Scene(accessDBRoot, inizia.getScene().getWidth(), inizia.getScene().getHeight()));
      } catch (IOException e) {
        alertText.setText("Errore durante il cambio di scena");
      }

    } catch (IOException e) {
      alertText.setText("Errore di connessione al server");
    }
  }

}
