package org.openjfx.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import org.openjfx.connection.ServerMain;

/**
 * WelcomeController e' il controller per la schermata di benvenuto
 * dell'applicazione.
 * Gestisce l'interazione dell'utente con i campi di testo, i pulsanti e le
 * etichette presenti nella schermata.
 */
public class WelcomeController {

  // Componenti della schermata di benvenuto
  @FXML
  private TextField portField;

  @FXML
  private Button startButton;

  @FXML
  private Label statusLabel;

  /**
   * Il metodo initialize() viene chiamato automaticamente per inizializzare
   * il controller dopo che il file fxml e' stato caricato.
   * Imposta il listener per il pulsante startButton.
   */
  public void initialize() {
    // public in quanto viene richiamato automaticamente dall'FXML Loader di javafx
    // al momento del caricamento del file fxml
    startButton.setOnAction(event -> start());
  }

  /**
   * <p>
   * Quando l'utente preme il pulsante "Start", questo metodo inizializza il
   * serverSocket con la porta inserita dall'utente, qualora l'utente non la
   * inserisca sara' usata di default la porta 8080.
   * </p>
   * <p>
   * Ad operazione di inizializzazione effettuata, si occupa del caricamento della
   * scena successiva.
   * </p>
   */
  private void start() {
    Integer port = 0;
    String resp = "";

    try {
      if (portField.getText().isEmpty()) {
        port = Integer.parseInt(portField.getPromptText());
      } else {
        port = Integer.parseInt(portField.getText());
      }
      if (port < 1024 || port > 65535) {
        statusLabel.setText("Errore: inserire un numero di porta compreso tra 1024 e 65535");
      } else {
        resp = ServerMain.getInstance().openServerSocket(port); // apre il serverSocket sulla porta specificata
        try {
          // Carica la schermata di log e successivamente entra nel main loop del server
          FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/fxml/log.fxml"));
          Parent log = loader.load();
          LogController logcontroller = loader.getController();
          Stage stage = (Stage) startButton.getScene().getWindow();
          stage.setScene(new Scene(log, startButton.getScene().getWidth(), startButton.getScene().getHeight()));
          logcontroller.log(resp); // stampa il serverSocket.toString() sulla schermata di log
          ServerMain.getInstance().executeServerOperations(logcontroller); // main loop del server
        } catch (IOException e) {
          throw new FXMLLoadException();
        }
      }
    } catch (IOException e) {
      statusLabel.setText("Errore: la porta e' gia' in uso. Inserire un numero di porta compreso tra 1024 e 65535");
    } catch (NumberFormatException e) {
      statusLabel.setText("Errore: inserire un numero di porta compreso tra 1024 e 65535");
    } catch (FXMLLoadException e) {
      statusLabel.setText("Errore: problemi nel caricare la schermata di log");
    }

  }

}
